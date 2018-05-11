/*
 * Copyright 2011-2018 CPJIT Group.
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
package com.cpjit.swagger4j.annotation



/**
 * @author yonghuan
 * @since 2.1.5
 */
@Target(AnnotationTarget.FUNCTION)
@Retention
annotation class Delete(
        /**
         * 接口地址
         */
        val value: String = "",
        /**
         * 分类
         */
        val tags: Array<String> = arrayOf(),
        /**
         * 接口功能简述
         */
        val summary: String = "",
        /**
         * 接口功能详细说明
         */
        val description: String = "",
        /**
         * 操作ID, 默认与方法名相同
         */
        val operationId: String = "",
        /**
         * 允许的请求MIME，比如：multipart/form-data、application/xml、application/
         * json默认是application/json; charset=utf-8。
         *
         * **注意：**
         *
         *
         * 当为**multipart/form-data**时，[Param]
         * 的in()必须为formData，此时如果[Param]
         * 的schema不为空或者不为空串，那么请求参数将被忽略。 但是in()为path、header的
         * [Param]不用遵循此规则。
         *
         * @see Param
         */
        val consumes: Array<String> = arrayOf("application/json; charset=utf-8"),
        /**
         * 响应MIME，默认是application/json; charset=utf-8。
         */
        val produces: Array<String> = arrayOf("application/json; charset=utf-8"),
        /**
         * 请求参数
         */
        val parameters: Array<Param> = arrayOf(),
        /**
         * 接口是否已经被废弃，默认是false。
         */
        val deprecated: Boolean = false,
        /**
         * 是否隐藏接口。
         */
        val hide: Boolean = false)
