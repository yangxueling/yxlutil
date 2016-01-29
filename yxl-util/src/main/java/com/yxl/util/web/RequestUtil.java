package com.yxl.util.web;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP 请求工具
 * @author yxl
 */
public class RequestUtil {

	/**
	 * 获取字符串
	 * @param key 索引
	 * @autor yxl
	 * 2013-11-2
	 */
	public static String getString(String key, HttpServletRequest request){
		String val = request.getParameter(key);
		if(val == null && request.getAttribute(key) != null) val = request.getAttribute(key).toString();
		return val;
	}
}
