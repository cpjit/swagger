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

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cpjit.swagger4j.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import com.alibaba.fastjson.JSONWriter;

/**
 * 接口解析器。
 * <p>
 * <p>可以通过如下的方式来构建一个接口解析器：</p>
 * <p>
 * <pre>
 * 	// 创建一个构建器
 * 	String host = "127.0.0.1/app";
 * 	String file = "c:/apis.json";
 * 	String[] packageToScan = new String[]{"com.cpj.demo.api"};
 * 	APIParser.Builder builder = new APIParser.Builder(host, file, packageToScan);
 *
 * 	// 设置可选参数
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
 *
 * @author yonghaun
 * @since 1.0.0
 */
public final class APIParser implements APIParseable {

    private final static Logger LOG = LoggerFactory.getLogger(APIParser.class);
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
    private final static String DELIMITER = "[;,]";
    private String[] schemes;
    private String host;
    private String basePath;
    private String suffix = "";
    private APIDocInfo info;
    private String file;
    private List<String> packageToScan;
    private Map<String, Item> items;

    /**
     * 创建一个解析器。
     *
     * @param props properties。
     * @see APIParser.Builder
     */
    public final static APIParser newInstance(Properties props) throws IOException {
        String[] packageToScan = props.getProperty("packageToScan").split(DELIMITER);
        String schemesStr = props.getProperty("schemes");
        String[] schemes;
        if (StringUtils.isNotBlank(schemesStr)) {
            schemes = schemesStr.split(",");
        } else {
            schemes = new String[]{"http"};
        }
        Builder builder = new Builder(schemes, props.getProperty("apiHost"), props.getProperty("apiFile"), packageToScan)
                .basePath(props.getProperty("apiBasePath"))
                .description(props.getProperty("apiDescription"))
                .termsOfService(props.getProperty("termsOfService"))
                .title(props.getProperty("apiTitle"))
                .version(props.getProperty("apiVersion"))
                .suffix(props.getProperty("suffix"));
        return new APIParser(builder);
    }

    private APIParser(Builder builder) {
        // 扫描class并生成文件所需要的参数
        this.schemes = builder.schemes;
        this.host = builder.host;
        this.file = builder.file;
        this.packageToScan = builder.packageToScan;
        try {
            packages = scanClass().stream()
                    .map(Class::getPackage)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new IllegalStateException("扫描包信息失败", e);
        }
        this.basePath = builder.basePath;
        this.suffix = builder.suffix;

        // API文档信息
        info = new APIDocInfo.Builder()
                .contact(builder.contact)
                .description(builder.description)
                .license(builder.license)
                .termsOfService(builder.termsOfService)
                .title(builder.title)
                .version(builder.version).build();
    }

    /**
     * @author yonghuan
     */
    public static class Builder {
        // required args
        private String host;
        private String file;
        private List<String> packageToScan;

        private String[] schemes;
        private String basePath;
        private String suffix = "";
        private String description;
        private String version;
        private String title;
        private String termsOfService;
        private String contact;
        private License license;

        /**
         * 创建一个构建器。
         *
         * @param host          API访问地址（不包含协议）
         * @param file          解析产生的文件的存放路径
         * @param packageToScan 待扫描的包
         */
        public Builder(String[] schemes, String host, String file, String[] packageToScan) {
            this(schemes, host, file, Arrays.asList(packageToScan));
        }

        /**
         * 创建一个构建器。
         *
         * @param host          API访问地址（不包含协议）
         * @param file          解析产生的文件的存放路径
         * @param packageToScan 待扫描的包
         */
        public Builder(String[] schemes, String host, String file, List<String> packageToScan) {
            this.schemes = schemes;
            this.host = host;
            this.file = file;
            this.packageToScan = packageToScan;
        }

        /**
         * 构建解析器。
         */
        public APIParser build() {
            return new APIParser(this);
        }

        /**
         * 设置API相对于host（API访问地址）的基路径
         *
         * @param val API相对于host（API访问地址）的基路径
         */
        public Builder basePath(String val) {
            this.basePath = val;
            return this;
        }

        /**
         * 设置请求地址的后缀，如：.do、.action。
         *
         * @param suffix 请求地址的后缀
         */
        public Builder suffix(String suffix) {
            if (StringUtils.isNotBlank(suffix)) {
                this.suffix = suffix;
            }
            return this;
        }

