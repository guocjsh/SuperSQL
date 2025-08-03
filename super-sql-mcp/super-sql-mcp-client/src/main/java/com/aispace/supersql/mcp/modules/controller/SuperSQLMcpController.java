package com.aispace.supersql.mcp.modules.controller;

import com.aispace.supersql.mcp.modules.response.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("mcp")
@AllArgsConstructor
public class SuperSQLMcpController {

    private final AzureOpenAiChatModel chatModel;

    private final ToolCallbackProvider tools;


    @GetMapping("chat")
    public ResponseResult<Object> chatWithDatabase(@RequestParam String question) {
        String content = ChatClient
                .builder(chatModel)
                .defaultToolCallbacks( tools)
                .build()
                .prompt(question)
                .call().content();
        return ResponseResult.success(content);
    }


}
