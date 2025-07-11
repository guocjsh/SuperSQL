package com.aispace.supersql.builder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagOptions {

    private Integer topN;

    private Double limitScore;

    private Boolean rerank;




}
