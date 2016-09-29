/*
 *
 * Copyright (c) 2011, 2016 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger;

/**
 * @author yonghaun
 * @since 1.0.0
 */
public interface APIParseable extends NoStoreableRestParser {
	
	/**
	 *  解析接口并把结果已JSON格式写入文件。
	 *  @throws Exception
	 */
	void parse() throws Exception;
}
