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
package com.cpjit.swagger4j;

import com.alibaba.fastjson.JSONWriter;
import com.cpjit.swagger4j.annotation.*;
import com.cpjit.swagger4j.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import kotlin.collections.HashMap

/**
 * 接口解析器。
 *
 * <p>可以通过如下的方式来构建一个接口解析器：</p>
 *
 * <pre>
 * 	// 创建一个构建器
 * 	String host = "127.0.0.1/app";
 * 	String file = "c:/apis.json";
 * 	String[] packageToScan = new String[]{"com.cpj.demo.api"};
 * 	APIParser.Builder builder = new APIParser.Builder(host, file, packageToScan);
 *
 *	// 设置可选参数
 * 	builder.basePath("/");
 *
 * 	// 构建解析器
 * 	APIParser parser = builder.build();
 * 	// 解析
 * 	parser.parse();
 * </pre>
 * <p>或者通过这种方式来构建一个接口解析器：</p>
 * <pre>
 * 	APIParser.newInstance(props);
 * </pre>
 * @author yonghaun
 * @since 1.0.0
 */
class APIParser : APIParseable {


    var host: String = "";
    var basePath: String = "";
    var suffix: String = "";
    var info = APIDocInfo();
    var file: String = "";
    var packageToScan: List<String> = Collections.emptyList();
    var items = java.util.HashMap<String, Item>();
    var packages: List<Package> = Collections.emptyList();

    companion object {

        private val LOG: Logger = LoggerFactory.getLogger(APIParser::class.java);
        private val DELIMITER: String = "[;,]";

        /**
         * 创建一个解析器。
         * @param props properties。
         * @see APIParser.Builder
         */
        @Throws(IOException::class)
        fun newInstance(props: Properties): APIParser {
            var packageToScan = props.getProperty("packageToScan").split(DELIMITER);
            var builder = Builder(props.getProperty("apiHost"), props.getProperty("apiFile"), packageToScan)
                    .basePath(props.getProperty("apiBasePath"))
                    .description(props.getProperty("apiDescription"))
                    .termsOfService(props.getProperty("termsOfService"))
                    .title(props.getProperty("apiTitle"))
                    .version(props.getProperty("apiVersion"))
                    .suffix(props.getProperty("suffix"));
            return APIParser(builder);
        }

        /**
         * @author yonghuan
         */
        @java.lang.Deprecated
        class Builder {
            // required args
            var host = "";
            var file = "";
            var packageToScan: List<String> = Collections.emptyList();

            var basePath = "";
            var suffix = "";
            var description = "";
            var version = "";
            var title = "";
            var termsOfService = "";
            var contact = "";
            var license = License();

            /**
             * 创建一个构建器。
             *
             * @param host
             *            API访问地址（不包含协议）
             * @param file
             *            解析产生的文件的存放路径
             * @param packageToScan
             *            待扫描的包
             */
            constructor(host: String, file: String, packageToScan: Array<String>) {
                this.host = host;
                this.file = file;
                this.packageToScan = packageToScan.toList();
            }

            /**
             * 创建一个构建器。
             *
             * @param host
             *            API访问地址（不包含协议）
             * @param file
             *            解析产生的文件的存放路径
             * @param packageToScan
             *            待扫描的包
             */
            constructor(host: String, file: String, packageToScan: List<String>) {
                this.host = host;
                this.file = file;
                this.packageToScan = packageToScan;
            }

            /**
             * 构建解析器。
             */
            fun build(): APIParser {
                return APIParser(this);
            }

            /**
             * 设置API相对于host（API访问地址）的基路径
             *
             * @param basePath
             *            API相对于host（API访问地址）的基路径
             */
            fun basePath(basePath: String): Builder {
                this.basePath = basePath;
                return this;
            }

            /**
             * 设置请求地址的后缀，如：.do、.action。
             * @param suffix 请求地址的后缀
             */
            fun suffix(suffix: String): Builder {
                if (StringUtils.isNotBlank(suffix)) {
                    this.suffix = suffix;
                }
                return this;
            }

            /**
             * 设置API描述
             * @param description
             *            API描述
             */
            fun description(description: String): Builder {
                this.description = description;
                return this;
            }

            /**
             * 设置API版本
             *
             * @param version
             *            API版本
             */
            fun version(version: String): Builder {
                this.version = version;
                return this;
            }

            /**
             * 设置API标题
             *
             * @param title
             *            API标题
             */
            fun title(title: String): Builder {
                this.title = title;
                return this;
            }

            /**
             * 设置API开发团队的服务地址
             *
             * @param termsOfService
             *            API开发团队的服务地址
             */
            fun termsOfService(termsOfService: String): Builder {
                this.termsOfService = termsOfService;
                return this;
            }

            /**
             * 设置API开发团队的联系人
             *
             * @param contact
             *            API开发团队的联系人
             */
            fun contact(contact: String): Builder {
                this.contact = contact;
                return this;
            }

            /**
             * 设置API遵循的协议（如apahce开源协议）
             *
             * @param license
             *            API遵循的协议（如apahce开源协议）
             */
            fun license(license: License): Builder {
                this.license = license;
                return this;
            }
        }
    }