        /**
         * 设置API描述
         *
         * @param val API描述
         */
        public Builder description(String val) {
            this.description = val;
            return this;
        }

        /**
         * 设置API版本
         *
         * @param val API版本
         */
        public Builder version(String val) {
            this.version = val;
            return this;
        }

        /**
         * 设置API标题
         *
         * @param val API标题
         */
        public Builder title(String val) {
            this.title = val;
            return this;
        }

        /**
         * 设置API开发团队的服务地址
         *
         * @param val API开发团队的服务地址
         */
        public Builder termsOfService(String val) {
            this.termsOfService = val;
            return this;
        }

        /**
         * 设置API开发团队的联系人
         *
         * @param val API开发团队的联系人
         */
        public Builder contact(String val) {
            this.contact = val;
            return this;
        }

        /**
         * 设置API遵循的协议（如apahce开源协议）
         *
         * @param val API遵循的协议（如apahce开源协议）
         */
        public Builder license(License val) {
            this.license = val;
            return this;
        }
    }

    /**
     * @return 解析完成后存放JSON数据的文件路径。
     */
    public String getFile() {
        return file;
    }

    /**
     * @return 待解析接口所在包
     */
    public List<String> getPackageToScan() {
        return packageToScan;
    }

    private Set<Package> packages;

