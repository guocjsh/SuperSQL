package com.aispace.supersql.factory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RequestFactory {

    private final RestClient restClient;

    public RequestFactory(String baseUrl){
        this(baseUrl,null);
    }


    public RequestFactory(String baseUrl,String apikey){
        this(baseUrl,apikey ,RestClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);
    }


    public RequestFactory(String baseUrl, String apiKey, RestClient.Builder restBuilder, ResponseErrorHandler errorHandler){
        this.restClient = restBuilder.baseUrl(baseUrl)
                .defaultHeaders(HeaderBuilder.getJsonContentHeaders(apiKey))
                .defaultStatusHandler(errorHandler)
                .build();
    }

    /**
     * Represents rerank response output
     *
     * @param results rerank output results
     */
    public record RerankResponseOutput(@JsonProperty("results") List<RerankResponseOutputResult> results) {

    }

    /**
     * Represents rerank response
     *
     * @param results rerank response results
     * @param requestId rerank request id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RerankResponse(@JsonProperty("results") List<RerankResponseOutputResult> results, @JsonProperty("id") String requestId) {

    }


    /**
     * Represents rerank request information.
     *
     * @param model ID of the model to use.
     *
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RerankRequest(@JsonProperty("model") String model,@JsonProperty("query") String query,
                                @JsonProperty("documents") List<String> documents) {
    }

    /**
     * Represents rerank request input
     *
     * @param query query string for rerank.
     * @param documents list of documents for rerank.
     */
    public record RerankRequestInput(@JsonProperty("query") String query,
                                     @JsonProperty("documents") List<String> documents) {
    }

    /**
     * Represents rerank output result
     *
     * @param index index of input document list
     * @param relevanceScore relevance score between query and document
     * @param document original document
     */
    public record RerankResponseOutputResult(@JsonProperty("index") Integer index,
                                             @JsonProperty("relevance_score") Double relevanceScore,
                                             @JsonProperty("document") Map<String, Object> document) {
    }

    /**
     * Creates rerank request for rerank model.
     * @param rerankRequest The chat completion request.
     * @return Entity response with {@link RerankResponse} as a body and HTTP status code
     * and headers.
     */
    public ResponseEntity<RerankResponse> rerankEntity(RerankRequest rerankRequest) {
        Assert.notNull(rerankRequest, "The request body can not be null.");
        return this.restClient.post()
                .body(rerankRequest)
                .retrieve()
                .toEntity(RerankResponse.class);
    }

}
