package com.aispace.supersql.console.domain.bo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainBO {

    private String question;

    private String content;

    private String policy;

}
