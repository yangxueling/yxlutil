package com.yxlisv.util.datasource;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>动态数据源过滤器</p>
 * <p>通过配置过滤器自动切换数据源</p>
 * @author 杨雪令
 * @time 2016年3月12日下午1:37:57
 * @version 1.0
 */
public class DynamicDataSourceFilter implements Filter {

	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Initializing DynamicDataSourceFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		try {
			//如果用户有自定义数据源，那么激活用户的数据源
			if (session.getAttribute(DynamicDataSource.DATA_SOURCE_HTTP_SESSION_KEY) != null) DynamicDataSource.active((DataSourceBean) session.getAttribute(DynamicDataSource.DATA_SOURCE_HTTP_SESSION_KEY));
			chain.doFilter(request, response);// 执行业务逻辑
		} finally {
			DynamicDataSource.release();
		}
	}

	@Override
	public void destroy() {
		logger.info("Destroy DynamicDataSourceFilter");
	}
}