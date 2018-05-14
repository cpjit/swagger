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

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.servlet.http.HttpServletRequest

/**
 * @author yonghuan
 * @since 2.0.1
 */
@Component
class DefaultConfigResolver : ConfigResolver {

    /**
     *  生成文件的默认保存地址。
     */
    val defaultApiFile = "/WEB-INF/apis.json"
    private var configFile = "classpath:/swagger.properties"
    private val config = AtomicReference<Properties>()
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    constructor() {}

    constructor(configFile: String) {
        this.configFile = configFile
    }

    @Throws(IOException::class)
    override fun obtainConfig(request: HttpServletRequest): Properties {
        if (config.get() == null) {
            config.set(loadConfig(request))
        }
        return config.get()
    }

    fun setConfigFile(configFile: String) {
        this.configFile = configFile
    }

    @Throws(IOException::class)
    private fun loadConfig(request: HttpServletRequest): Properties {
        val props = Properties()
        val `is` = resourceLoader.getResource(configFile).inputStream
        props.load(`is`)
        val path = request.contextPath
        val host = request.serverName + ":" + request.serverPort + path
        props.setProperty("apiHost", host)
        var apiFile = props.getProperty("apiFile")
        if (StringUtils.isBlank(apiFile)) {
            apiFile = defaultApiFile
        }
        val apiFilePath = request.getRealPath(apiFile)
        props.setProperty("apiFile", apiFilePath)
        var suffix = props.getProperty("suffix")
        if (StringUtils.isBlank(suffix)) {
            suffix = ""
        }
        props["suffix"] = suffix
        return props
    }

}
