/*
 * Copyright 2011-2017 CPJIT Group.
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
	private boolean deprecated;
	
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
	/**
	 * 判断接口是否已经被废弃。
	 * @since 1.2.2
	 */
	public boolean isDeprecated() {
		return deprecated;
	}
	/**
	 * @since 1.2.2
	 */
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}
}
