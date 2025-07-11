package com.aispace.supersql.factory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.util.Assert;

/**
 * @author chengjie.guo
 */
public class ChatClientFactory {


    /**
     * 根据聊天模型获取聊天客户端实例
     *
     * @param model 聊天模型，包含了聊天所需的各种数据和配置信息
     * @return ChatClient 聊天客户端实例，用于进行聊天操作
     */
    public static ChatClient buildChatClient(ChatModel model){
        Assert.notNull(model, "chatModel cannot be null");
        // 其他逻辑
        return ChatClient.create(model);
    }

}
