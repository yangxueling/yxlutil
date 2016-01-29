package com.yxl.util.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * java web session 缓存
 * @author yxl
 */
public class HttpSessionCache {
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected static Logger logger = LoggerFactory.getLogger(HttpSessionCache.class);
	
	/** session 缓存 */
	private static Map<String, HttpSession> sessionMap = new HashMap();
	
	/** 是否开启了清理session缓存的线程 */
	public static boolean cleanThread = false;
	
	/**
	 * 添加session
	 * @autor yxl
	 */
	public static void addSession(HttpSession session){
		startCleanSession(100);
		sessionMap.put(session.getId().toLowerCase(), session);
	}
	
	/**
	 * 读取 session
	 * @autor yxl
	 */
	public static HttpSession getSession(String key){
		if(key==null) return null;
		key = key.toLowerCase();
		HttpSession session = sessionMap.get(key);
		if(session!=null && isInvalidate(session)){
			removeSession(key);
			logger.info("session is invalidate, remove from cache：" + key);
			return null;
		}
		return session;
	}
	
	/**
	 * 移除 session
	 * @autor yxl
	 */
	public static void removeSession(String key){
		sessionMap.remove(key.toLowerCase());
	}
	
	/**
	 * session是否过期
	 * @autor yxl
	 */
	public static boolean isInvalidate(HttpSession session){
		//如果session过期，会报错
		try{
			session.getAttribute("test_session_is_invalidate");
		} catch(Exception e){
			return true;
		}
		return false;
	}
	
	/**
	 * 开启清理session线程，每隔一段时间检查所有session，如果有过期，则清理
	 * @param interval 间隔时间，单位分钟
	 * @autor yxl
	 * 2013-11-27
	 */
	public static void startCleanSession(final int interval){
		if(cleanThread == true) return;
		logger.info("starting CleanSessionCacheThread, interval " + interval + " minute...");
		cleanThread = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(cleanThread){
					//线程等待时间
					try {
						Thread.sleep(interval * 60000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					logger.info("CleanSessionCache...");
					Map tempMap = new HashMap();
					tempMap.putAll(sessionMap);
					//检测session
					for(Iterator it=tempMap.entrySet().iterator(); it.hasNext();){
						Entry entry = (Entry) it.next();
						String key = entry.getKey().toString();
						HttpSession session = (HttpSession) entry.getValue();
						if(isInvalidate(session)){
							removeSession(key);
							logger.info("session is invalidate, remove from cache：" + key);
						}
					}
				}
			}
		}).start();
	}
}