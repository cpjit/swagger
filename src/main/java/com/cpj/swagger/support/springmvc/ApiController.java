/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.support.springmvc;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cpj.common.util.ResourceUtil;
import com.cpj.common.util.TextUtil;
import com.cpj.swagger.annotation.API;
import com.cpj.swagger.annotation.APIs;
import com.cpj.swagger.annotation.Param;
import com.cpj.swagger.support.internal.ApiViewWriter;
import com.cpj.swagger.support.internal.DefaultApiViewWriter;

/**
 * @author yonghuan
 * @since 1.0.0
 */
@Controller
@RequestMapping("/api")
@APIs("/api")
public class ApiController implements InitializingBean {
	
	private ApiViewWriter apiViewWriter = new DefaultApiViewWriter();
	private Properties props = new Properties();
	
	@RequestMapping(value = "index", method = RequestMethod.GET)
	@API(value = "", summary = "获取API文档", method = "get", parameters = @Param(name = "lang", description = "语言（默认为中文）", type = "string", format = "string"))
	public void index(HttpServletRequest request,HttpServletResponse response,  @RequestParam(defaultValue = "zh-cn") String lang) throws Exception {
		if(TextUtil.isEmpty(lang)) {
			lang = "zh-cn";
		}
		String suffix = props.getProperty("suffix");
		if(TextUtil.isEmpty(suffix)) {
			suffix = "";
		}
		props.put("suffix", suffix);
		apiViewWriter.writeIndex(request, response, lang, props);
	}
	
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public void queryApi(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Properties props = new Properties();
		InputStream is = ResourceUtil.getResourceAsStream("swagger.properties");
		props.load(is);
		String path = request.getContextPath();
		String host = request.getServerName() + ":" + request.getServerPort() + path;
		props.setProperty("apiHost", host);
		String apiFile = props.getProperty("apiFile");
		if(TextUtil.isEmpty(apiFile)) {
			apiFile = "/WEB-INF/apis.json";
		}
		String apiFilePath = request.getServletContext().getRealPath(apiFile);
		props.setProperty("apiFile", apiFilePath);
		apiViewWriter.writeApis(request, response, props);
	}
	
	/**
	 * @since 1.2.0
	 */
	@RequestMapping(value="statics/**", method=RequestMethod.GET)
	public void queryStatic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		apiViewWriter.writeStatic(request, response, props);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		InputStream is = ResourceUtil.getResourceAsStream("swagger.properties");
		props.load(is);
	}
}
