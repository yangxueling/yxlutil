package com.yxlisv.util.hibernate;

import org.hibernate.Session;

/**
 * <p>hibernate Session管理器</p>
 * @author 杨雪令
 * @time 2016年3月9日上午10:17:40
 * @version 1.0
 */
public class SessionManager {

	// 当前线程中的Hibernate Session
	private static final ThreadLocal<Session> currentSession = new ThreadLocal<Session>();

	// 打开Session直到页面渲染结束
	private static boolean openSessionInView = false;

	/**
	 * <p>设置是否支持openSessionInView</p>
	 * @param openSessionInView true/false
	 * @author 杨雪令
	 * @time 2016年3月9日上午11:23:34
	 * @version 1.0
	 */
	public static void setOpenSessionInView(boolean openSessionInView) {
		SessionManager.openSessionInView = openSessionInView;
	}
	
	/**
	 * <p>获取新的 session</p>
	 * @return Session hibernate session
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:19:56
	 * @version 1.0
	 */
	public static Session newSession() {
		
		//开启新的session
		Session session = MulitDBSessionFactory.getSessionFactory().openSession();
		currentSession.set(session);
		return session;
	}

	/**
	 * <p>获取 session</p>
	 * @return Session hibernate session
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:19:56
	 * @version 1.0
	 */
	public static Session getSession() {
		
		// 先尝试从当前线程变量中获取session，如果获取不到，新开启一个session
		Session session = currentSession.get();
		if (session != null) return session;

		return newSession();
	}

	/**
	 * <p>关闭当前线程中的Session</p> 
	 * @author 杨雪令
	 * @time 2016年3月9日上午11:14:59
	 * @version 1.0
	 */
	public static void closeCurrentSession() {
		if (openSessionInView) return;
		forceCloseCurrentSession();
	}

	/**
	 * <p>强制关闭当前线程中的Session</p> 
	 * @author 杨雪令
	 * @time 2016年3月9日上午11:14:59
	 * @version 1.0
	 */
	public static void forceCloseCurrentSession() {
		Session session = currentSession.get();
		if (session != null && session.isOpen()) {
			session.clear();
			session.close();
			currentSession.remove();
		}
	}
}