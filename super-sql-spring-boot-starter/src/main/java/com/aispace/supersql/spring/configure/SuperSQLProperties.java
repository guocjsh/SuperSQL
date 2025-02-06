package com.aispace.supersql.spring.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@Setter
@EnableConfigurationProperties(value = {SuperSQLProperties.class})
@ConfigurationProperties(prefix = "super-sql")
public class SuperSQLProperties {

    private Boolean initTrain;

    private Double temperature;

    public Boolean getInitTrain() {
        return initTrain;
    }

    public void setInitTrain(Boolean initTrain) {
        this.initTrain = initTrain;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
