package com.aispace.supersql.mybatisplus.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExecuteSqlMapper {

    List<Map<String,Object>> execute(@Param("sql") String sql);

}
