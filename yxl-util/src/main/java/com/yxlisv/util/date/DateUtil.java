package com.yxlisv.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.yxlisv.util.i18n.I18nUtil;

/**
 * Title:日期处理类
 * CreateTime: Aug 27, 2010
 * @author 杨雪令
 * @version 1.0
 */

public class DateUtil {

	/** 一天时间的毫秒数 */
	public static long ONE_DAY = 3600 * 1000 * 24;

	/**
	 * 实例化SimpleDateFormat
	 * @pattern 格式化参数
	 * @date 2015年11月26日 下午12:15:40 
	 * @author yxl
	 */
	public static SimpleDateFormat newInstanceFormat(String pattern) {
		//自动识别英文格式时间
		if(pattern.contains("yyyy-MM-dd")){
			Locale locale = I18nUtil.getLocale();
			if(locale!=null && locale.toString().contains(("en"))) {
				pattern = pattern.replaceAll("yyyy-MM-dd", "MM-dd-yyyy");
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		//从当前线程读取时区
		TimeZone zone = TimeZoneUtil.get();
		if (zone != null) sdf.setTimeZone(zone);
		return sdf;
	}

	/**
	 * Description:把日期转换成 long
	 * @param time 传入的日期 格式：yyyy-MM-dd HH:mm:ss
	 * @return  long 类型的日期
	 * Copyright　深圳太极软件公司
	 * @author  杨雪令
	 */
	public static long toLong(Object time) {
		if (time == null) return 0;
		Date date = null;
		SimpleDateFormat sdf = newInstanceFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = newInstanceFormat("yyyy-MM-dd");
		SimpleDateFormat sdf3 = newInstanceFormat("MM/dd/yyyy");
		SimpleDateFormat sdf4 = newInstanceFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat sdf5 = newInstanceFormat("yyyyMMddHHmmssSSSS");
		try {
			date = sdf.parse(time.toString());
		} catch (ParseException e) {
			try {
				date = sdf2.parse(time.toString());
			} catch (ParseException e2) {
				try {
					date = sdf3.parse(time.toString());
				} catch (ParseException e3) {
					try {
						date = sdf4.parse(time.toString());
					} catch (ParseException e4) {
						try {
							date = sdf5.parse(time.toString());
						} catch (ParseException e5) {
							return 0;
						}
					}
				}
			}
		}
		if (date == null) return 0;
		return date.getTime();
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toDate(long time) {
		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toTime(long time) {
		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toYear(long time) {
		SimpleDateFormat df = newInstanceFormat("yyyy");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toMonth(long time) {
		SimpleDateFormat df = newInstanceFormat("MM");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toDay(long time) {
		SimpleDateFormat df = newInstanceFormat("dd");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toHour(long time) {
		SimpleDateFormat df = newInstanceFormat("HH");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toMinute(long time) {
		SimpleDateFormat df = newInstanceFormat("mm");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toSecond(long time) {
		SimpleDateFormat df = newInstanceFormat("ss");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toHourMinute(long time) {
		SimpleDateFormat df = newInstanceFormat("HH:mm");
		return df.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toHourMinuteSecond(long time) {
		SimpleDateFormat df = newInstanceFormat("HH:mm:ss");
		return df.format(new Date(time));
	}

	/**
	 * 传入一个日期字符串，返回日期带有星期的标识  如：传入2009-11-16 返回 2009年11月16日 星期一
	 * @param date 時間：yyyy-MM-dd
	 */
	public static String toWeek(String date) {

		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		String day = date.substring(8, 10);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = newInstanceFormat("yyyy年MM月dd日 EEE");

		c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

		return df.format(c.getTime());
	}
	
	
	/**
	 * 智能处理
	 * @date 2015年12月4日 下午2:58:14 
	 * @author yxl
	 */
	public static String toAi(long time) {
		String returnStr = "";
		long currentTime = System.currentTimeMillis();
		if(currentTime-time < 60*1000) {//小于一分钟
			returnStr = (currentTime-time)/1000 + DateI18nUtil.getI18n(DateI18nUtil.KEY_SECONDS) + DateI18nUtil.getI18n(DateI18nUtil.KEY_AGO);
		} else if(currentTime-time < 60*60*1000){//小于一小时
			returnStr = (currentTime-time)/1000/60 + DateI18nUtil.getI18n(DateI18nUtil.KEY_MINUTES) + DateI18nUtil.getI18n(DateI18nUtil.KEY_AGO);
		} else if(currentTime-time < 24*60*60*1000){//小于一天
			returnStr = (currentTime-time)/60/60/1000 + DateI18nUtil.getI18n(DateI18nUtil.KEY_HOUR) + DateI18nUtil.getI18n(DateI18nUtil.KEY_AGO);
		} else if(currentTime-time < 10*24*60*60*1000){//小于十天
			returnStr = (currentTime-time)/24/60/60/1000 + DateI18nUtil.getI18n(DateI18nUtil.KEY_DAY) + DateI18nUtil.getI18n(DateI18nUtil.KEY_AGO);
		} else {
			returnStr = toDate(time);
		}
		return returnStr;
	}
	
	/**
	 * 智能处理
	 * @date 2015年12月4日 下午2:58:14 
	 * @author yxl
	 */
	public static String toAi1(long time) {
		String returnStr = "";
		long currentTime = System.currentTimeMillis();
		if(currentTime-time < 10*24*60*60*1000){//小于十天
			returnStr = toAi(time);
		} else {
			returnStr = toTime(time);
		}
		return returnStr;
	}
	
	/**
	 * 传入一个日期字符串，返回星期几
	 * @param date 時間：yyyy-MM-dd
	 */
	public static String getWeek(String date) {

		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		String day = date.substring(8, 10);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = newInstanceFormat("EEE");
		c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
		return df.format(c.getTime());
	}

	/**
	 * 格式化时间
	 * @param time 时间
	 */
	public static String toSimpleNo(long time) {
		SimpleDateFormat df = newInstanceFormat("yyyyMMddHHmmss");
		return df.format(new Date(time));
	}

	/**
	 * 得到今天的时间
	 */
	public static String getToday() {
		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd");
		return df.format(new Date());
	}

	/**
	 * 得到當前系統时间
	 */
	public static String getTime() {
		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

	/**
	 * 根据一个日期，得到星期几的日期
	 * @param date 时间(yyyy-MM-dd)
	 * @param ofDay 星期几
	 */
	public static String getDateOfWeekDay(String date, int ofDay) {
		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		String day = date.substring(8, 10);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd");

		c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
		//如果要得到星期天的时间，星期天在国外是一个星期的第一天
		if (ofDay >= 7) ofDay = 0;
		c.set(c.DAY_OF_WEEK, ofDay + 1);

		return df.format(c.getTime());
	}

	/**
	 * 得到星期几的日期
	 * @param ofDay 星期几
	 */
	public static String getDateOfWeekDay(int ofDay) {
		String week = null;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd");
		week = df.format(c.getTime());
		week = getDateOfWeekDay(week, ofDay);
		return week;
	}

	/**
	 * 检查日期格式是否为xxxx-xx-xx
	 * @param date 時間
	 */
	public static boolean checkFormat(String date) {

		boolean status = true;
		Calendar c = Calendar.getInstance();
		try {
			String year = date.substring(0, 4);
			String month = date.substring(5, 7);
			String day = date.substring(8, 10);
			c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	/**
	 * 得到一个月以后的日期
	 * @param date 時間
	 */
	public static String getDateOfNextMonth(String date) {
		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		String day = date.substring(8, 10);
		Calendar c = Calendar.getInstance();

		c.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd");
		return df.format(c.getTime());
	}

	/**
	 * 得到一个月以后的日期
	 * @param date 時間
	 * @param ofDay 第幾天
	 */
	public static String getDateInMonth(String date, int ofDay) {

		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, ofDay);
		SimpleDateFormat df = newInstanceFormat("yyyy-MM-dd");
		return df.format(c.getTime());
	}

	/**
	 * 得到一个月以后的日期
	 * @param date 時間
	 * @param ofDay 第幾天
	 */
	public static String getDateInMonth(long date, int ofDay) {
		return DateUtil.getDateInMonth(DateUtil.toDate(date), ofDay);
	}
	
	/**
	 * 根据开始时间和结束时间，获取每一天的集合
	 * @date 2015年11月30日 下午5:20:52 
	 * @author yxl
	 */
	public static List<String> getDayList(String startDate, String endDate){
		List<String> dayList = new ArrayList<String>();
		long startDateLong = toLong(toDate(toLong(startDate)));
		long endDateLong = toLong(toDate(toLong(endDate)));
		int days = (int) ((endDateLong - startDateLong) /ONE_DAY) + 1;
		for(int i=0; i<days; i++){
			dayList.add(toDate(startDateLong+ONE_DAY*i));
		}
		return dayList;
	}

	/**
	 * 获取Select需要的数据
	 * @param yearCount 生成年份的数量
	 */
	public static Map getSelectData(int yearCount) {

		Map map = new HashMap();
		//年份数据
		List yearList = new ArrayList();
		int currentYear = Integer.parseInt(DateUtil.toYear(System.currentTimeMillis()));
		int startYear = currentYear - yearCount + 5;
		int endYear = currentYear + yearCount + 5;
		if (startYear > currentYear) startYear = currentYear;
		for (int i = startYear; i < endYear; i++) {
			if (i < 10) yearList.add("0" + i);
			else yearList.add(i);
		}

		//月份数据
		List monthList = new ArrayList();
		for (int i = 1; i <= 12; i++) {
			if (i < 10) monthList.add("0" + i);
			else monthList.add(i);
		}

		//日数据
		List dayList = new ArrayList();
		for (int i = 1; i <= 31; i++) {
			if (i < 10) dayList.add("0" + i);
			else dayList.add(i);
		}

		//小时数据
		List hourList = new ArrayList();
		for (int i = 0; i < 24; i++) {
			if (i < 10) hourList.add("0" + i);
			else hourList.add(i);
		}

		//分数据
		List minuteList = new ArrayList();
		for (int i = 0; i < 60; i++) {
			if (i < 10) minuteList.add("0" + i);
			else minuteList.add(i);
		}

		//秒数据
		List secondList = new ArrayList();
		for (int i = 0; i < 60; i++) {
			if (i < 10) secondList.add("0" + i);
			else secondList.add(i);
		}

		map.put("yearList", yearList);
		map.put("monthList", monthList);
		map.put("dayList", dayList);
		map.put("hourList", hourList);
		map.put("minuteList", minuteList);
		map.put("secondList", secondList);
		return map;
	}

	/**
	 * 測試
	 * @autor yxl
	 */
	public static void main(String[] args) {
		/*String date = "2013-08-24";
		date = "20150627121415000 0800";
		long tl = DateUtil.toLong(date);
		System.out.println(tl);
		System.out.println(DateUtil.toTime(tl));*/
		
		//List dayList = DateUtil.getDayList("2013-4-6 14:20:33", "2013-4-8");
		//System.out.println(dayList);
		System.out.println(toTime(1450332692048l));
		/*System.out.println(toAi(toLong("2015-11-4 15:13")));
		System.out.println(toAi(toLong("2015-12-1 15:13")));
		System.out.println(toAi(toLong("2015-12-3 15:13")));
		System.out.println(toAi(toLong("2015-12-4 11:13")));
		System.out.println(toAi(toLong("2015-12-4 15:13")));
		System.out.println(toAi(System.currentTimeMillis()-1000*800));
		System.out.println(toAi(System.currentTimeMillis()-1000*80));
		System.out.println(toAi(System.currentTimeMillis()-1000*5));
		System.out.println(toAi(System.currentTimeMillis()));*/
	}
}
