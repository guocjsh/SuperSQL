package com.aispace.supersql.console.controller;

import cn.hutool.core.util.StrUtil;
import com.aispace.supersql.enumd.TrainPolicyType;
import com.aispace.supersql.console.response.ResponseResult;
import com.aispace.supersql.vector.SpringVectorStore;
import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("document")
@AllArgsConstructor
public class DocumentController {

    private final SpringVectorStore store;

    @GetMapping("getDocuments")
    public ResponseResult<List<Document>> getDocuments(@RequestParam String question) {
        FilterExpressionBuilder expression = new FilterExpressionBuilder();
        List<Document> documents = this.store.searchByTag(StrUtil.isBlank(question) ?"ALL DTP`s DDL SQL DOCUMENTATION" :question,
                expression
                        .in("script_type", TrainPolicyType.DOCUMENTATION.name(),TrainPolicyType.SQL.name(),TrainPolicyType.DDL.name())
                        .build(),
                10

        );
        return ResponseResult.success(documents);
    }

    @GetMapping("remove")
    public ResponseResult<Boolean> removeDocuments(@RequestParam String id) {
        boolean delete = this.store.delete(List.of(id)).get();
        return ResponseResult.success(delete);
    }

}
