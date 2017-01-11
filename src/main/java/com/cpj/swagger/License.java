/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger;

/**
 * API遵循的协议。
 * 
 * @author yonghaun
 * @since 1.0.0
 */
public class License {
	private String name;
	private String url;

	public License() {}
	
	/**
	 * @param name
	 * @param url
	 * @since 1.0.3
	 */
	public License(String name, String url) {
		this.name = name;
		this.url = url;
	}

	/**
	 * @return 协议名
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 协议名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return 协议主页
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url 协议主页
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public License clone() throws CloneNotSupportedException {
		return (License) super.clone();
	}
}
