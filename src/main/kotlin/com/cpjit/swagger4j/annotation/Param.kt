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
 * 接口的请求参数。
 *
 * @author yonghaun
 * @since 1.0.0
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention
annotation class Param(
        /**
         * 输入参数类型，可取如下值：
         *
         *  * query - 参数拼接到url中
         *  * body - 参数直接放入请求体中
         *  * path - restful风格的参数传递
         *  * header - 参数放在请求头中
         *  * formData - 参数通过form表单提交
         *
         *
         *
         * **Note：**
         *
         *  * 当前请求方式为POST或PUT的时候，默认值为formData
         *  * 其他请求方式为的默认值为query
         *
         */
        val `in`: String = "",
        /** 参数名  */
        val name: String = "",
        /**
         *
         * 数据类型，与[.format]一起指定请求参数的数据类型。
         * [.type] 和 [.format]的可选值如下：
         * <table border="0" cellpadding="1" cellspacing="0">
         * <thead>
         * <tr bgcolor="#EFEFEF" style="border: 1px solid #cccccc;">
         * <th align="left">通用名</th>
         * <th align="left">[`type`](#dataTypeType)</th>
         * <th align="left">[`format`](#dataTypeFormat)</th>
         * <th align="left">备注</th>
        </tr> *
        </thead> *
         * <tbody>
         * <tr bgcolor="#FFFFFF">
         * <td>integer</td>
         * <td>`integer`</td>
         * <td>`int32`</td>
         * <td>signed 32 bits</td>
        </tr> *
         * <tr bgcolor="#EFEFEF">
         * <td>long</td>
         * <td>`integer`</td>
         * <td>`int64`</td>
         * <td>signed 64 bits</td>
        </tr> *
         * <tr bgcolor="#FFFFFF">
         * <td>float</td>
         * <td>`number`</td>
         * <td>`float`</td>
         * <td></td>
        </tr> *
         * <tr bgcolor="#EFEFEF">
         * <td>double</td>
         * <td>`number`</td>
         * <td>`double`</td>
         * <td></td>
        </tr> *
         * <tr bgcolor="#FFFFFF">
         * <td>string</td>
         * <td>`string`</td>
         * <td></td>
         * <td></td>
        </tr> *
         * <tr bgcolor="#EFEFEF">
         * <td>byte</td>
         * <td>`string`</td>
         * <td>`byte`</td>
         * <td>base64 encoded characters</td>
        </tr> *
         * <tr bgcolor="#FFFFFF">
         * <td>binary</td>
         * <td>`string`</td>
         * <td>`binary`</td>
         * <td>any sequence of octets</td>
        </tr> *
         * <tr bgcolor="#EFEFEF">
         * <td>boolean</td>
         * <td>`boolean`</td>
         * <td></td>
         * <td></td>
        </tr> *
         * <tr bgcolor="#FFFFFF">
         * <td>date</td>
         * <td>`string`</td>
         * <td>`date`</td>
         * <td>As defined by `full-date` - [RFC3339](http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14)</td>
        </tr> *
         * <tr bgcolor="#EFEFEF">
         * <td>dateTime</td>
         * <td>`string`</td>
         * <td>`date-time`</td>
         * <td>As defined by `date-time` - [RFC3339](http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14)</td>
        </tr> *
         * <tr bgcolor="#FFFFFF">
         * <td>password</td>
         * <td>`string`</td>
         * <td>`password`</td>
         * <td>Used to hint UIs the input needs to be obscured.</td>
        </tr> *
        </tbody> *
        </table> *
         * @see .format
         * @see DataType
         */
        @Deprecated("在版本1.2.2中被废弃，建议使用{@link Param#dataType()}替代。")
        val type: String = "",
        /**
         * 数据格式，与[.type]一起指定请求参数的数据类型。
         * @see .type
         * @see DataType
         *
         */
        @Deprecated("在版本1.2.2中被废弃，建议使用{@link Param#dataType()}替代")
        val format: String = "",
        /**
         * 请求参数的数据类型和格式。
         * @since 1.2.2
         */
        val dataType: DataType = DataType.UNKNOWN,
        /** 参数说明  */
        val description: String = "",
        /** 是否必须，默认是false  */
        val required: Boolean = false,
        /**
         * 可选值
         * @see Item
         *
         */
        val items: String = "",
        /**
         * 包装对象
         * @see APISchema
         *
         */
        val schema: String = "",
        /**
         * 默认值。
         * @since 2.1.0
         */
        val defaultValue: String = "")
