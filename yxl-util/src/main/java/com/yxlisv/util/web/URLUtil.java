package com.yxlisv.util.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.yxlisv.util.string.StringUtil;

/**
 * URL 工具类
 * @author yxl
 */
public class URLUtil {
	
	/**
	 * 给url拼装条件
	 * @param url ur
	 * @param paramMap 查询条件
	 * @autor yxl
	 */
	public static String getUrl(String url, Map<String, Object> paramMap) {

		for (Entry<String, Object> entry : paramMap.entrySet()) {
			if (entry.getValue() != null) {// 忽略空值
				String value = entry.getValue().toString();
				if (!value.equals("")) {// 忽略空值
					String key = entry.getKey();
					if (url.contains("?"))
						url += "&" + key + "=" + value;
					else
						url += "?" + key + "=" + value;
				}
			}
		}

		return url;
	}
	
	/**
	 * 给url拼装条件
	 * @param url ur
	 * @param paramMap 查询条件
	 * @param charSet 字符编码
	 * @return 条件
	 * @throws UnsupportedEncodingException 
	 * @autor yxl
	 */
	public static String getArgs(String url, Map<String, String> paramMap, String charSet) throws UnsupportedEncodingException {

		String args = "";
		for (Entry<String, String> entry : paramMap.entrySet()) {
			if (entry.getValue() != null) {// 忽略空值
				String value = entry.getValue().toString();
				if (!value.equals("")) {// 忽略空值
					String key = entry.getKey();
					value = URLEncoder.encode(value, charSet);
					if (url.contains("?")){
						url += "&" + key + "=" + value;
						args += "&" + key + "=" + value;
					}
					else{
						url += "?" + key + "=" + value;
						args += key + "=" + value;
					}
				}
			}
		}

		return args;
	}
	
	/**
	 * Description:替换路径里面的'\' 为'/'
	 * 
	 * @param path
	 *            要替换的路径
	 * @return 替换过的路径 Copyright　深圳太极软件公司
	 * @author 杨雪令
	 */
	public static String getStandardUrl(String path) {

		return StringUtil.forceReplace(path, "\\", "/");
	}
	
	public static void main(String[] args) {
		String url = "http://www.google.com?t=2";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", "测试");
		paramMap.put("pageSize", "100");
		
		System.out.println(getUrl(url,paramMap));
	}
}