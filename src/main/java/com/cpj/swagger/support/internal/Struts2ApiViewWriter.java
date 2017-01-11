/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.support.internal;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yonghuan
 * @since 1.2.0
 */
public class Struts2ApiViewWriter extends DefaultApiViewWriter implements ApiViewWriter {
	
	protected String getTemplateName() {
		return "api-struts2.ftlh";
	}
	
	@Override
	protected String buildResourcePath(HttpServletRequest request) {
		String url = request.getParameter("url");
		String path = "com/cpj/swagger/support/internal/statics/"+url;
		return path;
	}
}
