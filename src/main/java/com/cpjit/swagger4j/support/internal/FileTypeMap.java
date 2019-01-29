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
package com.cpjit.swagger4j.support.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

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
		if(StringUtils.isBlank(mime)) {
			mime = "application/octet-stream";
		}
		return mime;
	}
}
