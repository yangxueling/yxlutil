package com.yxl.util.net.socket.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * SSL工具类
 * @author yxl
 */
public class SSLUtil {

	/**
	 * SSLSocketFactory类用于操作SSL套接字，需要通过SSLContext类的getSockeFactory方法获取，SSLContext在初始化的时候需要配置对应的密匙库与信任库。
	 * @return SSLSocketFactory
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @throws Exception 
	 * @autor yxl
	 * 2013-8-1
	 */
	public static SSLSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(new KeyManager[0], new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

		return sc.getSocketFactory();
	}
}