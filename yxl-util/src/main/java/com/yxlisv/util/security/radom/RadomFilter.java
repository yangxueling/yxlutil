package com.yxlisv.util.security.radom;

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
 * 随机数生成器
 * @author yxl
 */
public class RadomFilter implements Filter {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		//设置 randomStrKey
		if(filterConfig.getInitParameter("randomStrKey") != null)
			WebRadomUtil.RD_KEY = filterConfig.getInitParameter("randomStrKey");
		
		logger.info("Initializing RadomFilter, randomStrKey=" + WebRadomUtil.RD_KEY);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		WebRadomUtil.add((HttpServletRequest) request);
		
		HttpServletResponse res = (HttpServletResponse) response;  
		res.setHeader("P3P","CP=CAO PSA OUR");//iframe引起的内部cookie丢失  
		//执行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		logger.info("Destroy RadomFilter");
	}
}