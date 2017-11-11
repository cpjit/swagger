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
package com.cpjit.swagger4j;

/**
 * API遵循的协议。
 * 
 * @author yonghaun
 * @since 1.0.0
 */
public class License {
	private String name;
	private String url;

	public License() {}
	
	/**
	 * @since 1.0.3
	 */
	public License(String name, String url) {
		this.name = name;
		this.url = url;
	}

	/**
	 * @return 协议名
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 协议名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return 协议主页
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url 协议主页
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
