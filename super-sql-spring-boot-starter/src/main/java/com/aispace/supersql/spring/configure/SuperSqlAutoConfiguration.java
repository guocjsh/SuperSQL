package com.aispace.supersql.spring.configure;

import com.aispace.supersql.engine.SpringRagEngine;
import com.aispace.supersql.engine.SpringSqlEngine;
import com.aispace.supersql.engine.TrainingPlanGenerator;
import com.aispace.supersql.factory.RequestFactory;
import com.aispace.supersql.model.RerankOptions;
import com.aispace.supersql.rerank.DefaultRerankModel;
import com.aispace.supersql.rerank.RerankModel;
import com.aispace.supersql.service.IExecuteSqlService;
import com.aispace.supersql.vector.SpringVectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public SpringReRankProperties springReRankProperties() {
        return new SpringReRankProperties();
    }


    @Bean
    @ConditionalOnMissingBean
    public SpringSqlEngine sqlEngine(
            @Autowired SuperSQLProperties superSQLProperties,
            IExecuteSqlService executeService,
            SpringVectorStore springVectorStore,
            SpringRagEngine springRagEngine,
            ResourceLoader resourceLoader,
            @Autowired(required = false) RerankModel rerankModel
    ) {
        SpringSqlEngine engine = new SpringSqlEngine(executeService, null, springVectorStore, springRagEngine, resourceLoader, rerankModel);

        if (superSQLProperties == null || !superSQLProperties.getInitTrain()) {
            log.info("Initialization training for SuperSQL has been disabled.");
            return engine;
        }

        log.info("Initialization training for SuperSQL has been enabled.");
        String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS";
        if (superSQLProperties.getScope() == SuperSQLProperties.ScopeType.ALONE) {
            List<SuperSQLProperties.Schema> schemas = superSQLProperties.getSchemas();
            if (schemas != null && !schemas.isEmpty()) {
                String scheamList = schemas.stream()
                        .map(i -> "'" + i.schema() + "'")
                        .collect(Collectors.joining(",", "(", ")"));
                sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema in " +scheamList ;
            }
        }
        try {
            List<Map<String, Object>> result = engine.executeSql(sql);
            if (result == null || result.isEmpty()) {
                log.warn("No columns found in the database during initialization training.");
                throw new RuntimeException("No columns found in the database.");
            }

            TrainingPlanGenerator.TrainingPlan plan = TrainingPlanGenerator.getTrainingPlanGeneric(result);
            SpringRagEngine raggedEngine = ragEngine(springVectorStore);
            plan.getPlan().forEach(item -> raggedEngine.addDocumentation(item.getItemValue()));
            log.info("Training plan generated successfully with {} items.", plan.getPlan().size());

        } catch (Exception e) {
            log.error("Failed to execute SQL query for initialization training: {}", sql, e);
            throw new RuntimeException("Failed to execute SQL query for initialization training.", e);
        }

        return engine;
    }



    @Configuration
    @ConditionalOnProperty(
            prefix = "spring.ai.reranker",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    protected static class SuperSqlReRankerConfiguration {

        @Bean
        public RerankModel rerankModel(
                @Autowired SpringReRankProperties reRankProperties,
                RetryTemplate retryTemplate,
                RestClient.Builder restClientBuilder,
                ResponseErrorHandler responseErrorHandler
        ) {
            RequestFactory requestFactory = new RequestFactory(reRankProperties.getBaseUrl(), reRankProperties.getApiKey(), restClientBuilder, responseErrorHandler);
            RerankOptions options = new RerankOptions() {
                @Override
                public String getModel() {
                    return reRankProperties.getModel();
                }

                @Override
                public Integer getTopN() {
                    return 10;
                }
            };
            return new DefaultRerankModel(retryTemplate, requestFactory, options);
        }

    }


}
