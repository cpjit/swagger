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
package com.cpjit.swagger4j.parameter

import com.cpjit.swagger4j.Api
import com.cpjit.swagger4j.annotation.DataType
import com.cpjit.swagger4j.annotation.Param
import com.cpjit.swagger4j.specification.Parameter
import com.cpjit.swagger4j.specification.Specification
import org.apache.commons.lang3.StringUtils

/**
 * @author yonghuan
 * @since 2.1.6
 */
internal class PrimitiveParameterResolver : ParameterResolver, AbstractParameterResolver() {

    override fun supportsParameter(api: Api, param: Param): Boolean = true

    override fun resolveParameter(url: String, api: Api, param: Param, parameters: MutableList<Parameter>, spec: Specification) {
        val parameter: Parameter
        var requestParamType: String
        var requestParamFormat: String
        if (param.dataType !== DataType.UNKNOWN) {
            // since 1.2.2
            requestParamType = param.dataType.type
            requestParamFormat = param.dataType.format
        } else {
            requestParamType = param.type
            requestParamFormat = param.format
        }
        val isMultipart = api.consumes.any({ "multipart/form-data" == it })
        if (isMultipart && "path" != param.`in` && "header" != param.`in`) {
            // 包含文件上传
            parameter = Parameter(param.name, "formData")
            parameter.type = requestParamType
        } else {
            // 不包含文件上传
            var `in` = param.`in`
            if (StringUtils.isBlank(`in`)) {
                `in` = when ("post".equals(api.method, ignoreCase = true) || "put".equals(api.method, ignoreCase = true)) {
                    true -> "formData"
                    false -> "query"
                }
            }
            parameter = Parameter(param.name, `in`)
            parameter.type = requestParamType
            if (StringUtils.isNotBlank(requestParamFormat)) {
                parameter.format = requestParamFormat
            }
        }
        parameter.description = param.description
        parameter.required = param.required
        if (StringUtils.isNotBlank(param.items)) {
            // 兼容 2.1.6 以前的版本
            if (requestParamType != "array") {
                throw IllegalArgumentException(arrayOf("请求参数 [ ", param.name, " ]存在可选值(items)的时候，请求参数类型(type)的值只能为array").joinToString(""))
            }
            if (spec.items != null) {
                val item = spec.items!![param.items.trim({ it <= ' ' })]
                if (item != null) { // 可选值
                    val i = Parameter("", "")
                    i.type = item.type
                    i.default = item.defaultValue
                    i.enum = parseOptionalValue(item.type, item.optionalValue)
                    parameter.items = i
                }
            }
        }
        if (StringUtils.isNotBlank(param.defaultValue)) {
            parameter.default = param.defaultValue
        }
        parameters.add(parameter);
    }
}