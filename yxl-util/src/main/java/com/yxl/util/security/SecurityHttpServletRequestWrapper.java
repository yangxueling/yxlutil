package com.yxl.util.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @Title:HttpServletRequest包装器，主要解决SQL漏洞、跨站脚本漏洞编制，如果需要禁用HTTP方法，可以在web.xml中进行配置
 * chain.doFilter(new SecurityHttpServletRequestWrapper((HttpServletRequest) request), response);
 * @Company: 深圳太极软件有限公司
 * @Author 杨雪令
 * @Date: 2010-05-28
 * @version: 1.0
 */
public class SecurityHttpServletRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * 重写 getContextPath
	 */
	@Override
	public String getContextPath() {
		return SecurityUtil.simpleClear(super.getContextPath());
	}


	/**
	 * 重写 getPathInfo
	 */
	@Override
	public String getPathInfo() {
		return SecurityUtil.simpleClear(super.getPathInfo());
	}


	/**
	 * 重写 getQueryString
	 */
	@Override
	public String getQueryString() {
		return SecurityUtil.simpleClear(super.getQueryString());
	}


	/**
	 * 重写 getRequestURI
	 */
	@Override
	public String getRequestURI() {
		return SecurityUtil.simpleClear(super.getRequestURI());
	}


	/**
	 * 重写 getServletPath
	 */
	@Override
	public String getServletPath() {
		return SecurityUtil.simpleClear(super.getServletPath());
	}


	/**
	 * 重写 getParameterMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, String[]> getParameterMap() {
		Map paramMap = new HashMap();
		for(Iterator it=super.getParameterMap().entrySet().iterator(); it.hasNext();){
			Entry entry = (Entry) it.next();
			String[] values = (String[]) entry.getValue();
			if(values != null && values.length>0){
				for(int i=0; i<values.length; i++){
					String value = values[i];
					if(value != null){
						value = SecurityUtil.simpleClear(value);
						values[i] = value;
					}
				}
			}
			paramMap.put(SecurityUtil.simpleClear(entry.getKey().toString()), values);
		}
		return paramMap;
	}


	/**
	 * 默认构造方法
	 * 
	 * @param request
	 */
	public SecurityHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}


	/**
	 * 重写 getRequestURL
	 */
	public StringBuffer getRequestURL() {
		StringBuffer url = super.getRequestURL();
		return new StringBuffer(SecurityUtil.simpleClear(url.toString()));
	}


	/**
	 * 重写 getParameter
	 * 截取表单数据检查
	 * 替换掉不合法的字符
	 * 返回处理过的数据
	 */
	public String getParameter(String name) {
		String value = super.getParameter(name);

		if (value != null) {
			value = SecurityUtil.simpleClear(value.trim());
		}

		return value;
	}
	
	/**
	 * 重写 getParameterValues
	 * 截取表单数据检查
	 * 替换掉不合法的字符
	 * 返回处理过的数据
	 */
	public String[] getParameterValues(String name) {
		String str[] = super.getParameterValues(name);
		if (str == null) {
			return null;
		}
		int i = str.length;
		String parameterValues[] = new String[i];
		for (int j = 0; j < i; j++) {
			parameterValues[j] = SecurityUtil.simpleClear(str[j]);
		}
		return parameterValues;
	}
}