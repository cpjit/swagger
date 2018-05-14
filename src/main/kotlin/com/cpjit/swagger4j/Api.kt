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

import com.cpjit.swagger4j.annotation.Param

/**
 * @author yonghuan
 */
internal class Api {
    var value = ""
    var tags = arrayOf<String>()
    var method = ""
    var summary = ""
    var description = ""
    var operationId = ""
    var consumes = arrayOf<String>()
    var produces = arrayOf<String>()
    var parameters = arrayOf<Param>()
    var deprecated: Boolean = false
    var hide: Boolean = false
}
