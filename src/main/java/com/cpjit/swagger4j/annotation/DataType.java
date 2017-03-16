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
package com.cpjit.swagger4j.annotation;

/**
 * 请求参数的数据类型。
 * @author yonghuan
 * @since 1.2.2
 */
public enum DataType {
	/** 未知数据类型 */
	UNKNOWN(null, null),
	/** 32位的有符号整数 */
	INTEGER("integer", "int32"),
	/** 64位的有符号整数 */
	LONG("integer", "int64"),
	/** 单精度浮点数 */
	FLOAT("number", "float"),
	/** 双精度浮点数 */
	DOUBLE("number", "double"),
	/** 字符串 */
	STRING("string", null),
	/** base64编码的字符 */
	BYTE("string", "byte"),
	/** 任何的八位字节序列 */
	BINARY("string", "binary"), 
	/** boolean类型 */
	BOOLEAN("boolean", null),
	/** 日期 */
	DATE("string", "date"),
	/** 日期和时间 */
	DATE_TIME("string", "date-time"),
	/** 密码字符串 */
	PASSWORD("string", "password"),
	/** 请求参数只能是指定值中的某一项 */
	ARRAY("array", null),
	/** 文件 */
	FILE("file", "");

	private String type;
	public String type() {
		return type;
	}

	private String format;
	public String format() {
		return format;
	}

	private DataType(String type, String format) {
		this.type = type;
		this.format = format;
	}
}
