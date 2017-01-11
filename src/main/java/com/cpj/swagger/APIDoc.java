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

	private String[] schemes = new String[] { "http" };

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

	private Map<String, Map<String, Path>> paths;

	/**
	 * @return 全部path
	 */
	public Map<String, Map<String, Path>> getPaths() {
		return paths;
	}

	/**
	 * @param paths
	 *            全部path
	 */
	public void setPaths(Map<String, Map<String, Path>> paths) {
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

	private List<Tag> tags = new ArrayList<Tag>();

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}
