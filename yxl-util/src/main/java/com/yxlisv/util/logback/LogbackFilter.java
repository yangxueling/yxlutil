package com.yxlisv.util.logback;

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
import org.slf4j.MDC;

import com.yxlisv.util.map.MapUtil;


/**
 * Logback Filter
 * @author yxl
 */
public class LogbackFilter implements Filter {
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Initializing LogbackFilter...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	    clearMDC();
	    HttpServletRequest httpReq = ((HttpServletRequest)request);
	    initMDC(httpReq);
		chain.doFilter(request, response);
	}
	
	/**
	 * 清空MDC值
	 * @date 2015年12月2日 下午4:42:53 
	 * @author yxl
	 */
	private void initMDC(HttpServletRequest httpReq) {
		MDC.put("req.remoteHost", format("Host", httpReq.getRemoteHost()));
		MDC.put("req.method", format("Method", httpReq.getMethod()));
		StringBuffer requestURL = httpReq.getRequestURL();
	    if (requestURL != null)  MDC.put("req.requestURL", format("URL", requestURL.toString()));
		MDC.put("req.referer", format("Referer", httpReq.getHeader("referer")));
	    MDC.put("req.locale", format("Locale", httpReq.getLocale().toString()));
	    MDC.put("req.queryString",  format("Param", MapUtil.toString(httpReq.getParameterMap(), 30)));
		if(httpReq.getSession().getAttribute("logFileMark")!=null) MDC.put("logFileMark", httpReq.getSession().getAttribute("logFileMark").toString());//日志文件标记
		if(httpReq.getSession().getAttribute("logUserMark")!=null) MDC.put("logUserMark", format("User", httpReq.getSession().getAttribute("logUserMark").toString()));//日志用户标记
	}
	
	/**
	 * 消息格式化
	 * @date 2015年12月2日 下午8:30:27 
	 * @author yxl
	 */
	private String format(String key, String msg){
		if(msg==null || msg.trim().length()<1) return "";
		return "[" + key + " : " + msg + "]";
	}
	
	/**
	 * 清空MDC值
	 * @date 2015年12月2日 下午4:42:53 
	 * @author yxl
	 */
	private void clearMDC() {
		MDC.remove("req.remoteHost");
		MDC.remove("req.method");
		MDC.remove("req.requestURL");
		MDC.remove("req.referer");
		MDC.remove("req.locale");
		MDC.remove("req.queryString");
		MDC.remove("logFileMark");
		MDC.remove("logUserMark");
	}
	
	@Override
	public void destroy() {
		logger.info("Destroy LogbackFilter");
	}
}