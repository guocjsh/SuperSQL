package com.aispace.supersql.spring.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@Setter
@EnableConfigurationProperties(value = {SpringReRankProperties.class})
@ConfigurationProperties(prefix = "spring.ai.reranker")
public class SpringReRankProperties {

    private String baseUrl;

    private String apiKey;

    private String model;

    private Boolean enabled=false;

}
