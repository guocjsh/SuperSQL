package com.aispace.supersql.engine;

import cn.hutool.core.util.StrUtil;
import com.aispace.supersql.builder.FollowupQuestionsBuilder;
import com.aispace.supersql.builder.SqlpromptBuilder;
import com.aispace.supersql.builder.TrainBuilder;
import com.aispace.supersql.enumd.TrainPolicyType;
import com.aispace.supersql.model.ChatClientFactory;
import com.aispace.supersql.util.SqlExtractorUtils;
import com.aispace.supersql.util.TextProcessor;
import com.aispace.supersql.vector.BaseVectorStore;
import com.aispace.supersql.prompt.SqlAssistantPrompt;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author chengjie.guo
 */
@Slf4j
public abstract class AbstractSqlEngine implements SqlEngine {

    protected BaseVectorStore vectorStore;

    protected RagEngine ragEngine;

    protected ChatModel chatModel;

    @PostConstruct
    public void initialize() {
        try {
            builder();
            log.info("Initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize: {}", e.getMessage(), e);
            // 可以根据具体需求决定是否重新抛出异常或采取其他措施
            throw new RuntimeException("Initialization failed", e);
        }
    }

    protected abstract void builder();

    /**
     * 根据标签搜索向量
     * <p>
     * 本方法旨在通过指定的问题文本和训练策略类型来搜索相关的文档向量
     * 它首先构建一个过滤表达式，以匹配具有相同脚本类型的文档，然后对搜索结果进行过滤，
     * 仅返回得分高于预定阈值的文档
     *
     * @param question 问题文本，用于搜索相关文档
     * @param type     训练策略类型，用于过滤文档
     * @return 返回一个文档列表，这些文档与给定问题相关，并且得分高于阈值
     */
    private List<Document> searchVectorByTag(String question, TrainPolicyType type) {
        try {
            FilterExpressionBuilder expression = new FilterExpressionBuilder();
            List<Document> documents = this.vectorStore.searchByTag(
                    question,
                    expression.eq("script_type", type.name()).build(),
                    10);
            return documents.stream()
                    .filter(doc -> doc.getScore() >= 0.4)
                    .collect(Collectors.toList());
//            return documents;
        } catch (Exception e) {
            // 记录日志并返回空列表，具体处理方式根据业务需求调整
            System.err.println("Error searching documents by type " + type + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Flux<ChatResponse> generateSummary(String question, String context) {
        PromptTemplate sysTemplate = new PromptTemplate("""
                You are a helpful data assistant. The user asked the question: \n
                '{question}'\n
                The following is a pandas DataFrame with the results of the query: \n
                {context}\n
                Please reply in MarkDown format\n
                    """);
        Prompt sysPrompt = sysTemplate.create(Map.of(
                "question", getOrDefault(question, "No question provided"),
                "context", getOrDefault(context, "No data available")
        ));
        Message systemMessage = new SystemMessage(sysPrompt.getContents());
        PromptTemplate userTemplate = new PromptTemplate("""
                Briefly summarize the data based on the question that was asked. Do not respond with any additional explanation beyond the summary.
                """);
        Message userMessage = new SystemMessage(userTemplate.create().getContents());
        List<Message> messages = new ArrayList<>(List.of(systemMessage,userMessage));
        Prompt prompt = new Prompt(messages);
        ChatClient.StreamResponseSpec stream = ChatClientFactory.buildChatClient(this.chatModel).prompt(prompt).stream();
        return stream.chatResponse();
    }

    @Override
    public String generateSql(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }
        FilterExpressionBuilder expression = new FilterExpressionBuilder();
        List<Document> questionSqlList = this.searchVectorByTag(question, TrainPolicyType.SQL);
        List<Document> ddlList = this.searchVectorByTag(question, TrainPolicyType.DDL);
        List<Document> documentList = this.searchVectorByTag(question, TrainPolicyType.DOCUMENTATION);
        SqlpromptBuilder sqlprompt = SqlpromptBuilder.builder().question(question).questionSqlList(questionSqlList).ddlList(ddlList).documentList(documentList).build();
        Prompt prompt = SqlAssistantPrompt.getSqlPrompt(sqlprompt);
        log.info("Generating SQL Prompt for first:\n {}", prompt.getContents());
        String llmResponse = ChatClientFactory.buildChatClient(this.chatModel).prompt(prompt).call().content();
        log.info("Generating SQL From LLM {}", llmResponse);
        if (llmResponse.contains("intermediate_sql")) {
            String intermediateSql = SqlExtractorUtils.extractSql(llmResponse);
            List<Map<String, Object>> executed = executeSql(intermediateSql);
            sqlprompt.getDocumentList().add(
                    new Document(String.format("""
                        The following is a pandas DataFrame with the results of the intermediate SQL query %s:\\n%s
                        """,intermediateSql, executed.toString()
                    )));
            prompt = SqlAssistantPrompt.getSqlPrompt(sqlprompt);
            llmResponse = ChatClientFactory.buildChatClient(this.chatModel).prompt(prompt).call().content();
        }
        String sql = SqlExtractorUtils.extractSql(llmResponse);
        return validSql(sql) ? sql : null;
    }

    @Override
    public List<String> followupQuestions(FollowupQuestionsBuilder questionsBuilder) {
        if (questionsBuilder == null) {
            log.error("Questions builder cannot be null");
            return null;
        }
        try {
            // 获取并验证 SQL 查询
            String sql = sanitizeSql(questionsBuilder.getSql());
            // 构建提示
            PromptTemplate sysTemplate = new PromptTemplate("""
                    You are a helpful data assistant. The user asked the question: '{question}' \n
                    The SQL query for this question was: '{sql}' \n
                    The following is data with the results of the query: \n
                    '{data}' \n
                    """);
            Prompt sysPrompt = sysTemplate.create(Map.of(
                    "question", getOrDefault(questionsBuilder.getQuestion(), "No question provided"),
                    "sql", getOrDefault(sql, "No SQL provided"),
                    "data", getOrDefault(questionsBuilder.getResult().toString(), "No data available")
            ));
            Message systemMessage = new SystemMessage(sysPrompt.getContents());

            PromptTemplate userTemplate=new PromptTemplate("""
                    Generate a list of '{questionsNum}' followup questions that the user might ask about this data. Respond with a list of questions, one per line. Do not answer with any explanations -- just the questions. Remember that there should be an unambiguous SQL query that can be generated from the question. Prefer questions that are answerable outside of the context of this conversation. Prefer questions that are slight modifications of the SQL query that was generated that allow digging deeper into the data. Each question will be turned into a button that the user can click to generate a new SQL query so don't use 'example' type questions. Each question must have a one-to-one correspondence with an instantiated SQL query.
                    Respond in the chinese language.
                    """);
            Prompt userPrompt = userTemplate.create(Map.of(
                    "questionsNum", questionsBuilder.getQuestionsNum()
            ));
            Message userMessage = new SystemMessage(userPrompt.getContents());
            List<Message> messages = new ArrayList<>(List.of(systemMessage,userMessage));
            Prompt prompt = new Prompt(messages);
            String llmResponse = ChatClientFactory.buildChatClient(this.chatModel).prompt(prompt).call().content();

            return analyzeLlmResponse(llmResponse);
        }catch (Exception e) {
            log.error("Failed to generate follow-up questions: {}", e.getMessage());
            return null;
        }
    }

    // 验证和清理 SQL 查询
    private static String sanitizeSql(String sql) {
        // 这里可以添加更复杂的 SQL 清理逻辑
        if (sql == null || sql.trim().isEmpty()) {
            return "Invalid SQL";
        }
        return sql;
    }

    private List<String> analyzeLlmResponse(String llmResponse) {
        if (StrUtil.isBlank(llmResponse)) {
            throw new IllegalArgumentException("LLM response is empty or invalid.");
        }
        //llmResponse=TextProcessor.removeTags(llmResponse);
        Pattern pattern = Pattern.compile("^\\d+\\.\\s*", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(llmResponse);
        String numbersRemoved = matcher.replaceAll("");

        List<String> questions = new ArrayList<>();
        for (String q : numbersRemoved.split("\n")) {
            String trimmedQuestion = q.trim();
            if (!trimmedQuestion.isEmpty()) {
                questions.add(trimmedQuestion);
            }
        }
        return questions;
    }

    // 获取默认值
    private static String getOrDefault(String value, String defaultValue) {
        return Optional.ofNullable(value).orElse(defaultValue);
    }


    @Override
    public String rewrittenQuestion(String lastQuestion, String newQuestion) {
        if (lastQuestion == null || lastQuestion.trim().isEmpty()) {
            log.error("Last question cannot be null or empty");
            return null;
        }
        String sysPrompt = """
                    Your goal is to combine a sequence of questions into a singular question if they are related. If the second question does not relate to the first question and is fully self-contained, return the second question. Return just the new combined question with no additional explanations. The question should theoretically be answerable with a single SQL statement.
                    """;
        Message systemMessage = new SystemMessage(sysPrompt);

        PromptTemplate userTemplate=new PromptTemplate("""
                    First question:: '{lastQuestion}' \n
                    Second question: '{newQuestion}' \n
                    """);
        Prompt userPrompt = userTemplate.create(Map.of(
                "lastQuestion", lastQuestion,
                "newQuestion",newQuestion
        ));
        Message userMessage = new SystemMessage(userPrompt.getContents());
        List<Message> messages = new ArrayList<>(List.of(systemMessage,userMessage));
        Prompt prompt = new Prompt(messages);
        String content = ChatClientFactory.buildChatClient(this.chatModel).prompt(prompt).call().content();
        return content;//TextProcessor.removeTags(content);
    }



    @Override
    public Boolean validSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            log.error("SQL cannot be null or empty");
            return false;
        }
        return true;
    }

    /**
     * 执行给定的 SQL 查询。
     * 此方法接收一个字符串格式的 SQL 查询，并执行它。
     *
     * @param sql 要执行的 SQL 查询。
     * @param sql The SQL query to execute.
     * @return 执行 SQL 查询的结果。
     * <p>
     * Executes a given SQL query.
     * This method takes a SQL query in string format and executes it.
     * @return The result of executing the SQL query.
     */
    protected abstract List<Map<String, Object>> executeSql(String sql);

    @Override
    public void train(TrainBuilder train) {
        if (train == null) {
            throw new IllegalArgumentException("TrainDao cannot be null");
        }
        try {
            TrainPolicyType policy = train.getPolicy();
            switch (policy.name()) {
                case "DDL":
                    ragEngine.addDdl(train.getContent());
                    break;
                case "SQL":
                    // Check if the training question is null or empty
                    // If the question is null or only contains whitespace, a question needs to be generated
                    if (train.getQuestion() == null || train.getQuestion().trim().isEmpty()) {
                        // Generate a question based on the content
                        // The generateQuestion function is responsible for generating a suitable question based on the content
                        String question = generateQuestion(train.getContent());
                        // Set the generated question to the training question
                        train.setQuestion(question);
                    }
                    ragEngine.addQuestionSql(train.getQuestion(), train.getContent().toUpperCase());
                    break;
                case "DOCUMENTATION":
                    ragEngine.addDocumentation(train.getContent());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid train policy: " + policy);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during training process: " + e);
        }
    }


    private String generateQuestion(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL query cannot be null or empty");
        }
        try {
            String sysPrompt = "The user will give you SQL and you will try to guess what the business question this query is answering. Return just the question without any additional explanation. Do not reference the table name in the question.";
            Message userMessage = new UserMessage(sql);
            Message systemMessage = new SystemMessage(sysPrompt);
            List<Message> messages = new ArrayList<>(List.of(systemMessage, userMessage));
            Prompt prompt = new Prompt(messages);
            String content = ChatClientFactory.buildChatClient(this.chatModel).prompt(prompt).call().content();
            return content;//TextProcessor.removeTags(content);
        } catch (Exception e) {
            // Log the exception details
            throw new RuntimeException("Failed to generate question from SQL: " + e.getMessage(), e);
        }
    }
}
