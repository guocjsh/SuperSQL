package com.aispace.supersql.builder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowupQuestionsBuilder {

    private String question;

    private String sql;

    private Integer questionsNum=5;

    private Object result;


}
