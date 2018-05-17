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
package com.cpjit.swagger4j.specification


/**
 * @author yonghuan
 * @since 2.1.6
 */
class Operation(val operationId: String) {

    var tags: Array<String>? = null
    var summary: String? = null
    var description: String? = null
    var consumes: Array<String>? = null
    var produces: Array<String>? = null
    /**
     * The transfer protocol for the operation. Values MUST be from the list: "http", "https", "ws", "wss".
     */
    var schemes: Array<String>? = null
    var deprecated = false
    var parameters: List<Parameter>? = null
}