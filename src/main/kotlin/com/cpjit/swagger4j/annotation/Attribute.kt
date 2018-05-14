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
 * @author yonghaun
 * @since 1.0.0
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention
annotation class Attribute(
        /** 属性名  */
        val value: String,
        /** 数据类型  */
        val type: String,
        /** 数据格式  */
        val format: String,
        /** 属性说明  */
        val description: String,
        /** 可选值  */
        val optionalValue: Array<String> = arrayOf(),
        /** 必须 ，默认是false  */
        val required: Boolean = false)
