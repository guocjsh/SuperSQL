package com.aispace.supersql.mcp.modules.service;

import java.util.List;
import java.util.Map;

public interface ISQLEngineService {

    String genSQL(String question);

    List<Map<String,Object>> getNl2SQLData(String question);

}
