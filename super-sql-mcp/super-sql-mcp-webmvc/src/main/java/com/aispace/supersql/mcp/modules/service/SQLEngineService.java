package com.aispace.supersql.mcp.modules.service;

import com.aispace.supersql.builder.RagOptions;
import com.aispace.supersql.engine.SpringSqlEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SQLEngineService implements ISQLEngineService{

    private final AzureOpenAiChatModel chatModel;
    private final SpringSqlEngine sqlEngine;

    public SQLEngineService(@Lazy AzureOpenAiChatModel chatModel, SpringSqlEngine sqlEngine) {
        this.chatModel = chatModel;
        this.sqlEngine = sqlEngine;
    }

    @Override
    public String genSQL(String question) {
        return sqlEngine
                .setChatModel(chatModel)
                .setOptions(RagOptions.builder().topN(10).rerank(false).limitScore(0.4).build())
                .generateSql(question);
    }

    @Override
    public List<Map<String,Object>> getNl2SQLData(String question) {
        String sql = sqlEngine
                .setChatModel(chatModel)
                .setOptions(RagOptions.builder().topN(10).rerank(false).limitScore(0.4).build())
                .generateSql(question);
        return sqlEngine.executeSql(sql);
    }
}
