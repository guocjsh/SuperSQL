package com.aispace.supersql.mcp.configuration;

import com.aispace.supersql.mcp.modules.tool.SuperSQLService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class SuperSQLMcpConfig {

    @Bean
    public ToolCallbackProvider generateSqlTools(SuperSQLService sqlService) {
        return MethodToolCallbackProvider.builder().toolObjects(sqlService).build();

    }
}