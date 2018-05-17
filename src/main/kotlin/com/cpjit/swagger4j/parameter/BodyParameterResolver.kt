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
import com.cpjit.swagger4j.specification.*
import org.apache.commons.lang3.StringUtils

/**
 * @author yonghuan
 * @since 2.1.6
 */
internal class BodyParameterResolver : ParameterResolver, AbstractParameterResolver() {

    override fun supportsParameter(api: Api, param: Param): Boolean = "body".equals(param.`in`, ignoreCase = true)

    override fun resolveParameter(url: String, api: Api, param: Param, parameters: MutableList<Parameter>, spec: Specification) {
        val body = bodyParameter(parameters)

        val definitionName = url.replace("/".toRegex(), "_")
        var definition = spec.definitions.firstOrNull { it.name.equals(definitionName) }
        if (definition == null) {
            definition = Schema(definitionName)
            definition.type = "object"
            definition.properties = mutableListOf()
            definition.required = mutableListOf()
            spec.definitions.add(definition)
            var schema = Schema("")
            schema.ref = Reference(definitionName)
            body.schema = schema
        }
        var properties: MutableList<Property> = definition.properties!!
        var required: MutableList<String> = definition.required!!
        val property = Property()
        if (param.dataType != DataType.UNKNOWN) {
            property.name = param.name
            property.type = param.dataType.type
            property.format = param.dataType.format
        } else {
            property.type = param.type
            property.format = param.format
        }
        property.description = param.description
        if (param.required) { // 为必须参数
            required.add(param.name)
        }
        if (StringUtils.isNotBlank(param.items)) { // 可选值
             val type = if (param.dataType != DataType.UNKNOWN) param.dataType.type else param.type
         property.enum = parseOptionalValue(type, param.items.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
          }
        properties.add(property)
    }

    private fun bodyParameter(parameters: MutableList<Parameter>): Parameter {
        var body: Parameter? = parameters.firstOrNull { it.name == "body" && it.`in` == "body" }
        if (body == null) {
            body = Parameter("body", "body")
            parameters.add(body)
        }
        return body;
    }
}
