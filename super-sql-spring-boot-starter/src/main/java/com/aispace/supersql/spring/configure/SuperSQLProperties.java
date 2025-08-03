package com.aispace.supersql.spring.configure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "super-sql")
public class SuperSQLProperties {

    private Boolean initTrain = false;

    private ScopeType scope = ScopeType.ALONE;

    private List<Schema> schemas;

    private Double temperature = 0.0;

    public enum ScopeType {
        ALONE,
        ALL;
    }

    public record Schema( @JsonProperty("schema") String schema) {}

}
