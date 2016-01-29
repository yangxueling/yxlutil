package com.yxl.util.web;

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
 * 域名标准化跳转
 * @author yxl
 */
public class DomainStandardFilter implements Filter {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		logger.info("Initializing DomainStandardFilter...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String requestUrl = httpRequest.getRequestURL().toString();
		if(requestUrl.startsWith("http://www.")) {
			requestUrl = requestUrl.replaceAll("http://www.", "http://");
			httpResponse.setStatus(301);
			String queryString = (httpRequest.getQueryString() == null ? "" : "?" + httpRequest.getQueryString());
			httpResponse.setHeader("Location", requestUrl + queryString);  
			httpResponse.setHeader("Connection", "close");
			return;
		}
		//执行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		logger.info("Destroy DomainStandardFilter");
	}
}