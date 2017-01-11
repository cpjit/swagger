/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yonghaun
 * @since 1.0.0
 */
class Path {
	private String summary;
	private String description;
	private String operationId;
	private List<String> tags = new ArrayList<String>();
	private List<String> consumes = new ArrayList<String>();
	private List<String> produces = new ArrayList<String>();
	private List<Map<String, Object>> parameters = new ArrayList<Map<String, Object>>();

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getConsumes() {
		return consumes;
	}

	public void setConsumes(List<String> consumes) {
		this.consumes = consumes;
	}

	public List<String> getProduces() {
		return produces;
	}

	public void setProduces(List<String> produces) {
		this.produces = produces;
	}

	public List<Map<String, Object>> getParameters() {
		return parameters;
	}

	public void setParameters(List<Map<String, Object>> parameters) {
		this.parameters = parameters;
	}
}
