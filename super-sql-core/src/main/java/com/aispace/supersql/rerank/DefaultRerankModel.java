package com.aispace.supersql.rerank;

import com.aispace.supersql.factory.RequestFactory;
import com.aispace.supersql.model.DocumentWithScore;
import com.aispace.supersql.model.RerankOptions;
import com.aispace.supersql.model.RerankRequest;
import com.aispace.supersql.model.RerankResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class DefaultRerankModel implements RerankModel{

    private final RetryTemplate retryTemplate;

    private final RequestFactory requestFactory;

    private final RerankOptions options;

    @Override
    public RerankResponse call(RerankRequest request) {
        Assert.notNull(request.getQuery(), "query must not be null");
        Assert.notNull(request.getInstructions(), "documents must not be null");
        RequestFactory.RerankRequest rerankRequest = createRequest(request);
        ResponseEntity<RequestFactory.RerankResponse> responseEntity = this.retryTemplate
                .execute(ctx -> this.requestFactory.rerankEntity(rerankRequest));
        var response = responseEntity.getBody();
        if (response == null) {
            log.warn("No rerank returned for query: {}", request.getQuery());
            return new RerankResponse(Collections.emptyList());
        }

        List<DocumentWithScore> documentWithScores = response
                .results()
                .stream()
                .map(data -> DocumentWithScore.builder()
                        .withScore(data.relevanceScore())
                        .withDocument(request.getInstructions().get(data.index()))
                        .build())
                .toList();
        return new RerankResponse(documentWithScores, null);
    }

    private RequestFactory.RerankRequest createRequest(RerankRequest request) {
        List<String> docs = request.getInstructions().stream().map(Document::getText).toList();
        //var input = new RequestFactory.RerankRequestInput(request.getQuery(), docs);
        return new RequestFactory.RerankRequest(options.getModel(),request.getQuery(), docs);
    }


}