    @Override
    public void parse() throws Exception {
        /* 将结果写入文件 */
        java.nio.file.Path f = Paths.get(file);
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.join("", "生成的文件保存在=>", f.toString()));
        }
        JSONWriter writer = new JSONWriter(Files.newBufferedWriter(f, StandardCharsets.UTF_8));
        APIDoc api = (APIDoc) parseAndNotStore();
        writer.writeObject(api);
        writer.flush();
        writer.close();
    }

    @Override
    public Object parseAndNotStore() throws Exception {
        APIDoc api = new APIDoc();
        api.setSchemes(schemes);
        api.setHost(host);
        api.setBasePath(basePath);
        api.setInfo(info);

        /* 解析全部item */
        items = parseItem();

        /* 解析全部tag */
        Collection<Tag> tags = parseTag();
        api.setTags(tags);

        /* 解析全部definition */
        Map<String, Object> definitions = parseDefinition();

        /* 解析全部path */
        Map<String, Map<String, Path>> paths = parsePath(definitions);
        api.setPaths(paths);
        api.setDefinitions(definitions);
        return api;
    }

    private Map<String, Item> parseItem() throws Exception {
        return packages.stream()
                .filter(pk -> pk.getAnnotation(Items.class) != null)
                .map(pk -> pk.getAnnotation(Items.class))
                .flatMap(items -> Arrays.stream(items.items()))
                .collect(Collectors.toMap(item -> item.value(), item -> item));
    }


    /**
     * url -> [ path ]
     */
    private Map<String, Map<String, Path>> parsePath(Map<String, Object> definitions) throws Exception {
        Map<String, Map<String, Path>> paths = new HashMap<>();
        scanClass().forEach(clazz -> {
            APIs apis = clazz.getAnnotation(APIs.class);
            if (apis == null || apis.hide()) {
                return;
            }
            scanAPIMethod(clazz)
                    .forEach(method -> api2Path(method, apis, paths, definitions));
        });
        return paths;
    }

    private Api parseApi(Method method) {
        Api api = new Api();
        API apiAnnotation = method.getAnnotation(API.class);
        if (apiAnnotation != null) {
            api.parameters = apiAnnotation.parameters();
            api.tags = apiAnnotation.tags();
            api.consumes = apiAnnotation.consumes();
            api.deprecated = apiAnnotation.deprecated();
            api.hide = apiAnnotation.hide();
            api.description = apiAnnotation.description();
            api.method = apiAnnotation.method();
            api.operationId = apiAnnotation.operationId();
            api.produces = apiAnnotation.produces();
            api.summary = apiAnnotation.summary();
            api.value = apiAnnotation.value();
            return api;
        }
        Get get = method.getAnnotation(Get.class);
        if (get != null) {
            api.parameters = get.parameters();
            api.tags = get.tags();
            api.consumes = get.consumes();
            api.deprecated = get.deprecated();
            api.hide = get.hide();
            api.description = get.description();
            api.method = "GET";
            api.operationId = get.operationId();
            api.produces = get.produces();
            api.summary = get.summary();
            api.value = get.value();
            return api;
        }
        Post post = method.getAnnotation(Post.class);
        if (post != null) {
            api.parameters = post.parameters();
            api.tags = post.tags();
            api.consumes = post.consumes();
            api.deprecated = post.deprecated();
            api.hide = post.hide();
            api.description = post.description();
            api.method = "POST";
            api.operationId = post.operationId();
            api.produces = post.produces();
            api.summary = post.summary();
            api.value = post.value();
            return api;
        }
        Put put = method.getAnnotation(Put.class);
        if (put != null) {
            api.parameters = put.parameters();
            api.tags = put.tags();
            api.consumes = put.consumes();
            api.deprecated = put.deprecated();
            api.hide = put.hide();
            api.description = put.description();
            api.method = "PUT";
            api.operationId = put.operationId();
            api.produces = put.produces();
            api.summary = put.summary();
            api.value = put.value();
            return api;
        }
        Delete delete = method.getAnnotation(Delete.class);
        if (delete != null) {
            api.parameters = delete.parameters();
            api.tags = delete.tags();
            api.consumes = delete.consumes();
            api.deprecated = delete.deprecated();
            api.hide = delete.hide();
            api.description = delete.description();
            api.method = "DELETE";
            api.operationId = delete.operationId();
            api.produces = delete.produces();
            api.summary = delete.summary();
            api.value = delete.value();
            return api;
        }
        return null;
    }

    private void api2Path(Method method, APIs apis, Map<String, Map<String, Path>> paths, Map<String, Object> definitions) {
        Api service = parseApi(method);
        if (service.hide()) {
            return;
        }
        final boolean isMultipart = hasMultipart(service);
        String url;
        if ("".equals(service.value())) {
            url = String.join("", apis.value(), suffix);
        } else {
            url = String.join("", apis.value(), "/", service.value(), suffix);
        }
        Map<String, Path> path = paths.get(url); // get/psot/put/delete
        if (path == null) {
            path = new HashMap<>();
            paths.put(url, path);
        }

        Path p = path.get(service.method());
        if (p == null) {
            p = new Path();
            path.put(service.method().toLowerCase(), p);
        }
        if (StringUtils.isNotBlank(service.description())) {
            p.setDescription(service.description());
        } else {
            p.setDescription(service.summary());
        }
        if (StringUtils.isNotBlank(service.operationId())) {
            p.setOperationId(service.operationId());
        } else { // 未设置operationId，
            p.setOperationId(method.getName());
        }
        List<String> tags = Arrays.asList(service.tags());
        if (service.tags().length == 0) {
            String ns = apis.value();
            if (ns.startsWith("/")) {
                ns = ns.substring(1);
            }
            tags = Collections.singletonList(ns);
        }
        p.setTags(tags);
        p.setSummary(service.summary());
        if (isMultipart) { // multipart/form-data
            p.setConsumes(Collections.singletonList("multipart/form-data"));
        } else {
            p.setConsumes(Arrays.asList(service.consumes()));
        }
        p.setProduces(Arrays.asList(service.produces()));
        p.setDeprecated(service.deprecated());
        p.setParameters(parseParameters(url, service, isMultipart, definitions));
    }

    private List<Map<String, Object>> parseParameters(String url, Api service, boolean isMultipart, Map<String, Object> definitions) {
        List<Map<String, Object>> parameters = new ArrayList<>(); // 请求参数
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>(service.parameters().length);
        boolean useBody = Arrays.stream(service.parameters())
                .filter(param -> "body".equalsIgnoreCase(param.in()))
                .count() > 0;
        if (useBody) {
            Map<String, Object> definition = new HashMap<>();
            definition.put("type", "object");
            definition.put("properties", properties);
            definition.put("required", required);
            String definitionName = url.replaceAll("/", "_");
            definitions.put(definitionName, definition);
            body.put("in", "body");
            body.put("name", "body");
            Map<String, Object> ref = new HashMap<>();
            ref.put("$ref", "#/definitions/" + definitionName);
            body.put("schema", ref);
        }
        /* 解析参数，优先使用schema */
        for (Param paramAttr : service.parameters()) {
            Map<String, Object> parameter = new HashMap<>();
            if (paramAttr.schema() != null && !paramAttr.schema().trim().equals("")) { // 处理复杂类型的参数
                if (isMultipart) { // 当请求的Content-Type为multipart/form-data将忽略复杂类型的参数
                    throw new IllegalArgumentException(String.join("", "请求的Content-Type为multipart/form-data，将忽略复杂类型的请求参数[ ", paramAttr.schema(), " ]"));
                }
                parameter.put("in", "body");
                parameter.put("name", "body");
                Map<String, Object> ref = new HashMap<>();
                ref.put("$ref", "#/definitions/" + paramAttr.schema());
                parameter.put("schema", ref);
            } else if ("body".equalsIgnoreCase(paramAttr.in())) {
                Map<String, Object> propertie = new HashMap<>();
                if (paramAttr.dataType() != null) {
                    propertie.put("type", paramAttr.dataType().type());
                    propertie.put("format", paramAttr.dataType().format());
                } else {
                    propertie.put("type", paramAttr.type());
                    propertie.put("format", paramAttr.format());
                }
                propertie.put("description", paramAttr.description());
                if (paramAttr.required()) { // 为必须参数
                    required.add(paramAttr.name());
                }
                if (StringUtils.isNotBlank(paramAttr.items())) { // 可选值
                    propertie.put("enum", parseOptionalValue(paramAttr.type(), paramAttr.items().split(",")));
                }
                properties.put(paramAttr.name(), propertie);
                continue;
            } else { // 简单类型的参数
                String requestParamType, requestParamFormat;
                if (paramAttr.dataType() != DataType.UNKNOWN) { // since 1.2.2
                    requestParamType = paramAttr.dataType().type();
                    requestParamFormat = paramAttr.dataType().format();
                } else {
                    requestParamType = paramAttr.type();
                    requestParamFormat = paramAttr.format();
                }
                if (isMultipart && !"path".equals(paramAttr.in()) && !"header".equals(paramAttr.in())) { // 包含文件上传
                    parameter.put("in", "formData");
                    parameter.put("type", requestParamType);
                } else { // 不包含文件上传
                    String in = paramAttr.in();
                    if (StringUtils.isBlank(in)) {
                        if ("post".equalsIgnoreCase(service.method()) || "put".equalsIgnoreCase(service.method())) {
                            in = "formData";
                        } else {
                            in = "query";
                        }
                    }
                    parameter.put("in", in);
                    parameter.put("type", requestParamType);
                    if (StringUtils.isNotBlank(requestParamFormat)) {
                        parameter.put("format", requestParamFormat);
                    }
                }
                parameter.put("name", paramAttr.name());
                parameter.put("description", paramAttr.description());
                parameter.put("required", paramAttr.required());
                if (paramAttr.items() != null && !paramAttr.items().trim().equals("")) {
                    if (!requestParamType.equals("array")) {
                        throw new IllegalArgumentException(String.join("", "请求参数 [ ", paramAttr.name(), " ]存在可选值(items)的时候，请求参数类型(type)的值只能为array"));
                    }
                    Item item = items.get(paramAttr.items().trim());
                    if (item != null) { // 可选值
                        Map<String, Object> i = new HashMap<>();
                        i.put("type", item.type());
                        i.put("default", item.defaultValue());
                        i.put("enum", parseOptionalValue(item.type(), item.optionalValue()));
                        parameter.put("items", i);
                    }
                }
            }
            if (StringUtils.isNotBlank(paramAttr.defaultValue())) {
                parameter.put("defaultValue", paramAttr.defaultValue());
            }
            parameters.add(parameter);
        }
        if (properties.size() > 0) {
            parameters.add(body);
        }
        return parameters;
    }

    private Object parseOptionalValue(String type, String[] values) {
        if (type.equals("string")) { // string
            return values;
        } else if (type.equals("boolean")) { // boolean
            return Arrays.stream(values)
                    .map(Boolean::parseBoolean)
                    .collect(Collectors.toList());
        } else if (type.equals("integer")) { // integer
            return Arrays.stream(values)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } else { // double
            return Arrays.stream(values)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        }
    }

    /**
     * 判断接口的请求Content-Type是否为multipart/form-data。
     */
    private boolean hasMultipart(Api service) {
        return Arrays.stream(service.consumes())
                .anyMatch(consume -> "multipart/form-data".equals(consume));
    }

    /**
     * 解析全部Tag。
     *
     * @return 全部Tag。
     */
    private Collection<Tag> parseTag() throws Exception {
        // since1.2.2 先扫描被@APITag标注了的类
        Stream<APITag> tagsFromClass = scanClass()
                .stream()
                .map(clazz -> clazz.getAnnotation(APITag.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                .stream();
        // 扫描package-info上面的@APITags
        Stream<APITag> tagsFromPackage = packages.stream()
                .map(pk -> pk.getAnnotation(APITags.class))
                .filter(Objects::nonNull)
                .flatMap(apiTags -> Arrays.stream(apiTags.tags()));
        return Stream.of(tagsFromClass, tagsFromPackage)
                .flatMap(stream -> stream)
                .map(apiTag -> new Tag(apiTag.value(), apiTag.description()))
                .collect(Collectors.toSet());
    }

    /**
     * 解析全部definition。
     *
     * @return 全部definition
     */
    private Map<String, Object> parseDefinition() throws Exception {
        Map<String, Object> definitions = new HashMap<>();

        for (Package pk : packages) {
            APISchemas apiSchemas = pk.getAnnotation(APISchemas.class);
            if (apiSchemas == null) {
                continue;
            }
            APISchema[] schemas = apiSchemas.schemas();
            for (APISchema schema : schemas) {
                Map<String, Object> definition = new HashMap<>();
                definition.put("type", schema.type());
                List<String> required = new ArrayList<>();
                definition.put("required", required);
                APISchemaPropertie[] props = schema.properties();
                Map<String, Map<String, Object>> properties = new HashMap<>();
                for (APISchemaPropertie prop : props) {
                    Map<String, Object> propertie = new HashMap<>();
                    definition.put("properties", properties);

                    propertie.put("type", prop.type());
                    propertie.put("format", prop.format());
                    propertie.put("description", prop.description());

                    if (prop.required()) { // 为必须参数
                        required.add(prop.value());
                    }
                    if (prop.optionalValue().length > 0) { // 可选值
                        propertie.put("enum", parseOptionalValue(prop.type(), prop.optionalValue()));
                    }
                    properties.put(prop.value(), propertie);
                }
                definitions.put(schema.value(), definition); // 添加新的definition
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
    private List<Method> scanAPIMethod(Class<?> clazz) {
        APIs apis = clazz.getAnnotation(APIs.class);
        if (apis == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(this::apiAnnotationedMethod)
                .collect(Collectors.toList());
    }

    private boolean apiAnnotationedMethod(Method method) {
        return method.getAnnotation(API.class) != null
                || method.getAnnotation(Get.class) != null
                || method.getAnnotation(Post.class) != null
                || method.getAnnotation(Put.class) != null
                || method.getAnnotation(Delete.class) != null;
    }


    private List<Class<?>> scanClass() {
        List<Class<?>> classes = new ArrayList<>();
        for (String pkg : packageToScan) {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/" + pkg.replaceAll("\\.", "/") + "/**/*.class";
            Resource[] resources = null;
            try {
                resources = resourcePatternResolver.getResources(pattern);
            } catch (IOException e) {
                continue;
            }
            for (Resource res : resources) {
                try {
                    ClassMetadata meta = metadataReaderFactory.getMetadataReader(res).getClassMetadata();
                    Class<?> clazz = Class.forName(meta.getClassName());
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                } catch (IOException | ClassNotFoundException e) {
                }
            }
        }
        return classes;
    }

    private static class Api {
        private String value = "";
        private String[] tags = {};
        private String method = "";
        private String summary = "";
        private String description = "";
        private String operationId = "";
        private String[] consumes = {};
        private String[] produces = {};
        private Param[] parameters = {};
        private boolean deprecated;
        private boolean hide;

        String value() {
            return value;
        }

        String[] tags() {
            return tags;
        }

        String method() {
            return method;
        }

        String summary() {
            return summary;
        }

        String description() {
            return description;
        }

        String operationId() {
            return operationId;
        }

        String[] consumes() {
            return consumes;
        }

        String[] produces() {
            return produces;
        }

        Param[] parameters() {
            return parameters;
        }

        boolean deprecated() {
            return deprecated;
        }

        boolean hide() {
            return hide;
        }
    }
}
