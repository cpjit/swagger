/*
 * Copyright 2011-2019 CPJIT Group.
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
package com.cpjit.swagger4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Api文档。
 * 
 * @author yonghaun
 * @since 1.0.0
 */
class APIDoc {
	private final String swagger = "2.0";

	/**
	 * @return swagger协议版本
	 */
	public String getSwagger() {
		return swagger;
	}

	private String[] schemes = new String[] { "http", "https" };

	/**
	 * @return schemes
	 */
	public String[] getSchemes() {
		return schemes;
	}

	/**
	 * @param schemes
	 *            schemes
	 */
	public void setSchemes(String[] schemes) {
		this.schemes = schemes;
	}

	private Map<String, Map<String, Operation>> paths;

	/**
	 * @return 全部path
	 */
	public Map<String, Map<String, Operation>> getPaths() {
		return paths;
	}

	/**
	 * @param paths
	 *            全部path
	 */
	public void setPaths(Map<String, Map<String, Operation>> paths) {
		this.paths = paths;
	}

	private Map<String, Object> definitions;

	/**
	 * @return 全部definition
	 */
	public Map<String, Object> getDefinitions() {
		return definitions;
	}

	/**
	 * @param definitions
	 *            全部definition
	 */
	public void setDefinitions(Map<String, Object> definitions) {
		this.definitions = definitions;
	}

	private APIDocInfo info;

	/**
	 * @return 文档信息。
	 */
	public APIDocInfo getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            文档信息。
	 */
	public void setInfo(APIDocInfo info) {
		this.info = info;
	}

	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	private String basePath;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	private Collection<Tag> tags = new ArrayList<Tag>();

	public Collection<Tag> getTags() {
		return tags;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}
}
