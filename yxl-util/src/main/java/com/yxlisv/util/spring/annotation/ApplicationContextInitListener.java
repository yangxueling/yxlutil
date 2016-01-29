package com.yxlisv.util.spring.annotation;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化 ApplicationContextUtil
 * @author: yxl
 * @version 1.0
 */
public class ApplicationContextInitListener implements ServletContextListener  {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ApplicationContextUtil.init(sce.getServletContext());
		logger.info("Initializing ApplicationContextUtil");
	}
}
