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
package com.cpjit.swagger4j.parameter

import com.cpjit.swagger4j.Api
import com.cpjit.swagger4j.annotation.Item
import com.cpjit.swagger4j.annotation.Param
import com.cpjit.swagger4j.specification.Parameter
import com.cpjit.swagger4j.specification.Specification

/**
 * 请求参数配置解析器。
 *
 * @author yonghuan
 * @since 2.1.6
 */
internal interface ParameterResolver {

    fun supportsParameter(api: Api, param: Param): Boolean

    fun resolveParameter(url: String, api: Api, param: Param, parameters: MutableList<Parameter>, spec: Specification)

}