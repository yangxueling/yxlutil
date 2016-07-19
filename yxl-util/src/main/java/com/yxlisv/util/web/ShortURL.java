package com.yxlisv.util.web;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.net.socket.HttpConnection;
import com.yxlisv.util.string.JsonUtil;

/**
 * 短网址
 * @author whocare    
 * @version 1.0  
 * @created 2015-7-28 下午2:21:43
 */
public class ShortURL {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected static Logger logger = LoggerFactory.getLogger(ShortURL.class);

	/**
	 * 从dwz.cn获取
	 * @param url 要生成的网址
	 */
	@SuppressWarnings("rawtypes")
	public static String getDwz(String url){
		String shortUrl = url;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("url", url);
		try {
			String responseStr = HttpConnection.sendPost("http://dwz.cn/create.php", paramMap);
			Map resultMap = JsonUtil.jsonToMap(responseStr);
			if(resultMap.get("tinyurl")==null || !resultMap.get("status").toString().equals("0")){
				logger.error("从dwz.cn生成短网址失败:" + responseStr);
			} else {
				shortUrl = resultMap.get("tinyurl").toString();
			}
		} catch (Exception e) {
			logger.error("从dwz.cn生成短网址失败:" + e.getMessage());
			e.printStackTrace();
		}
		
		return shortUrl;
	}
	
	public static void main(String[] args) {
		String shortUrl = getDwz("http://help.baidu.com/question?prod_en=webmaster&class=%CD%F8%D2%B3%CB%D1%CB%F7%CC%D8%C9%AB%B9%A6%C4%DC&id=1000913#05");
		System.out.println(shortUrl);
	}
}