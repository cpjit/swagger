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
package com.cpjit.swagger4j.util

import java.io.File
import java.io.InputStream
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 查找资源相关的工具方法。
 *
 * **Note:**该类用于从classpath下查找资源。
 * @author yonghuan
 * @since 1.2.2
 */
@Deprecated("在多模块项目中，该类不能查找到其他模块中的资源，推荐使用  {@link org.springframework.core.io.support.ResourcePatternResolver} 替代。")
class ResourceUtil private constructor() {

    init {
        throw AssertionError("不允许实例化 " + ResourceUtil::class.java.name)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ResourceUtil::class.java)
        private val DEFAULT_LOADER = ResourceUtil::class.java

        /**
         * 根据资源文件名获取资源文件的URL。
         * @param loader loader
         * @param name 资源文件名
         * @return 资源文件的URL。
         */
        fun getResource(loader: Class<*>, name: String): URL? {
            var url: URL?
            var l: Class<*>? = loader
            if (l == null) {
                l = DEFAULT_LOADER
            }
            url = l.getResource("/$name")
            if (url == null) {
                url = ClassLoader.getSystemResource(name)
            }
            return url
        }

        /**
         * 根据资源文件名获取资源文件的URL。
         * @param name 资源文件名
         * @return 资源文件的URL。
         */
        fun getResource(name: String): URL? {
            return getResource(DEFAULT_LOADER, name)
        }

        /**
         * 根据资源文件名获取资源文件。
         * @param loader
         * @param name 资源文件名
         * @return 资源文件
         * @since 1.0.4
         */
        fun getResourceAsFile(loader: Class<*>, name: String): File? {
            val url = getResource(loader, name)
            try {
                val path = Paths.get(url!!.toURI())
                return path.toFile()
            } catch (e: URISyntaxException) {
                return null
            }

        }

        /**
         * 根据资源文件名获取资源文件。
         * @param name name
         * @return 资源文件
         * @since 1.0.4
         */
        fun getResourceAsFile(name: String): File? {
            return getResourceAsFile(DEFAULT_LOADER, name)
        }

        fun getResourceAsStream(loader: Class<*>, name: String): InputStream? {
            val url = getResource(loader, name)
                    ?: // 未找到资源
                    return null

            var `is`: InputStream? = null
            try {
                `is` = Files.newInputStream(Paths.get(url.toURI()))
            } catch (e: Exception) {
                LOG.error(arrayOf("查找资源文件 ", name, " 发生错误").joinToString(""), e)
            }

            return `is`
        }

        fun getResourceAsStream(name: String): InputStream? {
            return getResourceAsStream(DEFAULT_LOADER, name)
        }
    }
}
