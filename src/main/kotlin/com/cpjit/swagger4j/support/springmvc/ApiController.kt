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
package com.cpjit.swagger4j.support.springmvc

import com.cpjit.swagger4j.support.internal.ApiViewWriter
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author yonghuan
 * @since 1.0.0
 */
@Controller
@RequestMapping("/api")
class ApiController : InitializingBean {

    @Autowired
    @Qualifier("defaultApiViewWriter")
    lateinit var apiViewWriter: ApiViewWriter

    @RequestMapping(value = "index", method = [RequestMethod.GET])
    @Throws(Exception::class)
    fun index(request: HttpServletRequest, response: HttpServletResponse, @RequestParam(defaultValue = "zh-cn") lang: String) {
        apiViewWriter.writeIndex(request, response, lang)
    }

    @RequestMapping(value = "", method = [RequestMethod.GET])
    @Throws(Exception::class)
    fun queryApi(request: HttpServletRequest, response: HttpServletResponse) {
        apiViewWriter.writeApis(request, response)
    }

    /**
     * @since 1.2.0
     */
    @RequestMapping(value = "statics/**", method = [RequestMethod.GET])
    @Throws(Exception::class)
    fun queryStatic(request: HttpServletRequest, response: HttpServletResponse) {
        apiViewWriter.writeStatic(request, response)
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
    }

}
