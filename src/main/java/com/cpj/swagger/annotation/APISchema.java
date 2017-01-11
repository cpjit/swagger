/*
 *
 * Copyright (c) 2011, 2017 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yonghaun
 * @since 1.0.0
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
public @interface APISchema {
	/** 名称 */
	String value();

	/** 类型，默认是object */
	String type() default "object";

	/** 属性集合 */
	APISchemaPropertie[] properties();

	/** 序列化为XML的根元素名 */
	String xml();
}
