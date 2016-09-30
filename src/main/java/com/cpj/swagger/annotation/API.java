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
 * 该注解表示方法为一个接口。
 * 
 * @author yonghaun
 * @since 1.0.0
 * @see APIs
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface API {

	/** 接口地址 */
	String value();

	/** 分类 */
	String[] tags() default {};

	/** HTTP请求方式，默认是POST */
	String method() default "POST";

	/** 接口功能简述 */
	String summary() default "";

	/** 接口功能详细说明 */
	String description() default "";

	/** 操作ID, 默认与方法名相同 */
	String operationId() default "";

	/**
	 * 允许的请求MIME，比如：multipart/form-data、application/xml、application/
	 * json默认是application/json; charset=utf-8。
	 * <p>
	 * <strong style="color: red;">注意：</strong>
	 * <p>
	 * 当为<b>multipart/form-data</b>时，{@link #Param}
	 * 的in()必须为formData，此时如果{@link #Param}
	 * 的schema不为空或者不为空串，那么请求参数将被忽略。 但是in()为path、header的
	 * {@link #Param}不用遵循此规则。
	 * 
	 * @see Param
	 */
	String[] consumes() default { "application/json; charset=utf-8" };

	/** 响应MIME，默认是application/json; charset=utf-8。 */
	String[] produces() default { "application/json; charset=utf-8" };

	/** 请求参数 */
	Param[] parameters() default {};
}
