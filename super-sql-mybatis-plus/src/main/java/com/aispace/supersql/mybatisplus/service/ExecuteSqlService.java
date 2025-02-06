package com.aispace.supersql.mybatisplus.service;

import com.aispace.supersql.mybatisplus.mapper.ExecuteSqlMapper;
import com.aispace.supersql.service.IExecuteSqlService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ExecuteSqlService implements IExecuteSqlService {

    private final ExecuteSqlMapper executeSqlMapper;

    @Override
    public List<Map<String,Object>> executeSql(String sql) {
        return this.executeSqlMapper.execute(sql);
    }
}
