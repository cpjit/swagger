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
import com.cpjit.swagger4j.annotation.Param
import com.cpjit.swagger4j.specification.*
import org.apache.commons.lang3.StringUtils
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @author yonghuan
 * @since 2.1.6
 */
internal class SchemaObjectParameterResolver : ParameterResolver, AbstractParameterResolver() {

    override fun supportsParameter(api: Api, param: Param): Boolean =
            param.ref != Unit::class || StringUtils.isNoneBlank(param.schema)

    override fun resolveParameter(url: String, api: Api, param: Param, parameters: MutableList<Parameter>, spec: Specification) {
        val parameter = Parameter("body", "body")
        val isMultipart = api.consumes.any({ "multipart/form-data" == it })
        if (isMultipart) {
            // 当请求的Content-Type为multipart/form-data将忽略复杂类型的参数
            throw IllegalArgumentException(arrayOf("请求的Content-Type为multipart/form-data，将忽略复杂类型的请求参数[ ", param.schema, " ]").joinToString(""))
        }
        val ref: String
        if (param.ref != Unit::class) {
            ref = class2Definition(param.ref, spec)
        } else {
            ref = param.schema
        }
        val schema = Schema()
        schema.type = null
        // 引用定义
        schema.ref = Reference(ref)
        parameter.schema = schema
        parameters.add(parameter)
    }

    private fun class2Definition(clazz: KClass<*>, spec: Specification): String {
        var definitions = spec.definitions
        if (definitions == null) {
            definitions = mutableListOf()
            spec.definitions = definitions
        }
        var definition = definitions.firstOrNull { it.name == clazz.qualifiedName }
        if (definition == null) {
            definition = createSchema(clazz, spec)
        }
        return definition.name!!
    }

    private fun createSchema(clazz: KClass<*>, spec: Specification): Schema {
        val schema = Schema(clazz.simpleName!!)
        val fields = clazz.members
                .filter { it is KProperty }
                .map { it as KProperty }

        val properties = mutableListOf<Property>()
        for (field in fields) {
            var property = Property()
            property.name = field.name
            val type = field.returnType.classifier
            if (type == Int::class) {
                property.type = "integer"
                property.format = "int32"
            } else if (type == Long::class) {
                property.type = "integer"
                property.format = "int64"
            } else if (type == Float::class) {
                property.type = "number"
                property.format = "float32"
            } else if (type == Double::class) {
                property.type = "number"
                property.format = "float64"
            } else if (type == String::class) {
                property.type = "string"
            } else if (type == Boolean::class) {
                property.type = "boolean"
            } else if (type == List::class) {
            } else if (type == Set::class) {
            } else {
                val schemaProperty = class2Definition(field.returnType.classifier as KClass<*>, spec)
                property.ref = Reference(schemaProperty)
            }
            properties.add(property)
        }

        if (properties.isNotEmpty()) {
            schema.properties = properties
        }
        spec.definitions.add(schema)
        return schema
    }

}