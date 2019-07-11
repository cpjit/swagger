/*
 * Copyright 2011-2019 CPJIT Group.
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
package com.cpjit.swagger4j.support.servlet;

import com.cpjit.swagger4j.SwaggerMappingSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @author yonghuan
 * @see com.cpjit.swagger4j.SwaggerFilter
 * @since 1.0.0
 */
public class ApiServlet extends HttpServlet {

    private SwaggerMappingSupport mappingSupport;

    @Override
    public void init() throws ServletException {
        ServletConfig servletConfig = getServletConfig();
        Collection<String> servletMappings = servletConfig.getServletContext().getServletRegistration(servletConfig.getServletName()).getMappings();
        if (servletMappings.size() != 1) {
            throw new IllegalArgumentException();
        }
        String servletMapping = servletMappings.iterator().next().replaceAll("\\*", "");
        mappingSupport = new SwaggerMappingSupport(servletConfig.getServletContext(), servletMapping);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!mappingSupport.doMapping(req, resp)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
