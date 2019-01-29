/*
 * Copyright 2011-2019 CPJIT Group.
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
package com.cpjit.swagger4j;

import com.cpjit.swagger4j.support.Constants;
import com.cpjit.swagger4j.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yonghuan
 * @since 2.0.1
 */
public class DefaultConfigResolver implements ConfigResolver {

    public final static String DEFAULT_CONFIG_FILE = "swagger.properties";
    private String configFile = DEFAULT_CONFIG_FILE;
    private AtomicReference<Properties> config = new AtomicReference<>();

    public DefaultConfigResolver() {
    }

    public DefaultConfigResolver(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public Properties obtainConfig(HttpServletRequest request) throws IOException {
        if(config.get() == null) {
            config.set(loadConfig(request));
        }
        return config.get();
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    private Properties loadConfig(HttpServletRequest request) throws IOException {
        Properties props = new Properties();
        InputStream is = ResourceUtil.getResourceAsStream(configFile);
        props.load(is);
        String path = request.getContextPath();
        String host = request.getServerName() + ":" + request.getServerPort() + path;
        props.setProperty("apiHost", host);
        String apiFile = props.getProperty("apiFile");
        if (StringUtils.isBlank(apiFile)) {
            apiFile = Constants.DEFAULT_API_FILE;
        }
        String apiFilePath = request.getServletContext().getRealPath(apiFile);
        props.setProperty("apiFile", apiFilePath);
        String suffix = props.getProperty("suffix");
        if(StringUtils.isBlank(suffix)) {
            suffix = "";
        }
        props.put("suffix", suffix);
        return props;
    }

}
