package com.yxlisv.util.redis.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.cookie.CookieUtil;
import com.yxlisv.util.redis.JedisUtil;

/**
 * <p>Redis Session</p>
 * <p>实现跨服务器HttpSession共享</p>
 * @author 杨雪令
 * @time 2016年5月18日上午10:45:29
 * @version 1.0
 */
public class RedisSession implements HttpSession{
	
	private static Logger logger = LoggerFactory.getLogger(RedisSession.class);
	
	/** session 前缀 */
	public static String sessionPrefix = "yrsk_";
	/** 持有web容器的HttpSession对象 */
	private HttpSession httpSession;
	/** HttpServletRequest对象 */
	private HttpServletRequest request;
	/** HttpServletResponse对象 */
	private HttpServletResponse response;
	/** 存放到redis服务器的key */
	private String key = "";
	/** session过期时间，分钟 */
	private int timeout = 60;
	/** servletContext */
	public static ServletContext servletContext = null;
	
	/**
	 * 初始化
	 *	@param session HttpSession对象
	 *@param key sessionkey
	 * @date 2015年12月12日 上午11:22:30 
	 * @author yxl
	 */
	public RedisSession(HttpSession session, HttpServletRequest request, HttpServletResponse response){
		this.httpSession = session;
		this.request = request;
		this.response = response;
		this.key = getSessionKey();
		timeout = session.getMaxInactiveInterval()/60;
		if(servletContext == null) servletContext = RedisServletContext.getInstance(request.getServletContext());
	}

	@Override
	public long getCreationTime() {
		return httpSession.getCreationTime();
	}

	@Override
	public String getId() {
		if(key!=null && key.length()>0) return key;
		return httpSession.getId();
	}

	@Override
	public long getLastAccessedTime() {
		return httpSession.getLastAccessedTime();
	}

	@Override
	public ServletContext getServletContext() {
		return httpSession.getServletContext();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		httpSession.setMaxInactiveInterval(interval);
		timeout = interval / 60;
	}

	@Override
	public int getMaxInactiveInterval() {
		return httpSession.getMaxInactiveInterval();
	}

	@SuppressWarnings("deprecation")
	@Override
	public HttpSessionContext getSessionContext() {
		return httpSession.getSessionContext();
	}

	@Override
	public Object getAttribute(String name) {
		syncFromRedis();
		return httpSession.getAttribute(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object getValue(String name) {
		syncFromRedis();
		return httpSession.getValue(name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getAttributeNames() {
		syncFromRedis();
		return httpSession.getAttributeNames();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String[] getValueNames() {
		syncFromRedis();
		return httpSession.getValueNames();
	}

	@Override
	public void setAttribute(String name, Object value) {
		httpSession.setAttribute(name, value);
		putToRedis(name, value);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void putValue(String name, Object value) {
		httpSession.putValue(name, value);
		putToRedis(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		httpSession.removeAttribute(name);
		deleteFromRedis(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeValue(String name) {
		httpSession.removeValue(name);
		deleteFromRedis(name);
	}

	@Override
	public void invalidate() {
		if(key==null || key.length()<1) return;
		try {
			JedisUtil.delete(key);
		} catch (Exception e) {
			logger.error("从redis删除session出错：" + e.getMessage());
		} finally{
			httpSession.invalidate();
		}
	}

	@Override
	public boolean isNew() {
		return httpSession.isNew();
	}
	
	/**
	 * 获取sessionkey
	 * @date 2015年12月12日 下午2:33:47 
	 * @author yxl
	 */
	protected String getSessionKey() {
		String keyName = sessionPrefix + request.getContextPath().replaceAll("/", "");
		String sessionKey = CookieUtil.getString(keyName, request);
		if(sessionKey==null) {
			sessionKey = sessionPrefix + request.getContextPath().replaceAll("/", "") + "_" + request.getRemoteAddr() + "_" + UUID.randomUUID().toString().replaceAll("-", "");
			CookieUtil.setString(keyName, sessionKey, request, response);
		}
		return sessionKey;
    }
	
	/**
	 * <p>从redis 同步数据到本地</p>
	 * @author 杨雪令
	 * @time 2016年5月18日上午11:28:48
	 * @version 1.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> syncFromRedis(){
		if(key==null || key.length()<1) return null;
		Map<String, Object> paramMap = null;
		paramMap = (Map) JedisUtil.get(key);
		if(paramMap != null) {
			JedisUtil.setTimeout(key, timeout*60);
			//同步redis中的数据到httpSession中
			for(Map.Entry<String, Object> entry : paramMap.entrySet()){
				httpSession.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		return paramMap;
	}
	
	/**
	 * <p>设置数据到redis</p>
	 * @param key key
	 * @param value 值 
	 * @author 杨雪令
	 * @time 2016年5月18日上午11:46:03
	 * @version 1.0
	 */
	public void putToRedis(String key, Object value){
		if(this.key==null || this.key.length()<1) return;
		Map<String, Object> paramMap = syncFromRedis();
		if(paramMap == null) paramMap = new HashMap<String, Object>();
		paramMap.put(key, value);
		JedisUtil.set(this.key, paramMap, timeout*60);
	}
	
	/**
	 * <p>从redis中删除数据</p>
	 * @param key key
	 * @author 杨雪令
	 * @time 2016年5月18日上午11:46:03
	 * @version 1.0
	 */
	public void deleteFromRedis(String key){
		if(this.key==null || this.key.length()<1) return;
		Map<String, Object> paramMap = syncFromRedis();
		if(paramMap != null && paramMap.containsKey(key)){
			paramMap.remove(key);
		}
		JedisUtil.set(this.key, paramMap, timeout*60);
	}
	
	/**
	 * <p>同步数据到redis</p>
	 * @author 杨雪令
	 * @time 2016年5月18日上午11:46:03
	 * @version 1.0
	 */
	public void syncToRedis(){
		if(this.key==null || this.key.length()<1) return;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for(String valueName : httpSession.getValueNames()){
			paramMap.put(valueName, httpSession.getAttribute(valueName));
		}
		JedisUtil.set(this.key, paramMap, timeout*60);
	}
}