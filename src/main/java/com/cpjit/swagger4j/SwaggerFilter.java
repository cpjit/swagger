package com.cpjit.swagger4j;

import javax.servlet.*;
import java.io.IOException;
import java.util.Collection;

/**
 * @author yonghuan
 * @see com.cpjit.swagger4j.support.servlet.ApiServlet
 * @since 2.2.0
 */
public class SwaggerFilter implements Filter {
    private SwaggerMappingSupport mappingSupport;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String filterName = filterConfig.getFilterName();
        FilterRegistration filterRegistration = filterConfig.getServletContext().getFilterRegistration(filterName);
        Collection<String> urlPatternMappings = filterRegistration.getUrlPatternMappings();
        if (urlPatternMappings.size() != 1) {
            throw new IllegalArgumentException();
        }
        String urlPatternMapping = urlPatternMappings.iterator().next().replaceAll("\\*", "");
        mappingSupport = new SwaggerMappingSupport(filterConfig.getServletContext(), urlPatternMapping);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (!mappingSupport.doMapping(servletRequest, servletResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
