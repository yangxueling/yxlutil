package com.yxlisv.util.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Http工具类</p>
 * @author 杨雪令
 * @time 2016年3月8日下午1:11:03
 * @version 1.0
 */
public class HttpUtil {

	// 定义一个全局的记录器，通过LoggerFactory获取
	protected static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * <p>输入文本到HttpServletResponse</p>
	 * @param str 要输出的字符串
	 * @param response HttpServletResponse 对象 
	 * @author 杨雪令
	 * @time 2016年3月8日下午1:09:08
	 * @version 1.0
	 */
	public static void outputText(String str, HttpServletResponse response) {
		setResponseContentType(response);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(str);
		} catch (IOException e) {
			logger.error("输入文本到HttpServletResponse出错", e);
		} finally {
			if (out != null) out.close();
		}
	}

	/**
	 * <p>设置HttpServletResponse对象的ContentType</p>
	 * @param response HttpServletResponse 对象 
	 * @author 杨雪令
	 * @time 2016年3月8日下午1:06:50
	 * @version 1.0
	 */
	public static void setResponseContentType(HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
	}
}
