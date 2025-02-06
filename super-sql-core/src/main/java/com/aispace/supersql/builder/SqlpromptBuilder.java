package com.aispace.supersql.builder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlpromptBuilder {

    private String question;

    private List<Document> questionSqlList;

    private List<Document> ddlList;

    private List<Document> documentList;
}
