package com.yxl.util.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 属性文件工具类
 * @createTime 2015年11月19日 上午11:10:40 
 * @author yxl
 */
public class PropertiesUtil {

	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	/**
	 * 读取配置文件
	 * 先尝试从项目资源目录读取配置文件
	 * 如果资源目录没有读取到配置文件，从调用者位置读取配置文件
	 * @param path 文件路径
	 * @param srcClass 调用者的class
	 * @date 2015年11月19日 上午11:16:52 
	 * @author yxl
	 */
	@SuppressWarnings("rawtypes")
	public static Properties readProperties(String path, Class srcClass) {
		Properties properties = new Properties();
		InputStream is = null;
		BufferedReader bf = null;
		if (path.startsWith("/")) path = path.substring(1);
		is = srcClass.getResourceAsStream("/" + path);//先尝试从项目资源目录读取配置文件
		if (is == null) is = srcClass.getResourceAsStream(path);//从调用者位置读取配置文件
		try {
			bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));//转为字符流，设置编码为UTF-8防止出现乱码
			properties.load(bf);//properties对象加载文件输入流
			return properties;
		} catch (Exception e) {
			logger.error(path + "属性文件读取失败");
			return null;
		} finally {
			try {// 文件流关闭
				if (bf != null) bf.close();
				if (is != null) is.close();
			} catch (IOException e) {}
		}
	}
	
	//变量引用正则表达式
	private static Pattern varReferencePattern = Pattern.compile("\\$\\{\\s*([0-9a-zA-Z\\.-_]+)\\s*\\}");
	
	/**
	 * 从Properties配置文件中读取一个值
	 * 在Properties中可以使用${key}引用本文件中的变量
	 * @date 2016年1月28日 上午9:36:21 
	 * @author yxl
	 */
	public static String get(String key, Properties properties){
		if(key==null || key.trim().length()<1) return null;
		if(!properties.containsKey(key)) return null;
		String value = properties.get(key).toString();
		//处理变量引用
		Matcher varReferenceMatcher = varReferencePattern.matcher(value);
		while(varReferenceMatcher.find()){
			String varKey = varReferenceMatcher.group(1);
			String varVal = get(varKey, properties);
			if(varVal!=null) value = value.replace(varReferenceMatcher.group(0), varVal);
		}
		return value;
	}
}
