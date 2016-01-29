package com.yxlisv.util.string;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	@SuppressWarnings("unchecked")
	public static List<Map> jsonArrayToList(String json){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, List.class);
		} catch (Exception e) {
			logger.error("jsonArrayToList error : " + json, e);
			return null;
		}
	}
}
