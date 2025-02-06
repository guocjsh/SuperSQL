package com.aispace.supersql.builder;

import com.aispace.supersql.engine.RagEngine;
import com.aispace.supersql.vector.BaseVectorStore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlEngineBuilder {

    private BaseVectorStore vectorStore;

    private RagEngine ragEngine;

    private ChatClient chatClient;


}
