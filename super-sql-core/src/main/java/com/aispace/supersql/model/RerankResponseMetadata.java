package com.aispace.supersql.model;

import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.model.AbstractResponseMetadata;
import org.springframework.ai.model.ResponseMetadata;

import java.util.Map;

public class RerankResponseMetadata extends AbstractResponseMetadata implements ResponseMetadata {

	private Usage usage = new EmptyUsage();

	public RerankResponseMetadata() {
	}

	public RerankResponseMetadata(Usage usage) {
		this.usage = usage;
	}

	public RerankResponseMetadata(Usage usage, Map<String, Object> metadata) {
		this.usage = usage;
		this.map.putAll(metadata);
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

}