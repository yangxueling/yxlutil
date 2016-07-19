package com.yxlisv.util.context;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Web相关上下文过滤器</p>
 * @author 杨雪令
 * @time 2016年3月17日下午4:44:21
 * @version 1.0
 */
public class WebContextFilter implements Filter {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		logger.info("Initializing WebContextFilter...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		WebContext.setServletRequest(httpRequest);
		WebContext.setServletResponse(httpResponse);
		WebContext.setServletContext(httpRequest.getServletContext());
		//执行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		logger.info("Destroy WebContextFilter");
	}
}