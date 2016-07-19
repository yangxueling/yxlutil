package com.yxlisv.util.dubbo;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.yxlisv.util.hibernate.HibernateUtil;
import com.yxlisv.util.hibernate.TransactionManager;
import com.yxlisv.util.reflect.ReflectionUtils;

/**
 * <p>Hibernate 事物过滤器</p>
 * @author 杨雪令
 * @time 2016年3月16日下午4:39:37
 * @version 1.0
 */
public class HibernateTransactionFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

		String methodName = invocation.getMethodName();
		TransactionManager.begin(methodName);// 开启事物
		Result result = null;
		try {
			result = invoker.invoke(invocation);// 执行业务逻辑
			if (result.hasException()) TransactionManager.rollback(methodName);// 事物回滚
		} catch (Exception e) {
			TransactionManager.rollback(methodName);// 事物回滚
			throw e;
		}
		TransactionManager.commit(methodName);// 提交事务
		
		//清理对象中和hibernate相关的对象
		HibernateUtil.cleanHibernateObject(result.getValue());
		return result;
	}
}