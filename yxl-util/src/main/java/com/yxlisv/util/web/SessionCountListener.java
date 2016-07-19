package com.yxlisv.util.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.yxlisv.util.math.NumberUtil;

/**
 * <p>HttpSession 计数</p>
 * <p>不适用集群模式</p>
 * @author 杨雪令
 * @time 2016年5月18日下午3:17:53
 * @version 1.0
 */
public class SessionCountListener implements HttpSessionListener {
	
	/** HttpSession数量统计在ServletContext中的key */
	public final static String SESSION_COUNT_KEY = "sessionCount";

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		ServletContext servletContext = event.getSession().getServletContext();
		int sessionCount = NumberUtil.parseInt(servletContext.getAttribute(SESSION_COUNT_KEY));
		servletContext.setAttribute(SESSION_COUNT_KEY, ++sessionCount);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		ServletContext servletContext = event.getSession().getServletContext();
		int sessionCount = NumberUtil.parseInt(servletContext.getAttribute(SESSION_COUNT_KEY));
		sessionCount--;
		if(sessionCount < 0) sessionCount = 0;
		servletContext.setAttribute(SESSION_COUNT_KEY, sessionCount);
	}
}