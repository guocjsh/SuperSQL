package com.aispace.supersql.spring.configure;

import com.aispace.supersql.mybatisplus.mapper.ExecuteSqlMapper;
import com.aispace.supersql.mybatisplus.service.ExecuteSqlService;
import com.aispace.supersql.service.IExecuteSqlService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@MapperScan("com.aispace.supersql.mybatisplus.mapper")
public class SuperSqlMybatisPlusConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IExecuteSqlService executeSqlService(ExecuteSqlMapper executeSqlMapper){
        return new ExecuteSqlService(executeSqlMapper);
    }

}
