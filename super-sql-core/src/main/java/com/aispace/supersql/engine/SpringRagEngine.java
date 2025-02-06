package com.aispace.supersql.engine;

import com.aispace.supersql.enumd.TrainPolicyType;
import com.aispace.supersql.vector.BaseVectorStore;
import com.aispace.supersql.vector.SpringVectorStore;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author chengjie.guo
 */
@Slf4j
public class SpringRagEngine implements RagEngine {

    private final BaseVectorStore vectorStore;

    public SpringRagEngine(SpringVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void addDdl(String ddl) {
        // 输入验证
        if (ddl == null || ddl.trim().isEmpty()) {
            log.warn("Invalid input: ddl is null or empty");
            return;
        }

        try {
            // 确保线程安全（假设 vectorStore 是线程安全的）
            synchronized (vectorStore) {
                Document document = new Document(ddl, Map.of("script_type", TrainPolicyType.DDL.name()));
                log.info("Adding DDL Document : {}", document.getId());
                vectorStore.add(List.of(document));
            }
            log.info("DDL added successfully: {}", ddl);
        } catch (Exception e) {
            log.error("Failed to add DDL: {}", ddl, e);
            // 可以根据需求决定是否抛出异常或进行其他处理
        }
    }

    @Override
    public void addQuestionSql(String question, String sql) {
        // 输入验证
        if (question == null || question.trim().isEmpty()) {
            log.error("Invalid input: question is null or empty");
            throw new IllegalArgumentException("Question cannot be null or empty");
        }
        if (sql == null || sql.trim().isEmpty()) {
            log.error("Invalid input: sql is null or empty");
            throw new IllegalArgumentException("SQL cannot be null or empty");
        }

        try {
            // 创建 JSON 对象并添加数据
            JSONObject object = new JSONObject();
            object.put("question", question);
            object.put("sql", sql);

            // 将文档添加到向量存储
            synchronized (vectorStore) {
                Document document = new Document(object.toJSONString(), Map.of("script_type", TrainPolicyType.SQL.name()));
                log.info("Adding QuestionSql Document : {}", document.getId());
                vectorStore.add(List.of(document));
            }
        } catch (Exception e) {
            // 异常处理与日志记录
            log.error("Failed to add question and SQL to vector store", e);
            throw new RuntimeException("Failed to add question and SQL to vector store", e);
        }
    }


    @Override
    public void addDocumentation(String documentation) {
        // 输入验证
        if (documentation == null || documentation.trim().isEmpty()) {
            throw new IllegalArgumentException("Documentation cannot be null or empty");
        }

        try {
            // 创建 Document 对象
            Document doc = new Document(documentation, Map.of("script_type", TrainPolicyType.DOCUMENTATION.name()));

            // 确保线程安全（假设 vectorStore 是多线程环境下的共享资源）
            synchronized (vectorStore) {
                log.info("Adding Documentation Document : {}", doc.getId());
                vectorStore.add(List.of(doc));
            }
        } catch (Exception e) {
            // 异常处理
            // 记录日志并重新抛出异常，以便调用者能够处理
            log.error("Failed to add documentation: " + e.getMessage(), e);
            throw new RuntimeException("Failed to add documentation", e);
        }
    }


    @Override
    public void addDocumentation(File documentation) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new FileSystemResource(documentation));
        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
        List<Document> documents = splitter.apply(tikaDocumentReader.get());
        documents.forEach(document -> {
            vectorStore.add(List.of(
                    new Document(document.getFormattedContent(), Map.of("source", documentation.getName()))
            ));
        });
    }
}
