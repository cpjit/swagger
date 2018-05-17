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
import com.cpjit.swagger4j.parameter.BodyParameterResolver
import com.cpjit.swagger4j.parameter.ParameterResolver
import com.cpjit.swagger4j.parameter.PrimitiveParameterResolver
import com.cpjit.swagger4j.parameter.SchemaObjectParameterResolver
import com.cpjit.swagger4j.specification.*
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
    private val specificationTranslator = SpecificationTranslatorImpl()
    private val resourcePatternResolver = PathMatchingResourcePatternResolver()
    private val metadataReaderFactory = CachingMetadataReaderFactory(this.resourcePatternResolver)
    private val parameterResolvers: List<ParameterResolver>
    private var schemes: Array<String>
    var suffix = ""
    var info: APIDocInfo
    /**
     * @return 待解析接口所在包
     */
    private val packageToScan: List<String>
    private var packages: Collection<Package>

    init {
        parameterResolvers = listOf(
                SchemaObjectParameterResolver(),
                BodyParameterResolver(),
                PrimitiveParameterResolver()
        )
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
        writer.writeObject(parseAndNotStore())
        writer.flush()
        writer.close()
    }

    @Throws(Exception::class)
    override fun parseAndNotStore(): Any {
        val spec = Specification()
        spec.schemes = schemes;
        spec.host = host
        spec.basePath = basePath

        /* 解析全部item */
        parseItem(spec)
        /* 解析全部tag */
        parseTag(spec)
        /* 解析全部definition */
        parseDefinition(spec)
        /* 解析全部path */
        parsePath(spec)
        return specificationTranslator.translate(spec)
    }

    @Throws(Exception::class)
    private fun parseItem(spec: Specification) {
        val items = HashMap<String, Item>()
        packages.mapNotNull { it.getAnnotation(Items::class.java) }
                .forEach({
                    it.items.forEach {
                        items[it.value] = it
                    }
                })
        spec.items = items
    }


    @Throws(Exception::class)
    private fun parsePath(spec: Specification) {
        scanClass().forEach { clazz ->
            val apis = clazz.getAnnotation(APIs::class.java)
            if (apis?.hide == false) {
                scanAPIMethod(clazz)
                        .forEach {
                            api2Path(it, apis, spec)
                        }
            }
        }
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

    private fun api2Path(method: Method, apis: APIs, spec: Specification) {
        val service = parseApi(method)
        if (service!!.hide) {
            return
        }
        val isMultipart = hasMultipart(service)
        val url = when ("" == service.value) {
            true -> apis.value + suffix
            false -> arrayOf(apis.value, "/", service.value, suffix).joinToString("")
        }
        var path: Path? = spec.paths.firstOrNull { it.url.equals(url) } // get/psot/put/delete
        if (path == null) {
            path = Path(url)
            spec.paths.add(path)
        }
        var operation: Operation? = when (service.method.toLowerCase()) {
            "get" -> path.get
            "put" -> path.put
            "post" -> path.post
            "delete" -> path.delete
            "head" -> path.head
            "patch" -> path.patch
            "options" -> path.options
            else -> null
        }
        if (operation == null) {
            val operationId = when (StringUtils.isNotBlank(service.operationId)) {
                true -> service.operationId
                false -> method.name
            }
            operation = Operation(operationId)
            when (service.method.toLowerCase()) {
                "get" -> path.get = operation
                "put" -> path.put = operation
                "post" -> path.post = operation
                "delete" -> path.delete = operation
                "head" -> path.head = operation
                "patch" -> path.patch = operation
                "options" -> path.options = operation
            }
        }
        if (StringUtils.isNotBlank(service.description)) {
            operation.description = service.description
        } else {
            operation.description = service.summary
        }
        var tags = service.tags
        if (service.tags!!.isEmpty()) {
            var ns = apis.value
            if (ns.startsWith("/")) {
                ns = ns.substring(1)
            }
            tags = arrayOf(ns)
        }
        operation.tags = tags
        operation.summary = service.summary
        if (isMultipart) { // multipart/form-data
            operation.consumes = arrayOf("multipart/form-data")
        } else {
            operation.consumes = service.consumes
        }
        operation.produces = service.produces
        operation.deprecated = service.deprecated
        operation.parameters = parseParameters(url, service, spec)
    }

    /**
     * 解析请求参数。
     */
    private fun parseParameters(url: String, api: Api, spec: Specification): List<Parameter> {
        val parameters: MutableList<Parameter> = mutableListOf()
        for (param in api.parameters) {
            val parameterResolver = parameterResolvers.firstOrNull { it.supportsParameter(api, param) }
            parameterResolver?.resolveParameter(url, api, param, parameters, spec)
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
    private fun parseTag(spec: Specification) {
        // since1.2.2 先扫描被@APITag标注了的类
        val tags = scanClass()
                .mapNotNull { it.getAnnotation(APITag::class.java) }
                .toMutableSet()
        // 扫描package-info上面的@APITags
        packages.mapNotNull { it.getAnnotation(APITags::class.java) }
                .forEach {
                    tags.addAll(it.tags)
                }
        spec.tags = tags.map {
            val tag = Tag(it.value)
            tag.description = it.description
            tag
        }.toMutableList()

    }

    /**
     * 解析全部definition。
     *
     * @return 全部definition
     */
    @Throws(Exception::class)
    private fun parseDefinition(spec: Specification) {
        val definitions: MutableList<Schema> = mutableListOf()
        for (pk in packages) {
            val apiSchemas = pk.getAnnotation(APISchemas::class.java) ?: continue
            val schemas = apiSchemas.schemas
            for (schema in schemas) {
                val definition = Schema(schema.value)
                definition.type = schema.type
                val required = ArrayList<String>()
                // definition.required = required
                val props = schema.properties
                val properties = mutableListOf<Property>()
                definition.properties = properties;
                for (prop in props) {
                    val property = Property()
                    property.type = prop.type
                    property.format = prop.format
                    property.description = prop.description

                    if (prop.required) { // 为必须参数
                        required.add(prop.value)
                    }
//                    if (prop.optionalValue.isNotEmpty()) { // 可选值
//                        property["enum"] = parseOptionalValue(prop.type, prop.optionalValue)
//                    }
                    properties.add(property)
                }
                definitions.add(definition) // 添加新的definition
            }
        }
        spec.definitions = definitions
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
