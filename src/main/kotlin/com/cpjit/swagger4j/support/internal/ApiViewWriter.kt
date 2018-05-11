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
package com.cpjit.swagger4j.support.internal

import java.io.IOException
import java.util.Properties

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author yonghuan
 * @since 1.2.0
 */
interface ApiViewWriter {

    @Deprecated("")
    @Throws(IOException::class)
    fun writeIndex(request: HttpServletRequest, response: HttpServletResponse, lang: String, props: Properties)

    /**
     * @since 2.0.1
     */
    @Throws(IOException::class)
    fun writeIndex(request: HttpServletRequest, response: HttpServletResponse, lang: String)

    @Deprecated("")
    @Throws(Exception::class)
    fun writeApis(request: HttpServletRequest, response: HttpServletResponse, props: Properties)

    /**
     * @since 2.0.1
     */
    @Throws(Exception::class)
    fun writeApis(request: HttpServletRequest, response: HttpServletResponse)

    @Deprecated("")
    @Throws(IOException::class)
    fun writeStatic(request: HttpServletRequest, response: HttpServletResponse)

    /**
     * @since 1.2.2
     */
    @Throws(IOException::class)
    fun writeStatic(request: HttpServletRequest, response: HttpServletResponse, props: Properties)
}
