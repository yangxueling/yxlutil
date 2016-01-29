package com.yxlisv.util.net.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.yxlisv.util.net.socket.ssl.SSLUtil;
import com.yxlisv.util.net.socket.ssl.TrustAnyHostnameVerifier;
import com.yxlisv.util.web.URLUtil;

/**
 * <p>Title:http 连接工具类
 * <p>Copyright: Copyright 2010 Shenzhen Taiji SoftwareCorparation
 * <p>Company: 深圳太极软件有限公司
 * <p>CreateTime: Feb 24, 2011
 * @author 杨雪令
 * @version 1.1
 */

public class HttpConnection {

	static String REGIP = "([0-9]{1,3})(.[0-9]{1,3}){3}";// IP 正则表达式

	/**
	 * 发送请求并接收返回数据 Socket 方式
	 * @param host 访问地址
	 * @param port 访问端口号 8080
	 * @param url 访问路径 /dzjc_portal/rest/dzjc/supervise/xzxk/attention/AttentionList
	 * @param type 访问类型 GET,POST,PUT,DELETE
	 * @throws Exception 
	 */
	public static String sendSocket(String host, int port, String url, String type, String charSet) throws Exception {
		StringBuffer context = new StringBuffer();

		BufferedWriter wr = null;// 发送命令
		InetAddress addr = InetAddress.getByName(host);
		Socket socket = new Socket(addr, port); // 建立一个socket

		// 发送命令
		wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charSet));

		// 设置连接类型 GET,POST,PUT,DELETE
		type = type.toUpperCase().trim();
		wr.write(type + " " + url + " HTTP/1.1\r\n");

		wr.write("Host:" + host + "\r\n");
		wr.write("Accept: */*\r\n");
		wr.write("Accept-Language: zh-cn\r\n");
		wr.write("Accept-Encoding: gzip,deflate\r\n");
		wr.write("User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.8)\r\n");
		wr.write("Connection: keep-alive\r\n\r\n");
		wr.write("\r\n");
		wr.flush();

		// 接收返回的结果
		InputStream in = socket.getInputStream();
		context = inputStreamToString(in, charSet);

		if (wr != null)
			wr.close();
		return context.toString();
	}

	/**
	 * 发送请求并接收返回数据 URLConnection 方式
	 * @param address 访问地址
	 * @throws Exception 
	 */
	public static String sendPost(String address, Map<String, String> paramMap) throws Exception {
		return send(address, paramMap, "POST", "UTF-8", null, null);
	}
	
	/**
	 * 发送请求并接收返回数据 URLConnection 方式
	 * @param address 访问地址
	 * @param fileParams 上传文件<参数名，文件><参数名，文件>
	 * @throws Exception 
	 */
	public static String sendPost(String address, Map<String, String> paramMap, Map<String, File> fileParams) throws Exception {
		return send(address, paramMap, "POST", "UTF-8", null, fileParams);
	}

	/**
	 * 发送请求并接收返回数据 URLConnection 方式
	 * @param address 访问地址
	 * @throws Exception 
	 */
	public static String sendGet(String address, Map<String, String> paramMap) throws Exception {
		return send(address, paramMap, "GET", "UTF-8", null, null);
	}

	/**
	 * 发送请求并接收返回数据 URLConnection 方式
	 * @param address 访问地址
	 * @param type 访问类型 GET,POST,PUT,DELETE
	 * @throws Exception 
	 */
	public static String send(String address, Map<String, String> paramMap, String type) throws Exception {
		return send(address, paramMap, type, "UTF-8", null, null);
	}

	/**
	 * 发送请求并接收返回数据 URLConnection 方式
	 * @param address 访问地址
	 * @param type 访问类型 GET,POST,PUT,DELETE
	 * @param charSet 字符编码
	 * @throws Exception 
	 */
	public static String send(String address, Map<String, String> paramMap, String type, String charSet) throws Exception {
		return send(address, paramMap, type, charSet, null, null);
	}

	/**
	 * 发送请求并接收返回数据 URLConnection 方式
	 * @param address 访问地址
	 * @param type 访问类型 GET,POST,PUT,DELETE
	 * @param charSet 字符编码
	 * @param proxy 代理
	 * @param fileParams 上传文件<参数名，文件>
	 * @throws Exception 
	 * 
	 */
	public static String send(String address, Map<String, String> paramMap, String type, String charSet, Proxy proxy, Map<String, File> fileParams) throws Exception{
		StringBuffer context = new StringBuffer();

		String BOUNDARY = "-----------7d4a6d158c9"; // 分隔符 
		HttpURLConnection connection = null;
		DataOutputStream out = null;
		
		try{
			// 地址
			URL url = new URL(address);
			if (proxy == null)
				connection = (HttpURLConnection) url.openConnection();
			else
				connection = (HttpURLConnection) url.openConnection(proxy);

			// 如果是https请求
			if ("https".equals(url.getProtocol())){
				((HttpsURLConnection)connection).setSSLSocketFactory(SSLUtil.getSSLSocketFactory());
				((HttpsURLConnection)connection).setHostnameVerifier(new TrustAnyHostnameVerifier());
			}

			// 设置类型 GET,POST,PUT,DELETE
			type = type.toUpperCase().trim();
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);
			connection.setAllowUserInteraction(true);
			connection.setConnectTimeout(10000000);
			connection.setReadTimeout(10000000);
			connection.setRequestMethod(type);
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
			connection.setRequestProperty("Charsert", charSet);
			if(fileParams == null) connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charSet);
			if(fileParams != null) connection.setRequestProperty("Content-type", "multipart/form-data; charset=" + charSet + "; boundary=" + BOUNDARY);
			
			// 组装文件请求参数
			out = new DataOutputStream(connection.getOutputStream());
			//发送参数，url长度有限制，所以不能全都拼接到url之上
			if(paramMap != null){
				//组装形式受 Content-type 影响
				if(fileParams == null){
					String args = URLUtil.getArgs(address, paramMap, charSet);
					out.write(args.getBytes(charSet));
				} else {
					for(Entry<String, String> paramEntry : paramMap.entrySet()){
						String name = paramEntry.getKey();//参数
						String value = paramEntry.getValue();//值
						//multipart/form-data 方式不需要对URL编码
						out.write(HttpRequestAssembly.getHttpPostArgsHead(name, value, BOUNDARY).getBytes(charSet));
					}
				}
			}
			
			//上传文件
			if(fileParams != null){
				// 组装文件请求参数
				Set<Entry<String, File>> fileEntrySet = fileParams.entrySet();
				for (Entry<String, File> fileEntry : fileEntrySet) {
					String fileName = fileEntry.getKey();
					File file = fileEntry.getValue();
					out.write(HttpRequestAssembly.getHttpPostFileHead(fileName, file.getPath(), BOUNDARY).getBytes(charSet));
					
					//写文件内容
					DataInputStream in = null;
					try{
						in = new DataInputStream(new FileInputStream(file));
						int bytes = 0;
						byte[] bufferOut = new byte[1024];
						while ((bytes = in.read(bufferOut)) != -1) {
							out.write(bufferOut, 0, bytes);
						}
					} finally{
						if(in != null) in.close();
					}
				}
				// 添加请求结束标志
				out.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());
			}
			
			//如果有错误，读取错误消息
			InputStream in = null;
			if(connection.getResponseCode() != 200) in = connection.getErrorStream();
			else in = connection.getInputStream();
				
			context = inputStreamToString(in, charSet);
			
		} finally{
			if(out != null) {
				out.flush();
				out.close();
			}
			//释放连接
			if(connection != null) connection.disconnect();
			connection = null;
		}
		
		return context.toString();
	}
	
	
	/**
	 * <p>Description: 设置代理
	 * <p>Copyright　深圳太极软件公司
	 * @param proxyIP 代理IP
	 * @param proxyPort 代理端口
	 * @return 设置成功返回true
	 * @author  杨雪令
	 */
	public static boolean setProxyForSys(String proxyIP, int proxyPort) {
		// 验证数据正确性
		if (findByReg(REGIP, proxyIP) != null) {
			proxyIP = findByReg(REGIP, proxyIP);
		} else {
			return false;
		}
		if (proxyPort > 60000 || proxyPort < 1) {
			return false;
		}

		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", proxyIP);
		System.getProperties().setProperty("http.proxyPort", proxyPort + "");
		return true;
	}

	/**
	 * <p>Description: 设置代理
	 * <p>Copyright　深圳太极软件公司
	 * @param proxyIP 代理IP
	 * @param proxyPort 代理端口
	 * @return 设置成功返回true
	 * @author  杨雪令
	 */
	public static Proxy createProxy(String proxyIP, int proxyPort) {
		// 验证数据正确性
		if (findByReg(REGIP, proxyIP) != null) {
			proxyIP = findByReg(REGIP, proxyIP);
		} else {
			return null;
		}
		if (proxyPort > 60000 || proxyPort < 1) {
			return null;
		}

		SocketAddress proxyAddress = new InetSocketAddress(proxyIP, proxyPort);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);

		return proxy;
	}

	/**
	 * <p>Description: 根据正则表达式从字符串中获取数据，否则返回 null
	 * <p>Copyright　深圳太极软件公司
	 * @author  杨雪令
	 */
	private static String findByReg(String reg, String str) {

		Pattern pattern = Pattern.compile(reg);
		Matcher matcher;
		matcher = pattern.matcher(str);
		if (matcher.find()) {
			return matcher.group(0);
		} else {
			return null;
		}
	}

	/**
	 * <p>Copyright　深圳太极软件公司
	 * @param inputStream
	 * @param charSet 字符编码
	 * @throws Exception 
	 * @author  杨雪令
	 * @throws IOException 
	 */
	private static StringBuffer inputStreamToString(InputStream inputStream, String charSet) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, charSet));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
			buffer.append("\r\n");
		}
		in.close();
		return buffer;
	}

	/**
	 * <p>Description:测试
	 * <p>Copyright　深圳太极软件公司
	 * @param args 
	 * @author  杨雪令
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*HttpConnection wu = new HttpConnection();
		String result = "";
		//String address = "https://ebanks.gdb.com.cn/sperbank/perbankLogin.jsp";
		String address = "https://gw.open.1688.com/openapi/http/1/system.oauth2/getToken/1007047?grant_type=authorization_code&need_refresh_token=true&client_id=1007047&client_secret=y8y~8VQYaI&redirect_uri=http://192.168.1.33:333/desRm&code=e1625d42-205f-4dd4-b1fc-fb19ce533485";
		//String address = "http://baidu.com";
		int port = 8080;

		result = wu.sendPost(address);
		System.out.println(result);*/
		
		String URI = "http://192.168.1.33:333/desRm/ecApi/authorize/tb";
		Map paramMap = new HashMap();
		paramMap.put("error", "错误");
		paramMap.put("code", "lkjljljlj");
		paramMap.put("error_description", "斯蒂芬斯蒂芬");
		System.out.println(HttpConnection.sendPost(URI, paramMap));
	}
}
