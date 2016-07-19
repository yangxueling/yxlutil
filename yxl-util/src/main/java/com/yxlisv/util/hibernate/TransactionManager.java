package com.yxlisv.util.hibernate;

import org.hibernate.Session;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * <p>事物管理器</p>
 * @author 杨雪令
 * @time 2016年3月9日上午10:38:04
 * @version 1.0
 */
public class TransactionManager {

	// 不需要事物处理的方法
	private static final String[] dontTransactionMethods = { "page", "get", "find", "search", "load" };

	/**
	 * <p>根据方法名判断是否需要进行事物处理</p>
	 * @param methodName 方法名
	 * @return boolean 是否需要进行事物处理
	 * @author 杨雪令
	 * @time 2016年3月14日下午12:37:11
	 * @version 1.0
	 */
	public static boolean doTransaction(String methodName) {
		methodName = methodName.toLowerCase();
		for (String dontNeedMethod : dontTransactionMethods) {
			if (methodName.startsWith(dontNeedMethod)) return false;
			if (methodName.endsWith(dontNeedMethod)) return false;
		}
		return true;
	}

	/**
	 * <p>开启事物</p>
	 * @param methodName 方法名
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:40:58
	 * @version 1.0
	 */
	public static void begin(String methodName) {

		if (!doTransaction(methodName)) return;
		// 开启事物
		SessionManager.getSession().beginTransaction();
	}

	/**
	 * <p>提交事务</p>
	 * @param methodName 方法名
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:41:28
	 * @version 1.0
	 */
	public static void commit(String methodName) {

		try {
			if (!doTransaction(methodName)) return;
			Session session = SessionManager.getSession();
			if (session == null || !session.isOpen()) return;// session 没有开启，中断
			if (!session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) return;// 事物没有开启，中断

			session.flush();
			session.getTransaction().commit();
		} finally {
			SessionManager.closeCurrentSession();
		}
	}

	/**
	 * <p>回滚事务</p>
	 * @param methodName 方法名
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:55:00
	 * @version 1.0
	 */
	public static void rollback(String methodName) {

		try {
			if (!doTransaction(methodName)) return;
			Session session = SessionManager.getSession();
			if (session == null || !session.isOpen()) return;// session 没有开启，中断
			session.getTransaction().rollback();
		} finally {
			SessionManager.forceCloseCurrentSession();
		}
	}
}