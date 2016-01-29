package com.yxl.util.redis.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redis HttpServletRequest包装器
 * 使用redis 共享 session
 * @Author 杨雪令
 * @Date: 2015-12-12
 * @version: 1.0
 */
public class RedisHttpServletRequestWrapper extends HttpServletRequestWrapper {
	
	private static Logger logger = LoggerFactory.getLogger(RedisHttpServletRequestWrapper.class);
	
	/** session 前缀 */
	public static String sessionPrefix = "httpsession_";
	/** HttpServletRequest对象 */
	private HttpServletRequest request;
	/** HttpServletResponse对象 */
	private HttpServletResponse response;

	/**
	 * 构造方法
	 * @date 2015年12月12日 上午10:43:30 
	 * @author yxl
	 */
	public RedisHttpServletRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
		super(request);
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpSession getSession(boolean create) {
		RedisSession redisSession = null;
		try{
			redisSession = new RedisSession(super.getSession(create), request, response);
		} catch(Exception e){
			logger.warn("cerate redisSession error : " + e.getMessage());
		}
		return redisSession;
	}

	@Override
	public HttpSession getSession() {
		RedisSession redisSession = null;
		try{
			redisSession = new RedisSession(super.getSession(), request, response);
		} catch(Exception e){
			logger.warn("cerate redisSession error : " + e.getMessage());
		}
		return redisSession;
	}
}