    constructor(builder: Builder) {
        // 扫描class并生成文件所需要的参数
        this.host = builder.host;
        this.file = builder.file;
        this.packageToScan = builder.packageToScan;
        try {
            packages = ReflectUtils.scanPackages(this.packageToScan, true);
        } catch (e: Exception) {
            throw IllegalStateException("扫描包信息失败", e);
        }
        this.basePath = builder.basePath;
        this.suffix = builder.suffix;

        // API文档信息
        info = APIDocInfo.Builder()
                .contact(builder.contact)
                .description(builder.description)
                .license(builder.license)
                .termsOfService(builder.termsOfService)
                .title(builder.title)
                .version(builder.version).build();
    }




    @Throws(Exception::class)
    override fun parse() {
        /* 将结果写入文件 */
        var f = Paths.get(file);
        LOG.debug("生成的文件保存在=> {}", f.toString());
        var writer = JSONWriter(Files.newBufferedWriter(f, StandardCharsets.UTF_8));
        var api = parseAndNotStore();
        writer.writeObject(api);
        writer.flush();
        writer.close();
    }

    @Throws(Exception::class)
    override fun parseAndNotStore(): Any {
        var api = APIDoc();
        api.schemes = arrayOf("http");
        api.host = host;
        api.basePath = basePath;
        api.info = info;

        /* 解析全部item */
        items = parseItem();
        /* 解析全部tag */
        api.setTags(parseTag());

        /* 解析全部path */
        api.setPaths(parsePath());

        /* 解析全部definition */
        api.setDefinitions(parseDefinition());
        return api;
    }

    @Throws(Exception::class)
    private fun parseItem(): java.util.HashMap<String, Item> {
        var items = java.util.HashMap<String, Item>();
        packages.filter(fun(pk) = pk.getAnnotation(Items::class.java) != null)
                .map(fun(pk) = pk.getAnnotation(Items::class.java))
                .flatMap(fun(items) = items.items.toCollection(LinkedList<Item>()))
                .forEach { items.put(it.value, it) }
        return items;
    }


    /**
     * url -> [ path ]
     */
    @Throws(Exception::class)
    private fun parsePath(): Map<String, Map<String, Path>> {
        var paths = java.util.HashMap<String, HashMap<String, Path>>();
//		List<Class<?>> clazzs = ReflectUtils.scanClazzs(packageToScan, true); // 扫描包以获取包中的类
//		for(Class<?> clazz : clazzs) {
//			APIs apis = clazz.getAnnotation(APIs.class);
//			if(apis == null || apis.hide()) {
//				continue;
//			}
//			scanAPIMethod(clazz).stream()
//								.forEachOrdered(method -> api2Path(method, apis, paths));
//		}
        ReflectUtils.scanClazzs(packageToScan, true)
                .filter { it != null; }
                .forEach {
                    var apis = it.getAnnotation(APIs::class.java);
                    if (apis != null && !apis.hide) {
                        scanAPIMethod(it).forEach { api2Path(it, apis, paths) }
                    }
                };
        return paths;
    }

