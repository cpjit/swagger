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

import com.cpjit.swagger4j.annotation.Item

/**
 * @author yonghuan
 * @since 2.1.6
 */
class Specification {

    var swagger = "2.0"
    var host: String? = null
    var basePath: String? = null
    var schemes = arrayOf("http")
    var tags: MutableList<Tag> = mutableListOf()
    var paths: MutableList<Path> = mutableListOf()
    var definitions: MutableList<Schema> = mutableListOf()
    @Deprecated(message ="为了兼容2.1.6以前的版本而保留的属性")
    var items: MutableMap<String, Item>? = null
}