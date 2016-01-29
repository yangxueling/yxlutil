package com.yxlisv.util.redis.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.cookie.CookieUtil;
import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.redis.JedisUtil;

public class RedisSession implements HttpSession{
	
	private static Logger logger = LoggerFactory.getLogger(RedisSession.class);
	
	/** session 前缀 */
	public static String sessionPrefix = "rsk_";
	/** 持有web容器的HttpSession对象 */
	private HttpSession session;
	/** HttpServletRequest对象 */
	private HttpServletRequest request;
	/** HttpServletResponse对象 */
	private HttpServletResponse response;
	/** 存放到redis服务器的key */
	private String key = "";
	/** session过期时间，分钟 */
	private int timeout = 60;
	/** session中存放的值 */
	private Map<String, Object> valueMap = new HashMap<String, Object>();
	
	/**
	 * 初始化
	 *	@param session HttpSession对象
	 *@param key sessionkey
	 * @date 2015年12月12日 上午11:22:30 
	 * @author yxl
	 */
	@SuppressWarnings({ "deprecation" })
	public RedisSession(HttpSession session, HttpServletRequest request, HttpServletResponse response){
		this.session = session;
		this.request = request;
		this.response = response;
		this.key = getSessionKey();
		timeout = session.getMaxInactiveInterval()/60;
		getValueMapFromRedis();
		String[] valueNames = session.getValueNames();
		if(valueNames!=null){
			for(String valueName : valueNames){
				valueMap.put(valueName, session.getAttribute(valueName));
			}
		}
	}

	@Override
	public long getCreationTime() {
		return session.getCreationTime();
	}

	@Override
	public String getId() {
		if(key!=null && key.length()>0) return key;
		return session.getId();
	}

	@Override
	public long getLastAccessedTime() {
		return session.getLastAccessedTime();
	}

	@Override
	public ServletContext getServletContext() {
		return session.getServletContext();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		session.setMaxInactiveInterval(interval);
		timeout = interval / 60;
	}

	@Override
	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	@SuppressWarnings("deprecation")
	@Override
	public HttpSessionContext getSessionContext() {
		return session.getSessionContext();
	}

	@Override
	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object getValue(String name) {
		return session.getValue(name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getAttributeNames() {
		return session.getAttributeNames();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String[] getValueNames() {
		return session.getValueNames();
	}

	@Override
	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
		valueMap.put(name, value);
		updateValueMapToRedis();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void putValue(String name, Object value) {
		session.putValue(name, value);
		valueMap.put(name, value);
		updateValueMapToRedis();
	}

	@Override
	public void removeAttribute(String name) {
		session.removeAttribute(name);
		valueMap.remove(name);
		updateValueMapToRedis();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeValue(String name) {
		session.removeValue(name);
		valueMap.remove(name);
		updateValueMapToRedis();
	}

	@SuppressWarnings({ "rawtypes"})
	@Override
	public void invalidate() {
		for(Enumeration e=session.getAttributeNames(); e.hasMoreElements();) session.removeAttribute(e.nextElement().toString());
		if(key==null || key.length()<1) return;
		try {
			JedisUtil.delete(key);
		} catch (Exception e) {
			logger.info("从redis删除session出错：" + e.getMessage());
		}
	}

	@Override
	public boolean isNew() {
		return session.isNew();
	}
	
	/**
	 * 获取sessionkey
	 * @date 2015年12月12日 下午2:33:47 
	 * @author yxl
	 */
	protected String getSessionKey() {
		String keyName = "rsk_";
		keyName += request.getContextPath().replaceAll("/", "");
		String sessionKey = CookieUtil.getString(keyName, request);
		if(sessionKey==null) {
			sessionKey = sessionPrefix + request.getRemoteAddr() + "_" + System.currentTimeMillis() + NumberUtil.getRandomStr();
			CookieUtil.setString(keyName, sessionKey, request, response);
		}
		return sessionKey;
    }
	
	/**
	 * 从redis 获取ValueMap
	 * @date 2015年12月12日 上午11:44:42 
	 * @author yxl
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getValueMapFromRedis(){
		if(key==null || key.length()<1) return;
		Object cacheSessionValueMap = null;
		cacheSessionValueMap = JedisUtil.get(key);
		if(cacheSessionValueMap!=null) JedisUtil.setTimeout(key, timeout*60);
		//同步redis中的数据到session对象中
		if(cacheSessionValueMap!=null) {
			this.valueMap = (Map) cacheSessionValueMap;
			for(Iterator it=valueMap.entrySet().iterator(); it.hasNext();){
				Map.Entry<String, Object> entry = (Entry<String, Object>) it.next();
				session.setAttribute(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/**
	 * 更新ValueMap到redis
	 * @date 2015年12月12日 上午11:44:42 
	 * @author yxl
	 */
	public void updateValueMapToRedis(){
		if(key==null || key.length()<1) return;
		if(valueMap==null) return;
		JedisUtil.set(key, valueMap, timeout*60);
	}
}