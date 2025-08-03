package com.aispace.supersql.mcp.modules.tool;

import com.aispace.supersql.mcp.modules.service.ISQLEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperSQLService {

    private final ISQLEngineService engineService;

    @Tool(description = "Generate executable SQL statements according to user requirements and query to obtain data")
    public List<Map<String,Object>> getDataBaseData(String question) {
        return engineService.getNl2SQLData(question);
    }

}
