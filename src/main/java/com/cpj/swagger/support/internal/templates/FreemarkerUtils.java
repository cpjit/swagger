/*
 *
 * Copyright (c) 2011, 2016 CPJ and/or its affiliates. All rights reserved.
 * 
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
