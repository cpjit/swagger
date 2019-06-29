package com.cpjit.swagger4j;

import com.cpjit.swagger4j.support.internal.ApiViewWriter;
import com.cpjit.swagger4j.support.internal.DefaultApiViewWriter;
import com.cpjit.swagger4j.util.ResourceUtil;
import com.cpjit.swagger4j.util.matcher.AntPathRequestMatcher;
import com.cpjit.swagger4j.util.matcher.HttpMethod;
import com.cpjit.swagger4j.util.matcher.RequestMatcher;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yonghuan
 * @since 2.2.0
 */
public class SwaggerMappingSupport {
    private final static String[] RESOURCE_PATTERNS = {
            "css/*"
            , "fonts/*"
            , "images/*"
            , "lang/*"
            , "lib/*"
            , ""
            , "index.html"
            , "swagger-ui.min.js"
    };
    private final static String STATIC_RESCUE_PATH = "com/cpjit/swagger4j/support/internal/statics";
    private final static String TEMPLATES_PATH = "templates";
    private String contextPath;
    private List<RequestMatcher> requestMatchers;
    private String urlPrefix;
    private ITemplateEngine templateEngine;
    private ApiViewWriter apiViewWriter = new DefaultApiViewWriter();
    private final static String JSON = "json";
    private final ConfigResolver configResolver = new DefaultConfigResolver();

    public SwaggerMappingSupport(ServletContext servletContext, String urlPatternMapping) throws ServletException {
        this.contextPath = servletContext.getContextPath();
        urlPrefix = contextPath + urlPatternMapping;
        urlPrefix = urlPrefix.replaceAll("/{2,}", "/");
        if (!"/".equals(urlPrefix) && urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
        }
        requestMatchers = Arrays.stream(RESOURCE_PATTERNS)
                .map(pattern -> String.join("/", this.contextPath, urlPatternMapping, pattern))
                .map(pattern -> pattern.replaceAll("/{2,}", "/"))
                .map(pattern -> new AntPathRequestMatcher(pattern, HttpMethod.GET.name()))
                .collect(Collectors.toList());

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(SwaggerFilter.class.getClassLoader());
        templateResolver.setPrefix(TEMPLATES_PATH + "/");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        this.templateEngine = templateEngine;
    }

    public boolean doMapping(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        if (uri.equals(this.urlPrefix)) {
            String accept = request.getHeader("accept");
            if (accept != null && accept.contains(JSON)) {
                try {
                    apiViewWriter.writeApis(request, response);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            } else {
                toIndex(request, response);
            }
            return true;
        }
        if (unsupportedRequest(request)) {
            return false;
        }

        String relativePath = request.getRequestURI().replaceFirst(urlPrefix, "");
        // 搜索静态资源
        String staticResource = String.join("/", STATIC_RESCUE_PATH, relativePath);
        staticResource = staticResource.replaceAll("/{2,}", "/");
        InputStream is = ResourceUtil.getResourceAsStream(SwaggerFilter.class, staticResource);
        OutputStream out = null;
        try {
            if (is != null) {
                out = response.getOutputStream();
                IOUtils.copy(is, out);
                return true;
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
        }

        // 搜索模板
        String templateResource = String.join("", TEMPLATES_PATH, relativePath);
        templateResource = templateResource.replaceAll("/{2,}", "/");
        is = ResourceUtil.getResourceAsStream(templateResource);
        if (is != null) {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            try (Writer writer = response.getWriter()) {
                WebContext webContext = new WebContext(request, response, request.getServletContext());
                webContext.setVariable("baseUrl", resolveBaseUrl(request));
                webContext.setVariable("lang", "zh-cn");
                webContext.setVariable("getApisUrl", resolveBaseUrl(request));
                configResolver.obtainConfig(request).forEach((k, v) -> webContext.setVariable((String) k, v));
                String templateName = FilenameUtils.getName(relativePath);
                templateEngine.process(templateName, webContext, writer);
            }
            return true;
        }
        return false;
    }

    private void toIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (Writer writer = response.getWriter()) {
            WebContext webContext = new WebContext(request, response, request.getServletContext());
            webContext.setVariable("baseUrl", resolveBaseUrl(request));
            String lang = request.getParameter("lang");
            if (StringUtils.isBlank(lang)) {
                lang = "zh-cn";
            }
            webContext.setVariable("lang", lang);
            webContext.setVariable("getApisUrl", resolveBaseUrl(request));
            configResolver.obtainConfig(request).forEach((k, v) -> webContext.setVariable((String) k, v));
            String templateName = FilenameUtils.getName("index.html");
            templateEngine.process(templateName, webContext, writer);
        }
    }

    private String resolveBaseUrl(HttpServletRequest request) throws IOException {
        URL url = new URL(request.getRequestURL().toString());
        String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + this.urlPrefix;
        return baseUrl;
    }

    private boolean unsupportedRequest(HttpServletRequest request) {
        return requestMatchers.stream()
                .filter(requestMatcher -> requestMatcher.matches(request))
                .findAny()
                .isPresent() == false;
    }
}
