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
package com.cpjit.swagger4j.specification

/**
 * @author yonghuan
 * @since 2.1.6
 */
class SpecificationTranslatorImpl : SpecificationTranslator {

    override fun translate(spec: Specification): Map<String, Any?> {
        val doc = mutableMapOf<String, Any?>()
        doc["swagger"] = spec.swagger
        doc["host"] = spec.host
        doc["basePath"] = spec.basePath
        doc["schemes"] = spec.schemes
        val definitions = mutableMapOf<String, Any>()
        spec.definitions
                .filter { it.name != null }
                .forEach { definitions[it.name!!] = translateSchema(it) }
        if (definitions.isNotEmpty()) {
            doc["definitions"] = definitions
        }
        val paths = mutableMapOf<String, Any>()
        spec.paths.forEach {
            val api = mutableMapOf<String, Any>()
            paths[it.url] = api
            if (it.get != null) {
                api["get"] = translateOperation(it.get!!)
            }
            if (it.delete != null) {
                api["delete"] = translateOperation(it.delete!!)
            }
            if (it.head != null) {
                api["head"] = translateOperation(it.head!!)
            }
            if (it.options != null) {
                api["options"] = translateOperation(it.options!!)
            }
            if (it.patch != null) {
                api["patch"] = translateOperation(it.patch!!)
            }
            if (it.post != null) {
                api["post"] = translateOperation(it.post!!)
            }
            if (it.put != null) {
                api["put"] = translateOperation(it.put!!)
            }
        }
        if (paths.isNotEmpty()) {
            doc["paths"] = paths
        }
        return doc
    }

    private fun translateSchema(schema: Schema): Map<String, Any> {
        val s = mutableMapOf<String, Any?>()
        s["type"] = schema.type
        s["format"] = schema.format
        s["required"] = schema.required
        if (schema.ref != null) {
            s["\$ref"] = translateReference(schema.ref!!)
        }
        val properties = mutableMapOf<String, Any>()
        schema.properties
                ?.filter { it.name != null }
                ?.forEach { properties[it.name!!] = translateProperty(it) }
        if (properties.isNotEmpty()) {
            s["properties"] = properties
        }
        return s.filterValues {
            it != null
        } as Map<String, Any>
    }

    private fun translateProperty(property: Property): Map<String, Any> {
        val prop = mutableMapOf<String, Any?>()
        prop["description"] = property.description
        prop["enum"] = property.enum
        prop["format"] = property.format
        prop["type"] = property.type
        if (property.ref != null) {
            prop["\$ref"] = translateReference(property.ref!!)
        }
        return prop.filterValues {
            it != null
        } as Map<String, Any>
    }

    private fun translateReference(reference: Reference) = "#/definitions/${reference.ref}"

    private fun translateOperation(operation: Operation): Map<String, Any> {
        val opt = mutableMapOf<String, Any?>()
        opt["tags"] = operation.tags
        opt["consumes"] = operation.consumes;
        opt["deprecated"] = operation.deprecated
        opt["description"] = operation.description
        opt["operationId"] = operation.operationId
        opt["produces"] = operation.produces
        opt["summary"] = operation.summary
        opt["schemes"] = operation.schemes
        var params = emptyList<Map<String, Any>>()
        if (operation.parameters != null) {
            params = operation.parameters!!.mapNotNull { translateParameter(it) }
        }
        if (params.isNotEmpty()) {
            opt["parameters"] = params
        }
        return opt.filterValues {
            it != null
        } as Map<String, Any>
    }

    private fun translateParameter(parameter: Parameter): Map<String, Any> {
        val param = mutableMapOf<String, Any?>()
        param["type"] = parameter.type
        param["format"] = parameter.format
        param["description"] = parameter.description
        param["enum"] = parameter.enum
        param["in"] = parameter.`in`
        param["items"] = parameter.items
        param["required"] = parameter.required
        if (parameter.schema != null) {
            param["schema"] = translateSchema(parameter.schema!!)
        }
        param["default"] = parameter.default
        param["name"] = parameter.name
        return param.filterValues {
            it != null
        } as Map<String, Any>
    }

}