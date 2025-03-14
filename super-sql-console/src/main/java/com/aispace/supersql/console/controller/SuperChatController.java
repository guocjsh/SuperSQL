package com.aispace.supersql.console.controller;

import cn.hutool.core.util.StrUtil;
import com.aispace.supersql.engine.SpringSqlEngine;
import com.aispace.supersql.console.domain.bo.ChatBO;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@RestController
@RequestMapping("super")
@AllArgsConstructor
public class SuperChatController {

    private final AzureOpenAiChatModel chatModel;

    //private final OpenAiChatModel chatModel;

    private final SpringSqlEngine sqlEngine;

    @GetMapping("/chat2")
    public String chat2(){
        return "hello";
    }


    @PostMapping("/chat")
    public ResponseBodyEmitter chat(@RequestBody ChatBO chatBO, HttpServletResponse servletResponse) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        servletResponse.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        String sql = sqlEngine.setChatModel(chatModel).generateSql(chatBO.getQuestion());
        Object object = sqlEngine.executeSql(sql);
        JSONObject echarsJson = sqlEngine.generateEcharsJson(object.toString());
        Flux<ChatResponse> stream = sqlEngine.generateSummary(chatBO.getQuestion(), object.toString());
        stream.publishOn(Schedulers.boundedElastic())
                .doOnError(emitter::completeWithError)
                .doOnComplete(()->{
                    try {
                        if(echarsJson!=null){
                            emitter.send("<echar>"+echarsJson+"</echar>");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }finally {
                        emitter.complete();
                    }
                        }
                )
                .subscribe(data -> {
                    String text = data.getResult().getOutput().getText();
                    try {
                        if(StrUtil.isNotEmpty(text)){
                            emitter.send(text);
                            //Thread.sleep(50);
                        }
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                });
        return emitter;
    }

}
