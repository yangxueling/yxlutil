package com.yxlisv.util.hibernate;

import org.aspectj.lang.JoinPoint;

/**
 * <p>事物管理器Aspect</p>
 * @author 杨雪令
 * @time 2016年3月9日上午10:38:04
 * @version 1.0
 */
public class TransactionAspect {

	/**
	 * <p>开启事物</p>
	 * @param jp spring aop 切入点
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:40:58
	 * @version 1.0
	 */
	public void begin(JoinPoint jp) {
		
		TransactionManager.begin(jp.getSignature().getName());
	}

	/**
	 * <p>提交事务</p>
	 * @param jp spring aop 切入点
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:41:28
	 * @version 1.0
	 */
	public void commit(JoinPoint jp) {

		TransactionManager.commit(jp.getSignature().getName());
	}

	/**
	 * <p>回滚事务</p>
	 * @param jp spring aop 切入点
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:55:00
	 * @version 1.0
	 */
	public void rollback(JoinPoint jp) {

		TransactionManager.rollback(jp.getSignature().getName());
	}
}