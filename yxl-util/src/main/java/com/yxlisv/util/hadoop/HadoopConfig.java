package com.yxlisv.util.hadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.redis.JedisUtil;

/**
 * Hadoop配置
 * @createTime 2015年11月8日 上午8:25:53 
 * @author yxl
 */
public class HadoopConfig {
	
	private static Logger logger = LoggerFactory.getLogger(HadoopConfig.class);
	
	/** HDFS文件系统地址 */
	public static String hdfsAddr;
	
	/** Hadoop 配置文件 */
	private static Properties properties;

	// 静态方式加载hadoop 配置文件
	static {
		String propName = "hadoop.properties";
		properties = new Properties();
		InputStream is = null;
		BufferedReader bf = null;
		is = JedisUtil.class.getResourceAsStream("/" + propName);// 将地址加在到文件输入流中
		if(is==null) is = HadoopConfig.class.getResourceAsStream(propName);// 将地址加在到文件输入流中
		try {
			bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));// 转为字符流，设置编码为UTF-8防止出现乱码
			properties.load(bf);// properties对象加载文件输入流
		} catch (Exception e) {
			logger.error(propName + "属性文件读取失败");
		} finally {
			try {// 文件流关闭
				if (bf != null) bf.close();
				if (is != null) is.close();
			} catch (IOException e) {}
		}
		
		hdfsAddr = properties.getProperty("hadoop.hdfs.addr.host");//HDFS文件系统地址
	}
}