/*
 *
 * Copyright (c) 2011, 2016 CPJ and/or its affiliates. All rights reserved.
 * 
 */
package com.cpj.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注api分类信息。
 * <p>
 * 从版本1.2.2开始，{@link APITag}可以放置在类上面用来说明api分类信息。
 * </p>
 * @author yonghaun
 * @since 1.0.0
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface APITag {
	/** 名称 */
	String value();

	/** 说明 */
	String description();
}
