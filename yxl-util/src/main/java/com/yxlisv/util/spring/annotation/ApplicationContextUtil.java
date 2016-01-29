package com.yxlisv.util.spring.annotation;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * ApplicationContext 工具类
 * @author yxl
 */
@Component
public class ApplicationContextUtil {
	
	//spring ApplicationContext
	private static ApplicationContext applicationContext;
	
	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationContextUtil.applicationContext = applicationContext;
	}
	
	/**
	 * 初始化applicationContext，如果是获取annotation实例，不用初始化，可以自动注入，如果要获取xml文件中的实例，配置com.yxlisv.util.spring.ApplicationContextInitListener可以自动初始化
	 * @autor yxl
	 */
	public static void init(ServletContext sc){
		applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
	}

	/**
	 * 获取bean
	 * @param key bean 名称
	 * @autor yxl
	 */
	public static Object getBean(String key){
		if(applicationContext == null) return null;
		return applicationContext.getBean(key);
	}
}
