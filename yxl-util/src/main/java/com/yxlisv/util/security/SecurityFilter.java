package com.yxlisv.util.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过过滤解决SQL漏洞、跨站脚本漏洞编制
 * 
 * @author yxl
 */
public class SecurityFilter implements Filter {

	/** 定义一个全局的记录器，通过LoggerFactory获取 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Initializing SecurityFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new SecurityHttpServletRequestWrapper((HttpServletRequest) request), response);
	}

	@Override
	public void destroy() {
		logger.info("Destroy SecurityFilter");
	}
}