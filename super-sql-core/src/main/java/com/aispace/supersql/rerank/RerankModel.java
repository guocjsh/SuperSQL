package com.aispace.supersql.rerank;

import com.aispace.supersql.model.RerankRequest;
import com.aispace.supersql.model.RerankResponse;
import org.springframework.ai.model.Model;

public interface RerankModel extends Model<RerankRequest, RerankResponse> {

	@Override
	RerankResponse call(RerankRequest request);

}