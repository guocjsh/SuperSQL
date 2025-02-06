package com.aispace.supersql.prompt;

import com.aispace.supersql.builder.SqlpromptBuilder;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;

public class SqlAssistantPrompt {

    private static final Logger logger = LoggerFactory.getLogger(SqlAssistantPrompt.class);

    private static final String INITIAL_PROMPT = """
            ### Goal
            You are a SQL expert.
            Please help to generate a SQL query to answer the question. Your response should ONLY be based on the given context and follow the response guidelines and format instructions. 
            """;
    private static final String RESPONSE_GUIDELINES = """
            ###Response Guidelines
            1. If the provided context is sufficient, please generate a valid SQL query without any explanations for the question.
            2. If the provided context is almost sufficient but requires knowledge of a specific string in a particular column, please generate an intermediate SQL query to find the distinct strings in that column. Prepend the query with a comment saying intermediate_sql
            3. If the provided context is insufficient, please explain why it can't be generated.
            4. Please use the most relevant table(s).
            5. If the question has been asked and answered before, please repeat the answer exactly as it was given before.
            6. Ensure that the output SQL is SQL-compliant and executable, and free of syntax errors.
            """;

    private static final int MAX_LENGTH = 14000;

    public static Prompt getSqlPrompt(SqlpromptBuilder sqlprompt) {
        if (sqlprompt == null) {
            throw new IllegalArgumentException("Sqlprompt cannot be null");
        }
        String initialPrompt = addDdlToPrompt(INITIAL_PROMPT, sqlprompt.getDdlList(), MAX_LENGTH);
        initialPrompt = addDocumentationToPrompt(initialPrompt, sqlprompt.getDocumentList(), MAX_LENGTH);
        initialPrompt += RESPONSE_GUIDELINES;

        return generateMessageLog(initialPrompt, sqlprompt.getQuestionSqlList(), sqlprompt.getQuestion());
    }

   private static Prompt generateMessageLog(String initialPrompt, List<Document> questionSqlList, String question) {
    if (question == null || question.isEmpty()) {
        throw new IllegalArgumentException("Question cannot be null or empty");
    }

    Message systemMessage = new SystemMessage(initialPrompt);
    Message userMessage = new UserMessage(question);

    List<Message> messages = new ArrayList<>(List.of(systemMessage));
    if (questionSqlList.isEmpty()) {
        logger.warn("questionSqlList is empty, no example messages will be added.");
    }else {
        for (Document example : questionSqlList) {
            if (example == null) {
                logger.warn("Example document is null, skipping this entry.");
                continue;
            }
            try {
                JSONObject object = JSONObject.parseObject(example.getText());
                if (object == null) {
                    logger.warn("Failed to parse JSON from document: {}", example);
                    continue;
                }
                String sql = object.getString("sql");
                String docQuestion = object.getString("question");
                if (sql == null || docQuestion == null) {
                    logger.warn("Missing required keys 'sql' or 'question' in the object: {}", object);
                    continue;
                }
                messages.add(new AssistantMessage(sql));
                messages.add(new UserMessage(docQuestion));
            } catch (Exception e) {
                logger.warn("Error processing document: {}", example, e);
            }
        }
    }
    messages.add(userMessage);
    return new Prompt(messages);
}


    private static String addDdlToPrompt(String initialPrompt, List<Document> ddlList, int maxTokens) {
        validateInput(initialPrompt, ddlList, maxTokens);
        StringBuilder promptBuilder = new StringBuilder(initialPrompt);
        if (!ddlList.isEmpty()) {
            promptBuilder.append("\n###Tables \n");
            int currentTokenCount = strToApproxTokenCount(promptBuilder.toString());
            for (Document ddl : ddlList) {
                try {
                    int ddlTokenCount = strToApproxTokenCount(ddl.getText());
                    if (currentTokenCount + ddlTokenCount < maxTokens) {
                        promptBuilder.append(ddl.getText()).append("\n\n");
                        currentTokenCount += ddlTokenCount + 2; // 加上两个换行符的 token 数
                    }
                } catch (Exception e) {
                    logger.error("Error calculating token count", e);
                }
            }
        }

        return promptBuilder.toString();
    }

    private static String addDocumentationToPrompt(String initialPrompt, List<Document> documentationList, int maxTokens) {
        validateInput(initialPrompt, documentationList, maxTokens);
        StringBuilder promptBuilder = new StringBuilder(initialPrompt);
        int currentTokenCount = strToApproxTokenCount(initialPrompt);
        if (!documentationList.isEmpty()) {
            promptBuilder.append("\n###Additional Context \n\n");
            currentTokenCount += 2; // 加上两个换行符的 token 数
            for (Document documentation : documentationList) {
                try {
                    int docTokenCount = strToApproxTokenCount(documentation.getText());
                    if (currentTokenCount + docTokenCount < maxTokens) {
                        promptBuilder.append(documentation.getText()).append("\n\n");
                        currentTokenCount += docTokenCount + 2; // 加上两个换行符的 token 数
                    }
                } catch (Exception e) {
                    logger.error("Error calculating token count", e);
                }
            }
        }

        return promptBuilder.toString();
    }

    private static void validateInput(String initialPrompt, List<Document> list, int maxTokens) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }
        if (initialPrompt == null || initialPrompt.isEmpty()) {
            throw new IllegalArgumentException("Initial prompt cannot be null or empty");
        }
        if (maxTokens <= 0) {
            throw new IllegalArgumentException("Max tokens must be a positive integer");
        }
    }

    private static int strToApproxTokenCount(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        // 更精确的 token 计算逻辑
        return str.length() / 4;
    }
}
