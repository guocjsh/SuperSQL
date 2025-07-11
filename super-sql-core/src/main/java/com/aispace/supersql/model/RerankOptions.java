package com.aispace.supersql.model;

import org.springframework.ai.model.ModelOptions;
import org.springframework.lang.Nullable;

public interface RerankOptions extends ModelOptions {

	@Nullable
	String getModel();

	@Nullable
	Integer getTopN();

}