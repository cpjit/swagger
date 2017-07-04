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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口的请求参数。
 * 
 * @author yonghaun
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
	/** 
	 * 输入参数类型，可取如下值：
	 * <ul>
	 *  <li>query - 参数拼接到url中</li>
	 *  <li>body - 参数直接放入请求体中</li>
	 *  <li>path - restful风格的参数传递</li>
	 *  <li>header - 参数放在请求头中</li>
	 *  <li>formData - 参数通过form表单提交</li>
	 * </ul> 
	 * 
	 *  <p><b>Note：</b>
	 *  <ul>
	 *   <li>当前请求方式为POST的时候，默认值为formData</li>
	 *   <li>请求方式为非POST的时候，默认值为query</li>
	 *  </ul>
	 */
	String in() default "";

	/** 参数名 */
	String name() default "";

	/** 
	 * <p>数据类型，与{@link #format()}一起指定请求参数的数据类型。
	 * {@link #type()} 和 {@link #format()}的可选值如下：
	   <table border="0" cellpadding="1" cellspacing="0">
		<thead>
		<tr bgcolor="#EFEFEF" style="border: 1px solid #cccccc;">
		<th align="left">通用名</th>
		<th align="left"><a href="#dataTypeType"><code>type</code></a></th>
		<th align="left"><a href="#dataTypeFormat"><code>format</code></a></th>
		<th align="left">备注</th>
		</tr>
		</thead>
		<tbody>
		<tr bgcolor="#FFFFFF">
		<td>integer</td>
		<td><code>integer</code></td>
		<td><code>int32</code></td>
		<td>signed 32 bits</td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>long</td>
		<td><code>integer</code></td>
		<td><code>int64</code></td>
		<td>signed 64 bits</td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>float</td>
		<td><code>number</code></td>
		<td><code>float</code></td>
		<td></td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>double</td>
		<td><code>number</code></td>
		<td><code>double</code></td>
		<td></td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>string</td>
		<td><code>string</code></td>
		<td></td>
		<td></td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>byte</td>
		<td><code>string</code></td>
		<td><code>byte</code></td>
		<td>base64 encoded characters</td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>binary</td>
		<td><code>string</code></td>
		<td><code>binary</code></td>
		<td>any sequence of octets</td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>boolean</td>
		<td><code>boolean</code></td>
		<td></td>
		<td></td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>date</td>
		<td><code>string</code></td>
		<td><code>date</code></td>
		<td>As defined by <code>full-date</code> - <a href="http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a></td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>dateTime</td>
		<td><code>string</code></td>
		<td><code>date-time</code></td>
		<td>As defined by <code>date-time</code> - <a href="http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a></td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>password</td>
		<td><code>string</code></td>
		<td><code>password</code></td>
		<td>Used to hint UIs the input needs to be obscured.</td>
		</tr>
		</tbody>
	 	</table>
	 * @deprecated 在版本1.2.2中被废弃，建议使用{@link Param#dataType()}替代。	
	 * @see #format()
	 * @see DataType
	 **/
	@Deprecated
	String type() default "";
	
	/** 
	 *  数据格式，与{@link #type()}一起指定请求参数的数据类型。
	 *  @deprecated 在版本1.2.2中被废弃，建议使用{@link Param#dataType()}替代。
	 *  @see #type()
	 *  @see DataType
	 * */
	@Deprecated
	String format() default "";
	
	/**
	 * 请求参数的数据类型和格式。
	 * @since 1.2.2
	 */
	DataType dataType() default DataType.UNKNOWN;
	
	/** 参数说明 */
	String description() default "";

	/** 是否必须，默认是false */
	boolean required() default false;

	/**
	 * 可选值
	 * @see Item
	 * */
	String items() default "";

	/**
	 * 包装对象
	 * @see APISchema
	 * */
	String schema() default "";

	/**
	 * 默认值。
	 * @since 2.1.0
	 */
	String defaultValue() default "";

}
