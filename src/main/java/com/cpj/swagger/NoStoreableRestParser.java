/*
 *
 * Copyright (c) 2011, 2016 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger;

/**
 * 解析接口，但不把解析结果保存到文件中。
 * 
 * @author yonghuan
 * @since 1.0.0
 */
public interface NoStoreableRestParser {
	/**
	 * 解析接口，但不把解析结果保存到文件中。
	 * 
	 * @return 解析结果
	 */
	Object parseAndNotStore() throws Exception;
}