    private fun api2Path(method: Method, apis: APIs, paths: java.util.HashMap<String, java.util.HashMap<String, Path>>) {
        var service = method.getAnnotation(API::class.java);
        if (service.hide) {
            return;
        }
        val isMultipart = hasMultipart(service);
        var url: String;
        if ("".equals(service.value)) {
            url = apis.value + suffix;
        } else {
            url = apis.value + "/" + service.value + suffix;
        }
        var path = paths.get(url); // get/psot/put/delete
        if (path == null) {
            path = java.util.HashMap();
            paths.put(url, path);
        }

        var p = path.get(service.method);
        if (p == null) {
            p = Path();
            path.put(service.method.toLowerCase(), p);
        }
        if (StringUtils.isNotBlank(service.description)) {
            p.setDescription(service.description);
        } else {
            p.setDescription(service.summary);
        }
        if (StringUtils.isNotBlank(service.operationId)) {
            p.setOperationId(service.operationId);
        } else { // 未设置operationId，
            p.setOperationId(method.getName());
        }
        var tags = service.tags;
        if (tags.isEmpty()) {
            var ns = apis.value;
            if (ns.startsWith("/")) {
                ns = ns.substring(1);
            }
            tags = arrayOf(ns);
        }
        p.tags = tags.toList();
        p.summary = service.summary;
        if (isMultipart) { // multipart/form-data
            p.setConsumes(Collections.singletonList("multipart/form-data"));
        } else {
            p.consumes = service.consumes.toList();
        }
        p.produces = service.produces.toList();
        p.setDeprecated(service.deprecated);
        p.parameters = parseParameters(service, isMultipart);
    }

    private fun parseParameters(service: API, isMultipart: Boolean): List<Map<String, Any>> {
        var parameters = ArrayList<Map<String, Any>>(); // 请求参数
        /* 解析参数，优先使用schema */
        for (paramAttr in service.parameters) {
            var parameter = java.util.HashMap<String, Any>();
            if (paramAttr.schema != null && !paramAttr.schema.trim().equals("")) { // 处理复杂类型的参数
                if (isMultipart) { // 当请求的Content-Type为multipart/form-data将忽略复杂类型的参数
                    throw IllegalArgumentException("请求的Content-Type为multipart/form-data，将忽略复杂类型的请求参数[ " + paramAttr.schema + " ]");
                }
                parameter.put("in", "body");
                parameter.put("name", "body");
                var ref = HashMap<String, Any>();
                ref.put("$ref", "#/definitions/" + paramAttr.schema);
                parameter.put("schema", ref);
            } else { // 简单类型的参数
                var requestParamType: String;
                var requestParamFormat: String;
                if (paramAttr.dataType != DataType.UNKNOWN) { // since 1.2.2
                    requestParamType = paramAttr.dataType.type();
                    requestParamFormat = paramAttr.dataType.format();
                } else {
                    requestParamType = paramAttr.type;
                    requestParamFormat = paramAttr.format;
                }
                if (isMultipart && !"path".equals(paramAttr.`in`) && !"header".equals(paramAttr.`in`)) { // 包含文件上传
                    parameter.put("in", "formData");
                    parameter.put("type", requestParamType);
                } else { // 不包含文件上传
                    var _in = paramAttr.`in`;
                    if (StringUtils.isBlank(_in)) {
                        if ("post".equals(service.method.toLowerCase())) {
                            _in = "formData";
                        } else {
                            _in = "query";
                        }
                    }
                    parameter.put("in", _in);
                    parameter.put("type", requestParamType);
                    if (StringUtils.isNotBlank(requestParamFormat)) {
                        parameter.put("format", requestParamFormat);
                    }
                }
                parameter.put("name", paramAttr.name);
                parameter.put("description", paramAttr.description);
                parameter.put("required", paramAttr.required);
                if (paramAttr.items != null && !paramAttr.items.trim().equals("")) {
                    if (!requestParamType.equals("array")) {
                        throw  IllegalArgumentException("请求参数 [ " + paramAttr.name + " ]存在可选值(items)的时候，请求参数类型(type)的值只能为array");
                    }
                    var item = items.get(paramAttr.items.trim());
                    if (item != null) { // 可选值
                        var i = HashMap<String, Any>();
                        i.put("type", item.type);
                        i.put("default", item.defaultValue);
                        i.put("enum", parseOptionalValue(item.type, item.optionalValue));
                        parameter.put("items", i);
                    }
                }
            }
            if (StringUtils.isNotBlank(paramAttr.defaultValue)) {
                parameter.put("defaultValue", paramAttr.defaultValue);
            }
            parameters.add(parameter);
        }
        return parameters;
    }

