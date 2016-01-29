package com.yxlisv.util.string;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.web.RequestUtil;


/**
 * 自动从url中获取值设置到request中，并备份上一个访问的URL
 * @author yxl
 */
public class UrlValueFilter implements Filter {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//常用的关键词
	private static List keyList = new ArrayList();
	static{
		keyList.add("msgKey");
		keyList.add("msg");
		keyList.add("message");
		keyList.add("warning");
		keyList.add("warningMsg");
		keyList.add("error");
		keyList.add("errorMsg");
		
		keyList.add("method");
		keyList.add("yxl");
		keyList.add("pn");
		keyList.add("pageNum");
		keyList.add("pageSize");
		keyList.add("rd");
		keyList.add("onlydata");
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Initializing UrlValueFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		//从url中把常用的消息设置到request中去，返回到页面
		for(Iterator it=keyList.iterator(); it.hasNext();){
			String keyStr = it.next().toString();
			this.setValueFromUrl(keyStr, (HttpServletRequest) request);
		}
		
		//设置用户最后访问的连接
		this.setLastUrl((HttpServletRequest) request);
		
		//执行
		chain.doFilter(request, response);
	}
	
	/**
	 * 设置用户最后访问的连接
	 * @throws UnsupportedEncodingException 
	 * @autor yxl
	 */
	private void setLastUrl(HttpServletRequest request) throws UnsupportedEncodingException{
		String lastUrl = request.getRequestURI();
		lastUrl = lastUrl.replaceAll("_[0-9]+", "");
        request.setAttribute("lastUrl", lastUrl);
        String lastUrlSuffix = FilePathUtil.getSuffix(lastUrl);//url的后缀
        lastUrlSuffix = "." + lastUrlSuffix;
        String lastUrlNoSuffix = lastUrl;//没有后缀的
        if(lastUrlSuffix.length() > 0) lastUrlNoSuffix = lastUrlNoSuffix.replace(lastUrlSuffix, "");
        
        request.setAttribute("lastUrlSuffix", lastUrlSuffix);
        request.setAttribute("lastUrlNoSuffix", lastUrlNoSuffix);
	}
	
	
	
	/**
	 * 从url中获取值设置到request中
	 * @author yxl
	 */
	private void setValueFromUrl(String key, HttpServletRequest request){
		if(RequestUtil.getString(key, request) != null){
			String sVal = RequestUtil.getString(key, request);
			request.setAttribute(key, sVal);
		}
	}
	
	@Override
	public void destroy() {
		logger.info("Destroy UrlValueFilter");
	}
}