package com.yxlisv.util.monitor;

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
 * 性能监控 Filter
 * @author yxl
 */
public class MonitorFilter implements Filter {

	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/** 警告时间，超过这个时间之后，就发出警告信息 */
	protected int warnTime = 1000;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (filterConfig.getInitParameter("warnTime") != null) {
			try {
				this.warnTime = Integer.parseInt(filterConfig.getInitParameter("warnTime"));
			} catch (Exception e) {}
		}
		logger.info("Initializing MonitorFilter, warnTime=" + this.warnTime + "ms");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		long startTime = System.currentTimeMillis();
		// 执行
		chain.doFilter(request, response);

		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;

		String str = "used: " + runTime + "ms  (URI: " + ((HttpServletRequest) request).getRequestURI() + ")";

		// 大于1秒的记录下来
		if (runTime > warnTime) {
			logger.warn(str);
		} else {
			if(str.contains("/images/")) return;
			if(str.contains("/js/")) return;
			if(str.contains("/css/")) return;
			if(str.contains("/styles/")) return;
			if(str.contains("/image/")) return;
			if(str.contains("/style/")) return;
			logger.debug(str);
		}
	}

	@Override
	public void destroy() {
		logger.info("Destroy MonitorFilter");
	}
}