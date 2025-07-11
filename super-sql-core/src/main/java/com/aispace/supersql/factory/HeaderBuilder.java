package com.aispace.supersql.factory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.function.Consumer;

public class HeaderBuilder {

    public static Consumer<HttpHeaders> getJsonContentHeaders(String apiKey) {
        return (headers) -> {
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
           // headers.setContentLength(224);
        };
    }
}
