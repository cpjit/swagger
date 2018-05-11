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
package com.cpjit.swagger4j.support.internal

import com.alibaba.fastjson.JSONWriter
import com.cpjit.swagger4j.APIParser
import com.cpjit.swagger4j.ConfigResolver
import com.cpjit.swagger4j.support.internal.templates.FreemarkerUtils
import freemarker.template.TemplateException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author yonghuan
 * @since 1.2.0
 */
@Component("defaultApiViewWriter")
open class DefaultApiViewWriter : ApiViewWriter {

    @Autowired
    lateinit var configResolver: ConfigResolver
    @Autowired
    lateinit var resourceLoader: ResourceLoader
    protected open val templateName: String
        get() = "api.ftlh"
    private val isScanfed = AtomicBoolean()

    @Deprecated("")
    @Throws(IOException::class)
    override fun writeIndex(request: HttpServletRequest, response: HttpServletResponse, lang: String, props: Properties) {
        val disabled = props.getProperty("disabled", "false")
        if (java.lang.Boolean.TRUE == java.lang.Boolean.valueOf(disabled)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }
        val root = HashMap<String, Any>()
        root["lang"] = lang
        val path = request.contextPath
        val basePath = request.scheme + "://" + request.serverName + ":" + request.serverPort + path + "/"
        root["basePath"] = basePath
        val host = request.serverName + ":" + request.serverPort + path
        var suffix = props.getProperty("suffix")
        if (StringUtils.isBlank(suffix)) {
            suffix = ""
        }
        root["getApisUrl"] = request.scheme + "://" + host + "/api" + suffix
        root["apiDescription"] = props.getProperty("apiDescription")
        root["apiTitle"] = props.getProperty("apiTitle")
        root["apiVersion"] = props.getProperty("apiVersion")
        root["suffix"] = suffix
        val template = FreemarkerUtils.getTemplate(templateName)
        response.contentType = "text/html;charset=utf-8"
        val out = response.writer
        try {
            template!!.process(root, out)
        } catch (e: TemplateException) {
            throw IOException(e)
        }

        out.flush()
        out.close()
    }

    @Throws(IOException::class)
    override fun writeIndex(request: HttpServletRequest, response: HttpServletResponse, lang: String) {
        writeIndex(request, response, lang, configResolver.obtainConfig(request))
    }

    @Deprecated("")
    @Throws(Exception::class)
    override fun writeApis(request: HttpServletRequest, response: HttpServletResponse, props: Properties) {
        val disabled = props.getProperty("disabled", "false")
        if ("true".equals(disabled, true)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }
        val restParser = APIParser.newInstance(props)
        response.contentType = "application/json;charset=utf-8"
        val devMode = props.getProperty("devMode")
        if ("true".equals(devMode, true)) {
            val apis = restParser.parseAndNotStore()
            val writer = JSONWriter(response.writer)
            writer.writeObject(apis)
            writer.flush()
            writer.close()
        } else {
            if (!isScanfed.get()) {
                restParser.parse()
                isScanfed.set(true)
            }
            val bs = FileUtils.readFileToByteArray(File(props.getProperty("apiFile")))
            val out = response.outputStream
            out.write(bs)
            out.flush()
            out.close()
        }
    }

    @Throws(Exception::class)
    override fun writeApis(request: HttpServletRequest, response: HttpServletResponse) {
        writeApis(request, response, configResolver.obtainConfig(request))
    }

    /**
     * @since 1.2.2
     */
    protected open fun buildResourcePath(request: HttpServletRequest, config: Properties?): String {
        var uri = request.requestURI
        // 去掉servlet contextPath
        uri = uri.substring(uri.indexOf(request.contextPath))
        val suffix = config!!["suffix"] as String
        if(StringUtils.isNotBlank(suffix)) {
            // 去掉请求统一后缀，如 .do
            val index = uri.lastIndexOf(suffix)
            if (index > 0) {
                uri = uri.substring(0, index)
            }
        }
        var path = uri.substring(uri.indexOf("statics") + 7)
        return "com/cpjit/swagger4j/support/internal/statics$path"
    }

    @Deprecated("")
    protected open fun buildResourcePath(request: HttpServletRequest): String {
        return buildResourcePath(request, null)
    }

    @Throws(IOException::class)
    override fun writeStatic(request: HttpServletRequest, response: HttpServletResponse, props: Properties) {
        val path = buildResourcePath(request, props)

        val contentType = FileTypeMap.getContentType(path)
        response.contentType = contentType
        val resource = resourceLoader.getResource("classpath:$path")
        if (!resource.exists()) {
            response.sendError(404)
            return
        }
        var out: OutputStream? = null
        var `is` = resource.inputStream
        try {
            out = response.outputStream
            val buff = ByteArray(512)
            var len: Int
            while (true) {
                len = `is`.read(buff)
                if (len == -1) {
                    break
                }
                out!!.write(buff, 0, len)
            }
            out!!.flush()
        } finally {
            IOUtils.closeQuietly(`is`)
            IOUtils.closeQuietly(out)
        }
    }

    @Deprecated("")
    @Throws(IOException::class)
    override fun writeStatic(request: HttpServletRequest, response: HttpServletResponse) {
        writeStatic(request, response, configResolver.obtainConfig(request))
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DefaultApiViewWriter::class.java)
    }
}
