package com.yxlisv.util.hibernate;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>打开Hibernate Session直到页面渲染结束</p>
 * @author 杨雪令
 * @time 2016年3月9日上午11:25:58
 * @version 1.0
 */
public class OpenSessionInViewFilter implements Filter {

	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//初始化过滤器，设置SessionManager支持OpenSessionInView
		SessionManager.setOpenSessionInView(true);
		logger.info("Initializing Hibernate OpenSessionInViewFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);// 执行业务逻辑
		} finally {
			SessionManager.forceCloseCurrentSession();
		}
	}

	@Override
	public void destroy() {
		logger.info("Destroy Hibernate OpenSessionInViewFilter");
	}
}