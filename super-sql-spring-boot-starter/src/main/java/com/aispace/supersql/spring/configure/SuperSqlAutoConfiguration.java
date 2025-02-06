package com.aispace.supersql.spring.configure;

import com.aispace.supersql.engine.SpringRagEngine;
import com.aispace.supersql.engine.SpringSqlEngine;
import com.aispace.supersql.engine.TrainingPlanGenerator;
import com.aispace.supersql.service.IExecuteSqlService;
import com.aispace.supersql.vector.SpringVectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

/**
 * @author chengjie.guo
 */
@Slf4j
@AutoConfiguration
@Import(SuperSqlMybatisPlusConfiguration.class)
@PropertySource(value = "classpath:common-supersql.yml", factory = YmlPropertySourceFactory.class)
public class SuperSqlAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringVectorStore springVectorStore(@Autowired(required = false) VectorStore vectorStore) {
        return new SpringVectorStore(vectorStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringRagEngine ragEngine(SpringVectorStore springVectorStore) {
        return new SpringRagEngine(springVectorStore);
    }

    @Bean
    public SuperSQLProperties superSQLProperties() {
        return new SuperSQLProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringSqlEngine sqlEngine(
            @Autowired SuperSQLProperties superSQLProperties,
            IExecuteSqlService executeService,
            SpringVectorStore springVectorStore,
            SpringRagEngine springRagEngine
            ){
        SpringSqlEngine engine = new SpringSqlEngine(executeService, null, springVectorStore, springRagEngine);
        if (superSQLProperties != null && superSQLProperties.getInitTrain()) {
            log.info("Initialization training for SuperSQL has been enabled.");
            String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS";
            try {
                List<Map<String, Object>> object = engine.executeSql(sql);
                if (object != null && !object.isEmpty()) {
                    TrainingPlanGenerator.TrainingPlan plan = TrainingPlanGenerator.getTrainingPlanGeneric(object);
                    SpringRagEngine raggedEngine = ragEngine(springVectorStore);
                    plan.getPlan().forEach(item -> {
                        raggedEngine.addDocumentation(item.getItemValue());
                    });
                    log.info("Training plan generated successfully.");
                } else {
                    // 处理空结果集的情况
                    log.warn("No columns found in the database.");
                    return null;
                }
            } catch (Exception e) {
                // 记录异常日志
                // 抛出自定义异常或返回默认值
                throw new RuntimeException("Failed to execute SQL query", e);
            }
        }else {
            log.info("Initialization training for SuperSQL has been disabled.");
        }

        return engine;
    }







}
