package com.yxlisv.util.date;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.yxlisv.util.i18n.I18nUtil;

/**
 * 时间国际化工具
 * @createTime 2015年12月4日 下午3:21:59 
 * @author yxl
 */
public class DateI18nUtil {
	/** key 毫秒 */
	public static String KEY_MILLISECOND = "MILLISECOND";
	/** key 秒 */
	public static String KEY_SECONDS = "SECONDS";
	/** key 分 */
	public static String KEY_MINUTES = "MINUTES";
	/** key 小时 */
	public static String KEY_HOUR = "HOUR";
	/** key 天 */
	public static String KEY_DAY = "DAY";
	/** key ago */
	public static String KEY_AGO = "AGO";
	
	/** 国际化map */
	private static Map<String, String> i18nMap = new HashMap<String, String>();
	static {
		i18nMap.put(KEY_MILLISECOND, "毫秒");
		i18nMap.put(KEY_MILLISECOND + "_US", "ms");
		
		i18nMap.put(KEY_SECONDS, "秒");
		i18nMap.put(KEY_SECONDS + "_US", "s");
		
		i18nMap.put(KEY_MINUTES, "分钟");
		i18nMap.put(KEY_MINUTES + "_HK", "分鐘");
		i18nMap.put(KEY_MINUTES + "_TW", "分鐘");
		i18nMap.put(KEY_MINUTES + "_US", "minutes");
		
		i18nMap.put(KEY_HOUR, "小时");
		i18nMap.put(KEY_HOUR + "_HK", "小時");
		i18nMap.put(KEY_HOUR + "_TW", "小時");
		i18nMap.put(KEY_HOUR + "_US", "hour");
		
		i18nMap.put(KEY_DAY, "天");
		i18nMap.put(KEY_DAY + "_US", " days");
		
		i18nMap.put(KEY_AGO, "前");
		i18nMap.put(KEY_AGO + "_US", " ago");
	}
	
	/**
	 * 根据key获取名称
	 * @date 2015年12月4日 下午4:10:43 
	 * @author yxl
	 */
	public static String getI18n(String key){
		if(i18nMap.get(key)==null) return "";
		String val = i18nMap.get(key);
		Locale locale = I18nUtil.getLocale();
		if(locale!=null) key = key + "_" + I18nUtil.getLocale().getCountry();
		if(i18nMap.get(key)!=null) val = i18nMap.get(key);
		return val;
	}
}
