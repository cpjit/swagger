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
class Parameter {

    constructor()
    constructor(name: String?, `in`: String?) {
        this.name = name
        this.`in` = `in`
    }

    var name: String? = null
    var `in`: String? = null
    var description: String? = null
    var required: Boolean? = null
    /**
     * The schema defining the type used for the body parameter.
     */
    var schema: Schema? = null
    var type: String? = null
    var format: String? = null
    var default: Any? = null
    /**
     * Required if type is "array". Describes the type of items in the array.
     */
    var items: Parameter? = null
    var enum: List<Any>? = null
}