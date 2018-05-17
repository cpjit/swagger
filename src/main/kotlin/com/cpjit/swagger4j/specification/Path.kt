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
class Path(val url: String) {

    /**
     * A definition of a GET operation on this path.
     */
    var get: Operation? = null
    /**
     * A definition of a PUT operation on this path.
     */
    var put: Operation? = null
    /**
     * A definition of a POST operation on this path.
     */
    var post: Operation? = null
    /**
     * A definition of a DELETE operation on this path.
     */
    var delete: Operation? = null
    /**
     * A definition of a OPTIONS operation on this path.
     */
    var options: Operation? = null
    /**
     * A definition of a HEAD operation on this path.
     */
    var head: Operation? = null
    /**
     * A definition of a PATCH operation on this path.
     */
    var patch: Operation? = null
}