    private fun parseOptionalValue(type: String, values: Array<String>) = {
        when (type) {
            "string" -> values
            "boolean" -> values.map { java.lang.Boolean.parseBoolean(it) }.toList()
            "integer" -> values.map { java.lang.Integer.parseInt(it) }.toList()
            else -> values.map { java.lang.Double.parseDouble(it) }.toList()
        }
    }

    /**
     * 判断接口的请求Content-Type是否为multipart/form-data。
     */
    private fun hasMultipart(service: API): Boolean {
        return service.consumes.any { "multipart/form-data".equals(it) }
    }

    /**
     * 解析全部Tag。
     * @return 全部Tag。
     */
    @Throws(Exception::class)
    private fun parseTag(): Collection<Tag> {
        // since1.2.2 先扫描被@APITag标注了的类
        var tags = scanAPIsAnnotations().map { Tag(it.value, it.description) }
        // 扫描package-info上面的@APITags
        packages.map { it.getAnnotation(APITags::class.java) }
                .filter { it != null }
                .flatMap { it.tags.toList() }
                .forEach { tags.plus(Tag(it.value, it.description)) }
        return tags.toSet();
    }

    /**
     * 解析全部definition。
     *
     * @return 全部definition
     */
    @Throws(Exception::class)
    private fun parseDefinition(): java.util.HashMap<String, Any> {
        var definitions = HashMap<String, Any>();

        for (pk in packages) {
            var apiSchemas = pk.getAnnotation(APISchemas::class.java);
            if (apiSchemas == null) {
                continue;
            }
            for (schema in apiSchemas.schemas) {
                var definition = HashMap<String, Any>();
                definition.put("type", schema.type);
                var required = ArrayList<String>();
                definition.put("required", required);
                var properties = HashMap<String, Map<String, Any>>();
                for (prop in schema.properties) {
                    var propertie = HashMap<String, Any>();
                    definition.put("properties", properties);

                    propertie.put("type", prop.type);
                    propertie.put("format", prop.format);
                    propertie.put("description", prop.description);

                    if (prop.required) { // 为必须参数
                        required.add(prop.value);
                    }
                    if (prop.optionalValue.isNotEmpty()) { // 可选值
                        propertie.put("enum", parseOptionalValue(prop.type, prop.optionalValue));
                    }
                    properties.put(prop.value, propertie);
                }
                definitions.put(schema.value, definition); // 添加新的definition
            }
        }
        return definitions;
    }

    /*
     * 扫描所有用注解{@link API}修饰了的方法。
     *
     * @return 所有用注解{@link API}修饰了的方法
     * @throws Exception
     */
    private fun scanAPIMethod(clazz: Class<*>): List<Method> {
        var apis = clazz.getAnnotation(APIs::class.java);
        if (apis == null) {
            return Collections.emptyList();
        }
        return clazz.declaredMethods
                .filter { it.getAnnotation(API::class.java) != null }
                .toList();
    }

    /*
     * 扫描类上面的@APITag
     * @since 1.2.2
     */
    @Throws(Exception::class)
    private fun scanAPIsAnnotations(): List<APITag> {
        return ReflectUtils.scanClazzs(packageToScan, true)
                .map { it.getAnnotation(APITag::class.java) }
                .filter { it -> it != null }
                .toList()
    }
}
