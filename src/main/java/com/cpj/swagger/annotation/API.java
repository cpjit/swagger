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
	
	/** 
	 * 接口是否已经被废弃，默认是false。
	 * @since 1.2.2
	 */
	boolean deprecated() default false;
	
	/**
	 * 是否隐藏接口。
	 * @since 1.2.2
	 */
	boolean hide() default false;
}
