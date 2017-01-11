/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger;

/**
 * API文档信息。
 * <p>
 * 可以通过如下的方式来构建一个API文件信息对象：
 * </p>
 *<pre>
 *  	// 创建一个构建器
 *  	ApiDocInfo.Builder builder = new ApiDocInfo.Builder();
 *  	// 设置可选参数的值
 *  	builder.description("API描述信息");
 *  	builder.version("API版本号");
 *  	// 设置其他可选参数
 *  	// ...
 *  	// 构建API文档信息
 *  	builder.build();
 * 	</pre>
 * 
 * @author yonghaun
 * @since 1.0.0
 */
public class APIDocInfo {
	private String description;
	private String version;
	private String title;
	private String termsOfService;
	private String contact;
	private License license;

	private APIDocInfo(Builder builder) {
		this.contact = builder.contact;
		this.description = builder.description;
		this.license = builder.license;
		this.termsOfService = builder.termsOfService;
		this.title = builder.title;
		this.version = builder.version;
	}
	
	/**
	 * API文档信息构建器
	 * @author yonghuan
	 * @since 1.0.0
	 */
	public static class Builder{
		/**
		 * 创建一个构建器。
		 * @return
		 */
		public APIDocInfo build() {
			return new APIDocInfo(this);
		}
		
		private String description;

		/**
		 * 设置API描述
		 * 
		 * @param val
		 *            API描述
		 * @return
		 */
		public Builder description(String val) {
			this.description = val;
			return this;
		}

		private String version;

		/**
		 * 设置API版本
		 * 
		 * @param val
		 *            API版本
		 * @return
		 */
		public Builder version(String val) {
			this.version = val;
			return this;
		}

		private String title;

		/**
		 * 设置API标题
		 * 
		 * @param val
		 *            API标题
		 * @return
		 */
		public Builder title(String val) {
			this.title = val;
			return this;
		}

		private String termsOfService;

		/**
		 * 设置API开发团队的服务地址
		 * 
		 * @param val
		 *            API开发团队的服务地址
		 * @return
		 */
		public Builder termsOfService(String val) {
			this.termsOfService = val;
			return this;
		}

		private String contact;

		/**
		 * 设置API开发团队的联系人
		 * 
		 * @param val
		 *            API开发团队的联系人
		 * @return
		 */
		public Builder contact(String val) {
			this.contact = val;
			return this;
		}

		private License license;

		/**
		 * 设置API遵循的协议（如apahce开源协议）
		 * 
		 * @param val
		 *            API遵循的协议（如apahce开源协议）
		 * @return
		 */
		public Builder license(License val) {
			this.license = val;
			return this;
		}
	}
	public String getDescription() {
		return description;
	}

	public String getVersion() {
		return version;
	}

	public String getTitle() {
		return title;
	}

	public String getTermsOfService() {
		return termsOfService;
	}

	public String getContact() {
		return contact;
	}

	public License getLicense() {
		return license;
	}
}
