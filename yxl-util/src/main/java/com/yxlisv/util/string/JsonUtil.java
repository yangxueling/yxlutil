package com.yxlisv.util.string;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json工具类
 * @author yxl
 */
public class JsonUtil {

	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	/**
	 * 把简单的json字符串转换成map对象
	 * @param jsonStr json字符串
	 * @autor yxl
	 */
	@SuppressWarnings("rawtypes")
	public static Map jsonToMap(Object json){
		
		try {
			if(json!=null) return new ObjectMapper().readValue(json.toString(), Map.class);
		} catch (Exception e) {
			logger.error("jsonToMap error : " + json, e);
		}
		return null;
	}
	
	/**
	 * json数组转换成list对象，并把单个对象转换成map存储
	 * @param jsonArray json数组
	 * @autor yxl
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map> jsonArrayToList(String json){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, List.class);
		} catch (Exception e) {
			logger.error("jsonArrayToList error : " + json, e);
			return null;
		}
	}
	
	/**
	 * <p>对象转换为json字符串</p>
	 * @param obj 要转换的对象
	 * @return String json字符串
	 * @author 杨雪令
	 * @time 2016年4月15日下午4:51:19
	 * @version 1.0
	 */
	public static String toJson(Object obj){
		ObjectMapper mapper = new ObjectMapper();  
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("toJson error : " + obj, e);
			return null;
		}
	}
}
