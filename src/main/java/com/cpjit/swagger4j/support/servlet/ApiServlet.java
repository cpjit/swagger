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
package com.cpjit.swagger4j.support.servlet;

import com.cpjit.swagger4j.support.Constants;
import com.cpjit.swagger4j.support.internal.ApiViewWriter;
import com.cpjit.swagger4j.support.internal.DefaultApiViewWriter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yonghuan
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class ApiServlet extends HttpServlet implements Constants {
	
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
		if(StringUtils.isBlank(lang)) {
			lang = "zh-ch";
		}
		apiViewWriter.writeIndex(request, response, lang);
	}
	
	/*
	 * @since 1.2.0
	 */
	private void queryStatic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		apiViewWriter.writeStatic(request, response);
	}
	
	private void queryApi(HttpServletRequest request, HttpServletResponse response) throws Exception {
		apiViewWriter.writeApis(request, response);
	}

}
