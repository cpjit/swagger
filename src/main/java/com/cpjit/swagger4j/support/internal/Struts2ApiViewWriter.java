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
package com.cpjit.swagger4j.support.internal;

import java.util.Properties;

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
	
	@Override
	protected String buildResourcePath(HttpServletRequest request, Properties config) {
		return buildResourcePath(request);
	}
}
