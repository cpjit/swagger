/*
 *
 * Copyright (c) 2011, 2016 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.support.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cpj.common.util.ResourceUtil;
import com.cpj.common.util.TextUtil;
import com.cpj.swagger.support.internal.ApiViewWriter;
import com.cpj.swagger.support.internal.DefaultApiViewWriter;

/**
 * @author yonghuan
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class ApiServlet extends HttpServlet {
	
	private ApiViewWriter apiViewWriter = new DefaultApiViewWriter();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		if(uri.matches(".*/api/index(/?)(!/)*")) {
			toIndex(req, resp);
		} else if (uri.matches(".*/api/statics/.+")){
			queryStatic(req, resp);
		} else {
			try {
				queryApi(req, resp);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}
	
	private void toIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
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
		apiViewWriter.writeIndex(request, response, lang, props);
	}
	
	/*
	 * @since 1.2.0
	 */
	private void queryStatic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		apiViewWriter.writeStatic(request, response);
	}
	
	private void queryApi(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
}
