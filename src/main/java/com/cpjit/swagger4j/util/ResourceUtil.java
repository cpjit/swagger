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
package com.cpjit.swagger4j.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查找资源相关的工具方法。
 * <p><b>Note:</b>该类用于从classpath下查找资源。</p>
 * @author yonghuan
 * @since 1.2.2
 */
public final class ResourceUtil {
	private final static Logger LOG = LoggerFactory.getLogger(ResourceUtil.class);
	private final static Class<?> DEFAULT_LOADER = ResourceUtil.class;
	
	private ResourceUtil() {
		throw new AssertionError("不允许实例化 " + ResourceUtil.class.getName());
	}
	
	/**
	 * 根据资源文件名获取资源文件的URL。
	 * @param loader
	 * @param name 资源文件名
	 * @return 资源文件的URL。
	 */
	public static URL getResource(Class<?> loader,String name) {
		URL url = null;
		Class<?> l = loader;
		if(l == null) {
			l = DEFAULT_LOADER;
		}
		url = l.getResource("/" + name);
		if(url == null) {
			url = ClassLoader.getSystemResource(name);
		}
		return url;
	}
	
	/**
	 * 根据资源文件名获取资源文件的URL。
	 * @param name 资源文件名
	 * @return 资源文件的URL。
	 */
	public static URL getResource(String name) {
		return getResource(DEFAULT_LOADER, name);
	}
	
	/**
	 * 根据资源文件名获取资源文件。
	 * @param loader 
	 * @param name 资源文件名
	 * @return 资源文件
	 * @since 1.0.4
	 */
	public static File getResourceAsFile(Class<?> loader,String name) {
		URL url = getResource(loader, name);
		try {
			Path path = Paths.get(url.toURI());
			return path.toFile();
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	/**
	 * 根据资源文件名获取资源文件。
	 * @param name
	 * @return 资源文件
	 * @since 1.0.4
	 */
	public static File getResourceAsFile(String name) {
		return getResourceAsFile(DEFAULT_LOADER, name);
	}
	
	public static InputStream getResourceAsStream(Class<?> loader,String name) {
		URL url = getResource(loader, name);
		if(url == null) { // 未找到资源
			return null;
		}
		
		InputStream is = null;
		try {
			is = Files.newInputStream(Paths.get(url.toURI()));
		} catch (IOException ioe) {
			LOG.error(String.join("", "查找资源文件 ", name, " 发生错误"), ioe);
		} catch (URISyntaxException use) {
			LOG.error(String.join("",  "查找资源文件 ", name, " 发生错误"), use);
		}
		return is;
	}
	
	public static InputStream getResourceAsStream(String name) {
		return getResourceAsStream(DEFAULT_LOADER, name);
	}
}
