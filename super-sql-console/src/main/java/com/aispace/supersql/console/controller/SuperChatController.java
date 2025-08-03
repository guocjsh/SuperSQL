package com.aispace.supersql.console.controller;

import cn.hutool.core.util.StrUtil;
import com.aispace.supersql.builder.RagOptions;
import com.aispace.supersql.console.response.ResponseResult;
import com.aispace.supersql.engine.SpringSqlEngine;
import com.aispace.supersql.console.domain.bo.ChatBO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequestMapping("super")
@AllArgsConstructor
public class SuperChatController {

    private final AzureOpenAiChatModel chatModel;

    private final SpringSqlEngine sqlEngine;

    @PostMapping("/getSql")
    public ResponseResult<String> getSql(@RequestBody ChatBO chatBO) {
        String sql = sqlEngine
                .setChatModel(chatModel)
                .setOptions(RagOptions.builder().topN(10).rerank(true).limitScore(0.4).build())
                .generateSql(chatBO.getQuestion());
        return ResponseResult.success(sql);
    }

    @PostMapping("/chat")
    public ResponseBodyEmitter chat(@RequestBody ChatBO chatBO, HttpServletResponse servletResponse) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        servletResponse.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        String sql = sqlEngine
                .setChatModel(chatModel)
                .setOptions(RagOptions.builder()
                        .topN(5)
                        .rerank(false)
                        .limitScore(0.4)
                        .build())
                .generateSql(chatBO.getQuestion());
        Object object = sqlEngine.executeSql(sql);
        Flux<ChatResponse> stream = sqlEngine.generateSummary(chatBO.getQuestion(), object.toString());
        stream.publishOn(Schedulers.boundedElastic())
                .doOnError(emitter::completeWithError)
                .doOnComplete(emitter::complete)
                .subscribe(data -> {
                    String text = data.getResult().getOutput().getText();
                    try {
                        if (StrUtil.isNotEmpty(text)) {
                            emitter.send(text);
                            Thread.sleep(50);
                        }
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                });
        return emitter;
    }

}
