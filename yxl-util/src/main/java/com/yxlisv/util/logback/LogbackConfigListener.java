package com.yxlisv.util.logback;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.yxlisv.util.file.FilePathUtil;

/**
 * logback 配置文件控制
 * 依赖包：
 * log4j-over-slf4j.jar,logback-classic.jar,logback-core.jar,jcl-over-slf4j.jar
 * com.springsource.slf4j.api-1.6.1.jar
 * @author yxl
 * @version 1.0 
 */
public class LogbackConfigListener implements ServletContextListener {

	//配置文件路径KEY
	private static final String CONFIG_LOCATION_KEY = "logbackConfigLocation";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// 从web.xml中加载指定文件名的日志配置文件
		String logbackConfigLocation = event.getServletContext().getInitParameter(CONFIG_LOCATION_KEY);
		try {
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			loggerContext.reset();
			//把web.xml中配置的項目名稱給logback使用
			loggerContext.putProperty("ServletContextName", event.getServletContext().getServletContextName());
			loggerContext.putProperty("log.path", FilePathUtil.getWebRoot());
			
			JoranConfigurator joranConfigurator = new JoranConfigurator();
			joranConfigurator.setContext(loggerContext);
			//重定向配置文件路径
			if(logbackConfigLocation!=null) joranConfigurator.doConfigure(event.getServletContext().getRealPath(logbackConfigLocation));
			else joranConfigurator.doConfigure(event.getServletContext().getRealPath("WEB-INF/classes/logback.xml"));
		} catch (JoranException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}