package com.yxl.util.redis.session;

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
 * Redis HttpSession过滤器
 * 使用redis 共享 session
 * @author yxl
 */
public class RedisSessionFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(RedisSessionFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Initializing RedisSessionFilter...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new RedisHttpServletRequestWrapper((HttpServletRequest) request, (HttpServletResponse) response), response);
	}

	@Override
	public void destroy() {
		logger.info("Destroy RedisSessionFilter...");
	}
}