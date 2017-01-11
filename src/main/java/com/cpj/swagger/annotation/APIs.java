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
 * 该注解表示类中的方法可以作为接口。
 * 
 * @author yonghaun
 * @since 1.0.0
 * @see API
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface APIs {
	/**
	 * 命名空间。
	 */
	String value() default "";
	
	/**
	 * 是否隐藏命名空间。
	 * @since 1.2.2
	 */
	boolean hide() default false;
}
