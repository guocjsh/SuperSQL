package com.aispace.supersql.console.controller;

import cn.hutool.core.util.StrUtil;
import com.aispace.supersql.engine.SpringSqlEngine;
import com.aispace.supersql.console.domain.bo.ChatBO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.model.ChatResponse;
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

    @PostMapping("/chat")
    public ResponseBodyEmitter chat(@RequestBody ChatBO chatBO, HttpServletResponse servletResponse) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        servletResponse.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        String sql = sqlEngine.setChatModel(chatModel).generateSql(chatBO.getQuestion());
        Object object = sqlEngine.executeSql(sql);
        Flux<ChatResponse> stream = sqlEngine.generateSummary(chatBO.getQuestion(), object.toString());
        stream.publishOn(Schedulers.boundedElastic())
                .doOnError(emitter::completeWithError)
                .doOnComplete(emitter::complete)
                .subscribe(data -> {
                    String text = data.getResult().getOutput().getText();
                    try {
                        if(StrUtil.isNotEmpty(text)){
                            emitter.send(text );
                            Thread.sleep(50);
                        }
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                });
        return emitter;
    }


//    @PostMapping("/chat")
//    public ResponseBodyEmitter chat(@RequestBody ChatBO chatBO, HttpServletResponse servletResponse) {
//        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//        servletResponse.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
//        PromptTemplate promptTemplate = new PromptTemplate("""
//                你是一个GSK的AI私人助理，你叫小G。用户问的问题是:
//                '{question}'
//                请用私人助理的口吻回答用户问题
//                """);
//        Prompt prompt = promptTemplate.create(Map.of("question", chatBO.getQuestion()));
//        Flux<ChatResponse> stream = this.chatModel.stream(prompt);
//        stream.publishOn(Schedulers.boundedElastic())
//                .doOnError(emitter::completeWithError)
//                .doOnComplete(emitter::complete)
//                .subscribe(data -> {
//                    String text = data.getResult().getOutput().getText();
//                    try {
//                        if(StrUtil.isNotEmpty(text)){
//                            emitter.send(text.trim() + "\n");
//                            Thread.sleep(100);
//                        }
//                    } catch (IOException | InterruptedException e) {
//                        emitter.completeWithError(e);
//                    }
//                });
//        return emitter;
//    }





}
