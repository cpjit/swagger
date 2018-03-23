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
package com.cpjit.swagger4j.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 查找资源相关的工具方法。
 * <p><b>Note:</b>该类用于从classpath下查找资源。</p>
 *
 * @author yonghuan
 * @since 1.2.2
 * @deprecated 在多模块项目中，该类不能查找到其他模块中的资源，
 * 推荐使用  {@link org.springframework.core.io.support.ResourcePatternResolver} 替代。
 */
@Deprecated
public final class ResourceUtil {
    private final static Logger LOG = LoggerFactory.getLogger(ResourceUtil.class);
    private final static Class<?> DEFAULT_LOADER = ResourceUtil.class;

    private ResourceUtil() {
        throw new AssertionError("不允许实例化 " + ResourceUtil.class.getName());
    }

    /**
     * 根据资源文件名获取资源文件的URL。
     *
     * @param loader loader
     * @param name   资源文件名
     * @return 资源文件的URL。
     */
    public static URL getResource(Class<?> loader, String name) {
        URL url;
        Class<?> l = loader;
        if (l == null) {
            l = DEFAULT_LOADER;
        }
        url = l.getResource("/" + name);
        if (url == null) {
            url = ClassLoader.getSystemResource(name);
        }
        return url;
    }

    /**
     * 根据资源文件名获取资源文件的URL。
     *
     * @param name 资源文件名
     * @return 资源文件的URL。
     */
    public static URL getResource(String name) {
        return getResource(DEFAULT_LOADER, name);
    }

    /**
     * 根据资源文件名获取资源文件。
     *
     * @param loader
     * @param name   资源文件名
     * @return 资源文件
     * @since 1.0.4
     */
    public static File getResourceAsFile(Class<?> loader, String name) {
        URL url = getResource(loader, name);
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * 根据资源文件名获取资源文件。
     *
     * @param name name
     * @return 资源文件
     * @since 1.0.4
     */
    public static File getResourceAsFile(String name) {
        return getResourceAsFile(DEFAULT_LOADER, name);
    }

    public static InputStream getResourceAsStream(Class<?> loader, String name) {
        URL url = getResource(loader, name);
        // 未找到资源
        if (url == null) {
            return null;
        }

        InputStream is = null;
        try {
            is = FileUtils.openInputStream(new File(url.toURI()));
        } catch (Exception e) {
            LOG.error(StringUtils.join("查找资源文件 ", name, " 发生错误"), e);
        }
        return is;
    }

    public static InputStream getResourceAsStream(String name) {
        return getResourceAsStream(DEFAULT_LOADER, name);
    }
}
