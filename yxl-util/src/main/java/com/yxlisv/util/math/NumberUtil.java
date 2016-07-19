package com.yxlisv.util.math;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * 描述： 对数据进行处理的工具类
 * 
 * @author: 杨雪令
 * @version 1.0
 */
public class NumberUtil {
	
	
	/**
	 * 判断字符串是不是整型
	 * @autor yxl
	 */
	public static boolean isInt(Object str) {
		if(str==null) return false;
		if (str.toString().trim().equals("")) return false;
		try {
			Integer.parseInt(str.toString());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 字符串转整型
	 * @autor yxl
	 */
	public static int parseInt(Object str) {
		if(str==null) return 0;
		if (str.toString().trim().equals("")) return 0;
		if (isInt(str)) return Integer.parseInt(str.toString().trim());
		return 0;
	}
	
	/**
	 * 字符串转long
	 * @autor yxl
	 */
	public static long parseLong(Object str) {
		if(str==null) return 0;
		if (str.toString().trim().equals("")) return 0;
		try{
			return Long.parseLong(str.toString().trim());
		} catch(Exception e){
			return 0;
		}
	}
	
	/**
	 * 字符串转float
	 * @autor yxl
	 */
	public static double parseFloat(Object str) {
		if(str==null) return 0;
		if (str.toString().trim().equals("")) return 0;
		try{
			return Float.parseFloat(str.toString().trim());
		} catch(Exception e){
			return 0;
		}
	}
	
	/**
	 * 字符串转double
	 * @autor yxl
	 */
	public static double parseDouble(Object str) {
		if(str==null) return 0;
		if (str.toString().trim().equals("")) return 0;
		try{
			return Double.parseDouble(str.toString().trim());
		} catch(Exception e){
			return 0;
		}
	}

	/**
	 * 描述：四舍五入   
	 * @author: 杨雪令
	 * @param num 要处理的数据
	 * @return 四舍五入处理好的字符串
	 * @version: 2010-4-3 下午07:20:27
	 */
	public static int sswr(double num) {
		return (int)Math.round(num);
	}
	
	/**
	 * 获取一个随机数
	 * @param min 最小值
	 * @param max 最大值
	 * @autor yxl
	 */
	public static int getRandom(int min, int max){
		if(min>=max) return max;
		Random rd = new Random();
		int val = rd.nextInt(max+1-min);
		val += min;
		//System.out.println(val);
		return val;
	}
	
	/**
	 * 获取一个随机小数
	 * @param min 最小值
	 * @param max 最大值
	 * @autor yxl
	 */
	public static double getRandom(double min, double max){
		
		Random rd = new Random();
		//double val = rd.nextDouble(max-min);
		//val += min;
		int iVal = NumberUtil.getRandom((int)Math.floor(min), (int)Math.floor(max));
		//System.out.println(iVal);
		double dVal = min;
		if(iVal > min) dVal = iVal;
		
		boolean findDouble = false;
		while(!findDouble){
			double rdd = rd.nextDouble();
			//System.out.print(".");
			//出現越界
			if(dVal>=max) rdd = -rdd;
			if(dVal + rdd <= max){
				//System.out.println();
				dVal += rdd;
				break;
			}
		}
		
		return dVal;
	}
	
	
	/**
	 * 保留2位小数
	 * @autor yxl
	 */
	public static String getFomat2(double num) {
        return NumberUtil.getFomat(num, "#.##");
	}
	
	/**
	 * 保留3位小数
	 * @autor yxl
	 */
	public static String getFomat3(double num) {
		return NumberUtil.getFomat(num, "#.###");
	}
	
	/**
	 * 格式化小数
	 * @autor yxl
	 */
	public static String getFomat(double num, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
        return df.format(num);
	}
	
	
	/**
	 * 获取一个随机字符串
	 * @autor yxl
	 */
	public static String getRandomStr(){
		
		String rStr = "";
		//第一部分，int型随机数
		Random rd = new Random();
		rStr = "" + rd.nextInt(99999) + rd.nextInt(9999);
		rStr = rStr.replaceAll("-", "");
		
		//第二部分，当前系统时间的一部分
		String rStr2 = System.currentTimeMillis() + "";
		rStr2 = rStr2.substring(6,13);
		
		return rStr + rStr2;
	}
	
	/**
	 * 测试
	 * @autor yxl
	 */
	public static void main(String[] args) {
	//	NumberUtil nu = new NumberUtil();
		//System.out.println(nu.getRandom(-1d, 1));
		System.out.println(getRandomStr());
	}
}