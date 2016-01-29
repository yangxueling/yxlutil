package com.yxlisv.util.logback;

import javax.servlet.http.HttpSession;

import org.slf4j.MDC;

/**
 * Logback工具类
 * @author john Local
 *
 */
public class LogbackUtil {

	/**
	 * 设置文件标记
	 * @param mark 标记
	 * @autor yxl
	 * 2014-7-18
	 */
	public static void setFileMark(String mark, HttpSession session){
		session.setAttribute("logFileMark", mark);//日志标记
		MDC.put("logFileMark", mark);//立即生效
	}
	
	/**
	 * 设置用户标记
	 * @param mark 标记
	 * @autor yxl
	 * 2014-7-18
	 */
	public static void setUserMark(String mark, HttpSession session){
		session.setAttribute("logUserMark", mark);//日志标记
		MDC.put("logUserMark", mark);//立即生效
	}
}
