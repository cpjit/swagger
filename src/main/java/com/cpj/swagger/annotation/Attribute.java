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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
	/** 属性名 */
	String value();

	/** 数据类型 */
	String type();

	/** 数据格式 */
	String format();

	/** 属性说明 */
	String description();
	
	/** 可选值 */
	String[] optionalValue() default {};

	/** 必须 ，默认是false */
	boolean required() default false;
}
