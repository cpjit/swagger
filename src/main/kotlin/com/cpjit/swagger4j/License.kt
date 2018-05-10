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

/**
 * API遵循的协议。
 *
 * @author yonghaun
 * @since 1.0.0
 */
class License {
    /**
     * @return 协议名
     */
    /**
     * @param name 协议名
     */
    var name: String? = null
    /**
     * @return 协议主页
     */
    /**
     * @param url 协议主页
     */
    var url: String? = null

    constructor() {}

    /**
     * @since 1.0.3
     */
    constructor(name: String, url: String) {
        this.name = name
        this.url = url
    }
}
