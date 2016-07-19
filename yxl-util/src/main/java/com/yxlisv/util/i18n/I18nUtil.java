package com.yxlisv.util.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * I18N 工具类(读取I18N的消息内容)
 * @author john Local
 */
public class I18nUtil {
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected static Logger logger = LoggerFactory.getLogger(I18nUtil.class);

	/** 用户localekey，设置到session中使用 */
	public static String userLocaleKey = "myLocale";

	/**
	 * 获取消息
	 * @param key 名称
	 * @param fileName 文件名称
	 * @param locale 地区
	 * @autor yxl
	 */
	public static String getString(String key, String fileName, Locale locale) {
		String msgStr = "";
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(fileName, locale);
			msgStr = bundle.getString(key);
		} catch (Exception e) {
			logger.debug("I18N not found: " + fileName + "[" + key + "]" + locale.getLanguage());
		}
		return msgStr;
	}

	/**
	 * 获取消息（尝试从httpSession中取myLocale，如果没有使用request中的locale）
	 * @param key 名称
	 * @param fileName 文件名称
	 * @autor yxl
	 */
	public static String getString(String key, String fileName) {
		Locale locale = null;
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			locale = request.getLocale();
			if(request.getSession().getAttribute(I18nUtil.userLocaleKey)!=null) locale = (Locale) request.getSession().getAttribute(I18nUtil.userLocaleKey);
		} catch (Exception e) {
			return null;
		}
		return getString(key, fileName, locale);
	}
	
	/**
	 * 获取用户locale
	 * @date 2015年12月4日 下午3:25:28 
	 * @author yxl
	 */
	public static Locale getLocale(){
		Locale locale = null;
		try{
			HttpServletRequest request =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			locale = request.getLocale();
			if(request.getSession().getAttribute(userLocaleKey)!=null) locale = (Locale) request.getSession().getAttribute(userLocaleKey);
		} catch(Exception e) {
			return null;
		}
		return locale;
	}
}