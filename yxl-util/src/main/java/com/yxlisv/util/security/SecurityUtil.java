package com.yxlisv.util.security;


/**
 * 安全工具类
 * @createTime 2015年10月29日 上午9:17:50 
 * @author yxl
 */
public class SecurityUtil {

	/**
	 * Description: 简单清理影响安全的字符
	 * 1、跨站脚本编制
	 * 2、SQL注入
	 * @param str 要清理的字符
	 * @author  杨雪令
	 */
	public static String simpleClear(String str) {	

		if(str == null) return null;
		//清理js
		//str = str.replaceAll("(?i)<\\s*script\\s*>", "&lt;script&gt;").replaceAll("(?i)<\\s*/\\s*script\\s*>", "&lt;/script&gt;").replaceAll("<\\s*!\\s*", "&lt;!");
		str = str.replaceAll("<", "&lt").replaceAll(">", "&gt;");
		//清理SQL注入
		str = str.replaceAll("'", "&apos;");
		str = str.replaceAll("\\$", "&#x24;");
		return str;
	}
	
	/**
	 * 重置字符串
	 * @param str 要重置的字符
	 * @author  杨雪令
	 */
	public static String reset(String str) {	

		if(str == null) return null;
		//清理js
		//str = str.replaceAll("(?i)<\\s*script\\s*>", "&lt;script&gt;").replaceAll("(?i)<\\s*/\\s*script\\s*>", "&lt;/script&gt;").replaceAll("<\\s*!\\s*", "&lt;!");
		str = str.replaceAll("&lt", "<").replaceAll("&gt;", ">");
		//清理SQL注入
		str = str.replaceAll("&apos;", "'");
		str = str.replaceAll("&#x24;", "\\$");
		return str;
	}
	
	/**
	 * 测试
	 * @date 2015年12月18日 上午10:57:15 
	 * @author yxl
	 */
	public static void main(String[] args) {
		System.out.println(SecurityUtil.simpleClear("ljl$2d'd<dd"));
	}
}