package com.aispace.supersql.model;

import org.springframework.ai.model.ModelResponse;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class  RerankResponse implements ModelResponse<DocumentWithScore>{

    private final List<DocumentWithScore> documents;

    private final RerankResponseMetadata metadata;

    public RerankResponse(List<DocumentWithScore> documents) {
        this(documents, new RerankResponseMetadata());
    }

    public RerankResponse(List<DocumentWithScore> documents, RerankResponseMetadata metadata) {
        this.documents = documents;
        this.metadata = metadata;
    }

    @Override
    public DocumentWithScore getResult() {
        if (CollectionUtils.isEmpty(this.documents)) {
            return null;
        }

        return this.documents.get(0);
    }

    @Override
    public List<DocumentWithScore> getResults() {
        return this.documents;
    }

    @Override
    public RerankResponseMetadata getMetadata() {
        return this.metadata;
    }
}
