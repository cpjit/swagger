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
package com.cpj.swagger.support.internal.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author yonghuan
 * @since 1.2.0
 */
public final class FreemarkerUtils {
	
	public static Template getTemplate(String name) {
		Template template = null;
		try {
			template = sCfg.getTemplate(name);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return template;
	}
	
	private final static Configuration sCfg; 
	static {
		sCfg = new Configuration(Configuration.VERSION_2_3_23);
        sCfg.setClassLoaderForTemplateLoading(FreemarkerUtils.class.getClassLoader(), "com/cpj/swagger/support/internal/templates/ftlh");
        sCfg.setDefaultEncoding("UTF-8");
        sCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        sCfg.setLogTemplateExceptions(false);
	}
}
