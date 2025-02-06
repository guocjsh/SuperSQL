package com.aispace.supersql.engine;

import com.aispace.supersql.service.IExecuteSqlService;
import com.aispace.supersql.vector.SpringVectorStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class SpringSqlEngine extends AbstractSqlEngine {

    private final IExecuteSqlService executeService;

    private final ChatModel chatModel;

    private final SpringVectorStore store;

    private final SpringRagEngine springRagEngine;

    @Override
    protected void builder() {
        super.chatModel= chatModel;
        super.vectorStore = store;
        super.ragEngine =springRagEngine ;
    }

    public SpringSqlEngine setChatModel(ChatModel chatModel){
        super.chatModel= chatModel;
        return this;
    }

    @Override
    public List<Map<String,Object>> executeSql(String sql)  {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL cannot be null or empty");
        }
        try {
            log.info("Executing SQL: {}", sql);
            return executeService.executeSql(sql);
        } catch (Exception e) {
            log.error("Failed to execute SQL: {}", sql);
            throw new RuntimeException("Failed to execute SQL", e);
        }
    }
}
