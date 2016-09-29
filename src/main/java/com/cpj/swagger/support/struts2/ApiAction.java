/*
 *
 * Copyright (c) 2011, 2016 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.support.struts2;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.cpj.common.util.ResourceUtil;
import com.cpj.common.util.TextUtil;
import com.cpj.swagger.support.internal.ApiViewWriter;
import com.cpj.swagger.support.internal.Struts2ApiViewWriter;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.inject.Inject;

/**
 * @author yonghuan
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class ApiAction extends ActionSupport {
	
	@Inject("struts.action.extension")
	private String actionExtension;
	
	@Inject("struts.devMode")
	private String devMode;
	
	private ApiViewWriter apiViewWriter = new Struts2ApiViewWriter();
	
	@Override
	public String execute() throws Exception {
		Properties props = new Properties();
		InputStream is = ResourceUtil.getResourceAsStream("swagger.properties");
		props.load(is);
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = request.getContextPath();
		String host = request.getServerName() + ":" + request.getServerPort() + path;
		props.setProperty("apiHost", host);
		String apiFile = props.getProperty("apiFile");
		if(TextUtil.isEmpty(apiFile)) {
			apiFile = "/WEB-INF/apis.json";
		}
		String apiFilePath = request.getServletContext().getRealPath(apiFile);
		props.setProperty("apiFile", apiFilePath);
		if(TextUtil.isEmpty(props.getProperty("devMode"))) {
			props.setProperty("devMode", devMode);
		}
		apiViewWriter.writeApis(request, ServletActionContext.getResponse(), props);
		return null;
	}
	
	
	@Deprecated
	public void toIndex() throws Exception {
		index();
	}
	
	/**
	 * @since 1.2.0
	 */
	public void index() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String lang = request.getParameter("lang");
		if(TextUtil.isEmpty(lang)) {
			lang = "zh-cn";
		}
		Properties props = new Properties();
		InputStream is = ResourceUtil.getResourceAsStream("swagger.properties");
		props.load(is);
		String suffix = props.getProperty("suffix");
		if(TextUtil.isEmpty(suffix)) {
			suffix = "";
		}
		props.put("suffix", suffix);
		apiViewWriter.writeIndex(request, ServletActionContext.getResponse(), lang, props);
	}
	
	private String url;
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @since 1.2.0
	 */
	public void statics() throws Exception {
		apiViewWriter.writeStatic(ServletActionContext.getRequest(), ServletActionContext.getResponse());
	}
}
