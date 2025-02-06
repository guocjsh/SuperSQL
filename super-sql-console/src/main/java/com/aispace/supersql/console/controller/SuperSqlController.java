package com.aispace.supersql.console.controller;

import com.aispace.supersql.builder.TrainBuilder;
import com.aispace.supersql.engine.SpringSqlEngine;
import com.aispace.supersql.enumd.TrainPolicyType;
import com.aispace.supersql.console.domain.bo.TrainBO;
import com.aispace.supersql.console.response.ResponseResult;
import lombok.AllArgsConstructor;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("superSql")
@AllArgsConstructor
public class SuperSqlController{

    private final AzureOpenAiChatModel azureChatModel;

    private final SpringSqlEngine sqlEngine;

    @PostMapping("train")
    public ResponseResult<Object> train(@RequestBody TrainBO train) {
        TrainBuilder builder = TrainBuilder.builder().question(train.getQuestion()).content(train.getContent()).policy(TrainPolicyType.valueOf(train.getPolicy())).build();
        sqlEngine.setChatModel(azureChatModel).train(builder);
        return ResponseResult.success();
    }


}
