package com.yxlisv.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * HTTP 连接器
 * 
 * @author whocare
 * @version 1.0
 * @created 2014-4-15 下午10:52:53
 */
public class HttpConnection {

	/**
	 * 发送请求
	 * @param url 请求地址
	 * @param type 请求类型(GET,POST)
	 * @param paramMap 请求参数
	 * @param charset 字符集
	 * @param connectTimeout 连接超时时间，单位秒
	 * @param readTimeout 等待对方返回超时时间，单位秒
	 * @throws IOException
	 */
	public static String send(String url, String type, Map<String, String> paramMap, String charset, int connectTimeout, int readTimeout) throws IOException {

		//设置请求参数
		if(paramMap != null){
			for(Map.Entry mEntry : paramMap.entrySet()){
				if(url.contains("?")) url += "&";
				else url += "?";
				url += mEntry.getKey().toString() + "=" + URLEncoder.encode(mEntry.getValue().toString(), charset);;
			}
		}
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		StringBuffer sbResponse = new StringBuffer();
		BufferedReader reader = null;
		try {
			connection.setRequestMethod(type);
			connection.setConnectTimeout(120*1000);//连接120秒超时
			connection.setReadTimeout(readTimeout*1000);//等待对方返回，60秒超时
		    //connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		    //connection.setRequestProperty("Content-Type", "text/html; charset=" + charset);
		    //connection.setRequestProperty("Accept-Language", "*");
			
			connection.connect();

			// 发送数据到服务器并使用Reader读取返回的数据
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
			
			String lineStr;
			while ((lineStr = reader.readLine()) != null) sbResponse.append(lineStr+"\n");
		} catch(SocketException e){
			if(e.getMessage().contains("recv failed")) return send(url, type, paramMap, charset, connectTimeout, readTimeout);
		} finally{
			// 断开连接
			if(reader!=null) reader.close();
			if(connection!=null) connection.disconnect();
		}
		return sbResponse.toString();
	}
	
	
	/**
	 * 发送请求
	 * @param url 请求地址
	 * @param type 请求类型(GET,POST)
	 * @param charset 字符集
	 * @throws IOException
	 */
	public static String send(String url, String type, String charset) throws IOException {
		return send(url, type, null, charset, 100, 60);
	}

	/**
	 * 测试
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String url = "http://www.ccmn.cn/html/news/ZX018/201404/5ecc569c45556240014564aa952814e7.html";
		url = "http://www.baidu.com";
		url = "http://www.163.com";
		url = "http://www.sina.com";
		System.out.println(send(url, "GET", null, "utf-8", 100, 60));
	}
}