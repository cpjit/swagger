/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.support.internal;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yonghuan
 * @since 1.2.0
 */
public interface ApiViewWriter {

	void writeIndex( HttpServletRequest request, HttpServletResponse response, String lang, Properties props) throws IOException;
	
	void writeApis(HttpServletRequest request, HttpServletResponse response, Properties props) throws Exception;
	
	@Deprecated
	void writeStatic(HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	/**
	 * @since 1.2.2
	 */
	void writeStatic(HttpServletRequest request, HttpServletResponse response, Properties props) throws IOException;
}
