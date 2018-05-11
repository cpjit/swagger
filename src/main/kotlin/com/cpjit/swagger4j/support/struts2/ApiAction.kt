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
package com.cpjit.swagger4j.support.struts2

import com.cpjit.swagger4j.support.internal.ApiViewWriter
import com.opensymphony.xwork2.ActionSupport
import com.opensymphony.xwork2.inject.Inject
import org.apache.commons.lang3.StringUtils
import org.apache.struts2.ServletActionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * @author yonghuan
 * @since 1.0.0
 */
class ApiAction : ActionSupport() {

    @Inject("struts.action.extension")
    private val actionExtension: String? = null

    @Inject("struts.devMode")
    private val devMode: String? = null
    @Autowired
    lateinit var apiViewWriter: ApiViewWriter
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private var url: String? = null

    @Throws(Exception::class)
    override fun execute(): String? {
        val request = ServletActionContext.getRequest()
        val props = loadSettings(request)
        apiViewWriter.writeApis(request, ServletActionContext.getResponse(), props)
        return null
    }


    @Deprecated("")
    @Throws(Exception::class)
    fun toIndex() {
        index()
    }

    /**
     * @since 1.2.0
     * @throws Exception
     */
    @Throws(Exception::class)
    fun index() {
        val request = ServletActionContext.getRequest()
        var lang = request.getParameter("lang")
        if (StringUtils.isBlank(lang)) {
            lang = "zh-cn"
        }
        val props = loadSettings(request)
        apiViewWriter.writeIndex(request, ServletActionContext.getResponse(), lang, props)
    }

    fun setUrl(url: String) {
        this.url = url
    }

    /**
     * @since 1.2.0
     */
    @Throws(Exception::class)
    fun statics() {
        val request = ServletActionContext.getRequest()
        val response = ServletActionContext.getResponse()
        apiViewWriter.writeStatic(request, response, loadSettings(request))
    }

    @Throws(IOException::class)
    private fun loadSettings(request: HttpServletRequest): Properties {
        val props = Properties()
        val `is` = resourceLoader.getResource("classpath:/swagger.properties").inputStream
        props.load(`is`)
        val path = request.contextPath
        val host = request.serverName + ":" + request.serverPort + path
        props.setProperty("apiHost", host)
        var apiFile = props.getProperty("apiFile")
        if (StringUtils.isBlank(apiFile)) {
            apiFile = "zh-cn"
        }
        val apiFilePath = request.servletContext.getRealPath(apiFile)
        props.setProperty("apiFile", apiFilePath)
        if (StringUtils.isBlank(props.getProperty("devMode"))) {
            props.setProperty("devMode", devMode)
        }
        var suffix = props.getProperty("suffix")
        if (StringUtils.isBlank(suffix)) {
            suffix = ""
        }
        props["suffix"] = suffix
        return props
    }
}
