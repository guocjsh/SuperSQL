package com.aispace.supersql.builder;

import com.aispace.supersql.enumd.TrainPolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chengjie.guo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainBuilder {

    private String question;

    private String content;

    private TrainPolicyType policy;




}
