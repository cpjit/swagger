/*
 *
 * Copyright (c) 2011, 2016 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.support.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.cpj.common.util.TextUtil;

/**
 * @author yonghuan
 * @since 1.2.0
 */
class FileTypeMap {
	private final static Map<String, String> extension2mime = new HashMap<String, String>();
	static {
		extension2mime.put("css", "text/css");
		extension2mime.put("js", "application/javascript");
		extension2mime.put("jpg", "image/jpeg");
		extension2mime.put("png", "image/jpeg");
		extension2mime.put("svg", "image/svg+xml");
	}
	
	public final static String getContentType(String file) {
		String extension = FilenameUtils.getExtension(file);
		String mime = extension2mime.get(extension);
		if(TextUtil.isEmpty(mime)) {
			mime = "application/octet-stream";
		}
		return mime;
	}
}
