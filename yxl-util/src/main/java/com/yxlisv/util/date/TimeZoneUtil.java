package com.yxlisv.util.date;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.yxlisv.util.i18n.I18nUtil;

/**
 * 时区工具类
 * @createTime 2015年11月18日 下午3:25:46 
 * @author yxl
 */
public class TimeZoneUtil {

	/** 不同地区的GMT时区 */
	public static Map<String, String> localTimeZone = new HashMap<String, String>();
	static {
		localTimeZone.put("CN", "GMT+8");//中国
		localTimeZone.put("HK", "GMT+8");//香港
		localTimeZone.put("US", "GMT-10");//美国
		localTimeZone.put("JP", "GMT+8");//日本
	}

	/**
	 * 根据地区获取时区
	 * @param locale 地区
	 * @date 2015年11月18日 下午3:28:27 
	 * @author yxl
	 */
	public static TimeZone get(Locale locale) {
		if(locale==null) return null;
		String gmtId = "CN";
		if (localTimeZone.containsKey(locale.getCountry())) gmtId = localTimeZone.get(locale.getCountry());
		return TimeZone.getTimeZone(gmtId);
	}


	/**
	 * 根据地区获取时区
	 * @param locale 地区
	 * @date 2015年11月18日 下午3:28:27 
	 * @author yxl
	 */
	public static TimeZone get(String locale) {
		String gmtId = "CN";
		if (localTimeZone.containsKey(locale)) gmtId = localTimeZone.get(locale);
		return TimeZone.getTimeZone(gmtId);
	}
	
	/**
	 * 从当前线程读取时区（尝试从httpSession中取myLocale，如果没有使用request中的locale）
	 * @date 2015年11月26日 上午11:14:49 
	 * @author yxl
	 */
	public static TimeZone get() {
		return TimeZoneUtil.get(I18nUtil.getLocale());
	}
}