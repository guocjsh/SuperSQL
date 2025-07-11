package com.aispace.supersql.vector;

import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;

import java.util.List;
import java.util.Optional;

/**
 * @author chengjie.guo
 */
@AllArgsConstructor
public class  SpringVectorStore implements BaseVectorStore{

    private final VectorStore vectorStore;

    @Override
    public void add(List<Document> documents){
        vectorStore.add(documents);
    }

    @Override
    public void delete(List<String> idList) {
        vectorStore.delete(idList);
    }

    @Override
    public List<Document> similaritySearch(String query) {
        return vectorStore.similaritySearch(query);
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        return vectorStore.similaritySearch(request);
    }

    @Override
    public List<Document> searchByTag(String query, String tag, Integer topK) {
        return this.vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .filterExpression(tag)
                        .build()
        );
    }

    @Override
    public List<Document> searchByTag(String query, Expression expression, Integer topK) {
        return this.vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .filterExpression(expression)
                        .build()
        );
    }

    @Override
    public List<Document> searchByExpression(String query,Expression expression) {
      return this.vectorStore.similaritySearch(SearchRequest.builder().query(query).filterExpression(expression).build());
    }


}
