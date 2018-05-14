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

/**
 * API文档信息。
 *
 * @author yonghaun
 * @since 1.0.0
 */
class APIDocInfo {
    var description: String? = null
    var version: String? = null
    var title: String? = null
    var termsOfService: String? = null
    var contact: String? = null
    var license: License? = null
}
