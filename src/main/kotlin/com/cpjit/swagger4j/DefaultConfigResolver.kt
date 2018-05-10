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
package com.cpjit.swagger4j

import com.cpjit.swagger4j.support.Constants
import com.cpjit.swagger4j.util.ResourceUtil
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import java.io.IOException
import java.io.InputStream
import java.util.Properties
import java.util.concurrent.atomic.AtomicReference

/**
 * @author yonghuan
 * @since 2.0.1
 */
@Component
class DefaultConfigResolver : ConfigResolver {
    private var configFile = DEFAULT_CONFIG_FILE
    private val config = AtomicReference<Properties>()

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
        val `is` = ResourceUtil.getResourceAsStream(configFile)
        props.load(`is`)
        val path = request.contextPath
        val host = request.serverName + ":" + request.serverPort + path
        props.setProperty("apiHost", host)
        var apiFile = props.getProperty("apiFile")
        if (StringUtils.isBlank(apiFile)) {
            apiFile = Constants.DEFAULT_API_FILE
        }
        val apiFilePath = request.servletContext.getRealPath(apiFile)
        props.setProperty("apiFile", apiFilePath)
        var suffix = props.getProperty("suffix")
        if (StringUtils.isBlank(suffix)) {
            suffix = ""
        }
        props["suffix"] = suffix
        return props
    }

    companion object {

        val DEFAULT_CONFIG_FILE = "swagger.properties"
    }

}
