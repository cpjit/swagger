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
package com.cpjit.swagger4j

import com.alibaba.fastjson.JSONWriter
import com.cpjit.swagger4j.annotation.*
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import java.io.*
import java.lang.reflect.Method
import java.util.Arrays
import java.util.Properties
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 接口解析器。
 *
 * @author yonghaun
 * @since 1.0.0
 */
class APIParser private constructor(schemesStr: String, private val host: String,
                                    /**
                                     * @return 解析完成后存放JSON数据的文件路径。
                                     */
                                    val file: String, packageToScan: String,
                                    private val basePath: String, description: String, termsOfService: String, title: String, version: String, suffix: String) : APIParseable {
    private val resourcePatternResolver = PathMatchingResourcePatternResolver()
    private val metadataReaderFactory = CachingMetadataReaderFactory(this.resourcePatternResolver)
    private var schemes: Array<String>
    var suffix = ""
    var info: APIDocInfo
    /**
     * @return 待解析接口所在包
     */
    private val packageToScan: List<String>
    private var items: MutableMap<String, Item>? = null

    private var packages: Collection<Package>

    init {
        val schemes = when (StringUtils.isNotBlank(schemesStr)) {
            true -> schemesStr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            false -> arrayOf("http")
        }
        // 扫描class并生成文件所需要的参数
        this.schemes = schemes
        this.packageToScan = Arrays.asList(*packageToScan.split("[;,]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        try {
            packages = scanClass()
                    .map { it.`package` }
                    .toList()
        } catch (e: Exception) {
            throw IllegalStateException("扫描包信息失败", e)
        }

        this.suffix = suffix
        // API文档信息
        info = APIDocInfo()
        // info.setContact(contact);
        info.description = description
        // info.setLicense(license);
        info.termsOfService = termsOfService
        info.title = title
        info.version = version
    }

    @Throws(Exception::class)
    override fun parse() {
        /* 将结果写入文件 */
        val f = File(file)
        if (LOG.isDebugEnabled) {
            LOG.debug(arrayOf("生成的文件保存在=>", f.toString()).joinToString(""))
        }
        val writer = JSONWriter(OutputStreamWriter(FileOutputStream(f), "utf-8"))
        val api = parseAndNotStore() as APIDoc
        writer.writeObject(api)
        writer.flush()
        writer.close()
    }

    @Throws(Exception::class)
    override fun parseAndNotStore(): Any {
        val api = APIDoc()
        api.schemes = schemes
        api.host = host
        api.basePath = basePath
        api.info = info

        /* 解析全部item */
        items = parseItem()

        /* 解析全部tag */
        val tags = parseTag()
        api.tags = tags

        /* 解析全部definition */
        val definitions = parseDefinition()

        /* 解析全部path */
        val paths = parsePath(definitions)
        api.paths = paths
        api.definitions = definitions
        return api
    }

    @Throws(Exception::class)
    private fun parseItem(): MutableMap<String, Item> {
        val items = HashMap<String, Item>()
        packages.mapNotNull { it.getAnnotation(Items::class.java) }
                .forEach({
                    it.items.forEach {
                        items[it.value] = it
                    }
                })
        return items
    }


    /**
     * url -> [ path ]
     */
    @Throws(Exception::class)
    private fun parsePath(definitions: MutableMap<String, Any>): MutableMap<String, MutableMap<String, Path>> {
        val paths = mutableMapOf<String, MutableMap<String, Path>>()
        scanClass().forEach { clazz ->
            val apis = clazz.getAnnotation(APIs::class.java)
            if (apis?.hide == false) {
                scanAPIMethod(clazz)
                        .forEach {
                            api2Path(it, apis, paths, definitions)
                        }
            }
        }
        return paths
    }

    private fun parseApi(method: Method): Api? {
        val api = Api()
        val apiAnnotation = method.getAnnotation(API::class.java)
        if (apiAnnotation != null) {
            api.parameters = apiAnnotation.parameters
            api.tags = apiAnnotation.tags
            api.consumes = apiAnnotation.consumes
            api.deprecated = apiAnnotation.deprecated
            api.hide = apiAnnotation.hide
            api.description = apiAnnotation.description
            api.method = apiAnnotation.method
            api.operationId = apiAnnotation.operationId
            api.produces = apiAnnotation.produces
            api.summary = apiAnnotation.summary
            api.value = apiAnnotation.value
            return api
        }
        val get = method.getAnnotation(Get::class.java)
        if (get != null) {
            api.parameters = get.parameters
            api.tags = get.tags
            api.consumes = get.consumes
            api.deprecated = get.deprecated
            api.hide = get.hide
            api.description = get.description
            api.method = "GET"
            api.operationId = get.operationId
            api.produces = get.produces
            api.summary = get.summary
            api.value = get.value
            return api
        }
        val post = method.getAnnotation(Post::class.java)
        if (post != null) {
            api.parameters = post.parameters
            api.tags = post.tags
            api.consumes = post.consumes
            api.deprecated = post.deprecated
            api.hide = post.hide
            api.description = post.description
            api.method = "POST"
            api.operationId = post.operationId
            api.produces = post.produces
            api.summary = post.summary
            api.value = post.value
            return api
        }
        val put = method.getAnnotation(Put::class.java)
        if (put != null) {
            api.parameters = put.parameters
            api.tags = put.tags
            api.consumes = put.consumes
            api.deprecated = put.deprecated
            api.hide = put.hide
            api.description = put.description
            api.method = "PUT"
            api.operationId = put.operationId
            api.produces = put.produces
            api.summary = put.summary
            api.value = put.value
            return api
        }
        val delete = method.getAnnotation(Delete::class.java)
        if (delete != null) {
            api.parameters = delete.parameters
            api.tags = delete.tags
            api.consumes = delete.consumes
            api.deprecated = delete.deprecated
            api.hide = delete.hide
            api.description = delete.description
            api.method = "DELETE"
            api.operationId = delete.operationId
            api.produces = delete.produces
            api.summary = delete.summary
            api.value = delete.value
            return api
        }
        return null
    }

    private fun api2Path(method: Method, apis: APIs, paths: MutableMap<String, MutableMap<String, Path>>, definitions: MutableMap<String, Any>) {
        val service = parseApi(method)
        if (service!!.hide) {
            return
        }
        val isMultipart = hasMultipart(service)
        val url = when ("" == service.value) {
            true -> apis.value + suffix
            false -> arrayOf(apis.value, "/", service.value, suffix).joinToString("")
        }
        var path: MutableMap<String, Path>? = paths[url] // get/psot/put/delete
        if (path == null) {
            path = HashMap()
            paths[url] = path
        }

        var p: Path? = path[service.method]
        if (p == null) {
            p = Path()
            path[service.method.toLowerCase()] = p
        }
        if (StringUtils.isNotBlank(service.description)) {
            p.description = service.description
        } else {
            p.description = service.summary
        }
        if (StringUtils.isNotBlank(service.operationId)) {
            p.operationId = service.operationId
        } else { // 未设置operationId，
            p.operationId = method.name
        }
        var tags = service.tags.toList()
        if (service.tags.isEmpty()) {
            var ns = apis.value
            if (ns.startsWith("/")) {
                ns = ns.substring(1)
            }
            tags = listOf(ns)
        }
        p.tags = tags
        p.summary = service.summary
        if (isMultipart) { // multipart/form-data
            p.consumes = listOf("multipart/form-data")
        } else {
            p.consumes = service.consumes.toList()
        }
        p.produces = service.produces.toList()
        p.isDeprecated = service.deprecated
        p.parameters = parseParameters(url, service, isMultipart, definitions)
    }

    private fun parseParameters(url: String, service: Api, isMultipart: Boolean, definitions: MutableMap<String, Any>): List<MutableMap<String, Any>> {
        val parameters = ArrayList<MutableMap<String, Any>>() // 请求参数
        val body = HashMap<String, Any>()
        val properties = HashMap<String, Any>()
        val required = ArrayList<String>(service.parameters.size)
        val useBody = service.parameters
                .filter({ "body".equals(it.`in`, ignoreCase = true) })
                .count() > 0
        if (useBody) {
            val definition = HashMap<String, Any>()
            definition["type"] = "object"
            definition["properties"] = properties
            definition["required"] = required
            val definitionName = url.replace("/".toRegex(), "_")
            definitions[definitionName] = definition
            body["in"] = "body"
            body["name"] = "body"
            val ref = HashMap<String, Any>()
            ref["\$ref"] = "#/definitions/$definitionName"
            body["schema"] = ref
        }
        /* 解析参数，优先使用schema */
        for (paramAttr in service.parameters) {
            val parameter = HashMap<String, Any>()
            if (StringUtils.isNoneBlank(paramAttr.schema)) { // 处理复杂类型的参数
                if (isMultipart) { // 当请求的Content-Type为multipart/form-data将忽略复杂类型的参数
                    throw IllegalArgumentException(arrayOf("请求的Content-Type为multipart/form-data，将忽略复杂类型的请求参数[ ", paramAttr.schema, " ]").joinToString(""))
                }
                parameter["in"] = "body"
                parameter["name"] = "body"
                val ref = HashMap<String, Any>()
                ref["\$ref"] = "#/definitions/" + paramAttr.schema
                parameter["schema"] = ref
            } else if ("body".equals(paramAttr.`in`, ignoreCase = true)) {
                val propertie = HashMap<String, Any?>()
                if (paramAttr.dataType != DataType.UNKNOWN) {
                    propertie["type"] = paramAttr.dataType.type
                    propertie["format"] = paramAttr.dataType.format
                } else {
                    propertie["type"] = paramAttr.type
                    propertie["format"] = paramAttr.format
                }
                propertie["description"] = paramAttr.description
                if (paramAttr.required) { // 为必须参数
                    required.add(paramAttr.name)
                }
                if (StringUtils.isNotBlank(paramAttr.items)) { // 可选值
                    val type = if (paramAttr.dataType != DataType.UNKNOWN) paramAttr.dataType.type else paramAttr.type
                    propertie["enum"] = parseOptionalValue(type, paramAttr.items.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
                }
                properties[paramAttr.name] = propertie
                continue
            } else { // 简单类型的参数
                var requestParamType: String
                var requestParamFormat: String
                if (paramAttr.dataType !== DataType.UNKNOWN) { // since 1.2.2
                    requestParamType = paramAttr.dataType.type
                    requestParamFormat = paramAttr.dataType.format
                } else {
                    requestParamType = paramAttr.type
                    requestParamFormat = paramAttr.format
                }
                if (isMultipart && "path" != paramAttr.`in` && "header" != paramAttr.`in`) { // 包含文件上传
                    parameter["in"] = "formData"
                    parameter["type"] = requestParamType
                } else {
                    // 不包含文件上传
                    var `in` = paramAttr.`in`
                    if (StringUtils.isBlank(`in`)) {
                        `in` = when ("post".equals(service.method, ignoreCase = true) || "put".equals(service.method, ignoreCase = true)) {
                            true -> "formData"
                            false -> "query"
                        }
                    }
                    parameter["in"] = `in`
                    parameter["type"] = requestParamType
                    if (StringUtils.isNotBlank(requestParamFormat)) {
                        parameter["format"] = requestParamFormat
                    }
                }
                parameter["name"] = paramAttr.name
                parameter["description"] = paramAttr.description
                parameter["required"] = paramAttr.required
                if (StringUtils.isNotBlank(paramAttr.items)) {
                    if (requestParamType != "array") {
                        throw IllegalArgumentException(arrayOf("请求参数 [ ", paramAttr.name, " ]存在可选值(items)的时候，请求参数类型(type)的值只能为array").joinToString(""))
                    }
                    val item = items!![paramAttr.items.trim({ it <= ' ' })]
                    if (item != null) { // 可选值
                        val i = HashMap<String, Any>()
                        i["type"] = item.type
                        i["default"] = item.defaultValue
                        i["enum"] = parseOptionalValue(item.type, item.optionalValue)
                        parameter["items"] = i
                    }
                }
            }
            if (StringUtils.isNotBlank(paramAttr.defaultValue)) {
                parameter["defaultValue"] = paramAttr.defaultValue
            }
            parameters.add(parameter)
        }
        if (properties.size > 0) {
            parameters.add(body)
        }
        return parameters
    }

    private fun parseOptionalValue(type: String, values: Array<String>): Any {
        return when (type) {
            "string" -> values
            "boolean" -> values.map { java.lang.Boolean.parseBoolean(it) }
            "integer" -> values.map { Integer.parseInt(it) }
            else -> values.map { java.lang.Double.parseDouble(it) }
        }
    }

    /**
     * 判断接口的请求Content-Type是否为multipart/form-data。
     */
    private fun hasMultipart(service: Api): Boolean {
        return service.consumes.any({ "multipart/form-data" == it })
    }

    /**
     * 解析全部Tag。
     *
     * @return 全部Tag。
     */
    @Throws(Exception::class)
    private fun parseTag(): Collection<Tag> {
        // since1.2.2 先扫描被@APITag标注了的类
        val tags = scanClass()
                .mapNotNull { clazz -> clazz.getAnnotation(APITag::class.java) }
                .toMutableList()
        // 扫描package-info上面的@APITags
        packages.mapNotNull { pk -> pk.getAnnotation(APITags::class.java) }
                .forEach {
                    tags.addAll(it.tags)
                }
        return tags.map {
            val tag = Tag(it.value)
            tag.description = it.description
            tag
        }
    }

    /**
     * 解析全部definition。
     *
     * @return 全部definition
     */
    @Throws(Exception::class)
    private fun parseDefinition(): MutableMap<String, Any> {
        val definitions = HashMap<String, Any>()

        for (pk in packages) {
            val apiSchemas = pk.getAnnotation(APISchemas::class.java) ?: continue
            val schemas = apiSchemas.schemas
            for (schema in schemas) {
                val definition = HashMap<String, Any>()
                definition["type"] = schema.type
                val required = ArrayList<String>()
                definition["required"] = required
                val props = schema.properties
                val properties = HashMap<String, MutableMap<String, Any>>()
                for (prop in props) {
                    val propertie = HashMap<String, Any>()
                    definition["properties"] = properties

                    propertie["type"] = prop.type
                    propertie["format"] = prop.format
                    propertie["description"] = prop.description

                    if (prop.required) { // 为必须参数
                        required.add(prop.value)
                    }
                    if (prop.optionalValue.isNotEmpty()) { // 可选值
                        propertie["enum"] = parseOptionalValue(prop.type, prop.optionalValue)
                    }
                    properties[prop.value] = propertie
                }
                definitions[schema.value] = definition // 添加新的definition
            }
        }
        return definitions
    }

    /*
     * 扫描所有用注解{@link API}修饰了的方法。
     *
     * @return 所有用注解{@link API}修饰了的方法
     * @throws Exception
     */
    private fun scanAPIMethod(clazz: Class<*>): List<Method> {
        if (clazz.getAnnotation(APIs::class.java) != null) {
            return clazz.declaredMethods
                    .filter({ this.apiAnnotationedMethod(it) })
        }
        return emptyList()
    }

    private fun apiAnnotationedMethod(method: Method): Boolean {
        return (method.getAnnotation(API::class.java) != null
                || method.getAnnotation(Get::class.java) != null
                || method.getAnnotation(Post::class.java) != null
                || method.getAnnotation(Put::class.java) != null
                || method.getAnnotation(Delete::class.java) != null)
    }


    private fun scanClass(): List<Class<*>> {
        val classes = ArrayList<Class<*>>()
        for (pkg in packageToScan) {
            // 获取包及其子包下面的全部类文件
            val pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/" + pkg.replace("\\.".toRegex(), "/") + "/**/*.class"
            var resources: Array<Resource>
            try {
                resources = resourcePatternResolver.getResources(pattern)
            } catch (e: IOException) {
                continue
            }

            for (res in resources) {
                try {
                    val meta = metadataReaderFactory.getMetadataReader(res).classMetadata
                    val clazz = Class.forName(meta.className)
                    if (clazz != null) {
                        classes.add(clazz)
                    }
                } catch (e: IOException) {
                } catch (e: ClassNotFoundException) {
                }

            }
        }
        return classes
    }

    companion object {

        private val LOG = LoggerFactory.getLogger(APIParser::class.java)

        /**
         * 创建一个解析器。
         *
         * @param props properties。
         */
        @Throws(IOException::class)
        fun newInstance(props: Properties): APIParser {
            return APIParser(props.getProperty("schemes"), props.getProperty("apiHost"), props.getProperty("apiFile"), props.getProperty("packageToScan"), props.getProperty("apiBasePath"),
                    props.getProperty("apiDescription"), props.getProperty("termsOfService"), props.getProperty("apiTitle"), props.getProperty("apiVersion"), props.getProperty("suffix"))
        }
    }

}
