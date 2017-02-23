/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.support.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONWriter;
import com.cpj.swagger.APIParseable;
import com.cpj.swagger.APIParser;
import com.cpj.swagger.support.internal.templates.FreemarkerUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author yonghuan
 * @since 1.2.0
 */
public class DefaultApiViewWriter implements ApiViewWriter {
	private final static Logger LOG = Logger.getLogger(DefaultApiViewWriter.class);
	private static boolean scanfed = false;
	
	protected String getTemplateName() {
		return "api.ftlh";
	}
	
	
	@Override
	public void writeIndex(HttpServletRequest request, HttpServletResponse response, String lang, Properties props)
			throws IOException {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("lang", lang);
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		root.put("basePath", basePath);
		String host = request.getServerName() + ":" + request.getServerPort() + path;
		String suffix = props.getProperty("suffix");
		if(StringUtils.isBlank(suffix)) {
			suffix = "";
		}
		root.put("getApisUrl","http://" + host + "/api" + suffix);
		root.put("apiDescription", props.getProperty("apiDescription"));
		root.put("apiTitle", props.getProperty("apiTitle"));
		root.put("apiVersion", props.getProperty("apiVersion"));
		root.put("suffix", suffix);
        Template template = FreemarkerUtils.getTemplate(getTemplateName());
        response.setContentType("text/html;charset=utf-8");
        Writer out = response.getWriter();
        try {
			template.process(root, out);
		} catch (TemplateException e) {
			throw new IOException(e);
		}
        out.flush();
        out.close();
	}

	@Override
	public void writeApis(HttpServletRequest request, HttpServletResponse response, Properties props)
			throws Exception {
		APIParseable restParser = APIParser.newInstance(props);
		response.setContentType("application/json;charset=utf-8");
		String devMode = props.getProperty("devMode");
		if(Boolean.valueOf(devMode)) {
			Object apis = restParser.parseAndNotStore();
			JSONWriter writer = new JSONWriter(response.getWriter());
			writer.writeObject(apis);
			writer.flush();
			writer.close();
		} else {
			if(!scanfed) {
				restParser.parse();
				scanfed = true;
			}
			byte[] bs = Files.readAllBytes(Paths.get(props.getProperty("apiFile")));
			OutputStream out = response.getOutputStream();
			out.write(bs);
			out.flush();
			out.close();
		}
	}

	/**
	 * @since 1.2.2
	 */
	protected String buildResourcePath(HttpServletRequest request, Properties config) {
		String uri = request.getRequestURI();
		String suffix = (String) config.get("suffix");
		if(suffix != null) {
			int index = uri.lastIndexOf(suffix);
			if(index > 0) {
				 uri = uri.substring(0, index);
			}
		}
		String path = uri.substring(uri.indexOf("statics")+7);
		path = "com/cpj/swagger/support/internal/statics"+path;
		return path;
	}
	
	@Deprecated
	protected String buildResourcePath(HttpServletRequest request) {
		return buildResourcePath(request, null);
	}
	
	@Override
	public void writeStatic(HttpServletRequest request, HttpServletResponse response, Properties props) throws IOException {
		String path = buildResourcePath(request, props);
		LOG.debug("获取web资源文件： " + path);
		String contentType = FileTypeMap.getContentType(path);
		response.setContentType(contentType);
		InputStream resource = DefaultApiViewWriter.class.getClassLoader().getResourceAsStream(path);
		if(resource == null) {
			response.sendError(404);
			return;
		}
		
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			byte[] buff = new byte[512];
			int len = -1;
			while((len=resource.read(buff))!=-1) {
				out.write(buff, 0, len);
			}
			out.flush();
		} finally {
			try {
				if(resource != null) {
					resource.close();
				}
			}catch(IOException e) {
			}
			try {
				if(out != null) {
					out.close();
				}
			}catch(IOException e) {
			}
		}
	}
	
	@Deprecated
	@Override
	public void writeStatic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeStatic(request, response, null);
	}
}
