package com.yxl.util.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.yxl.util.proxy.test.TestProxy;
import com.yxl.util.proxy.test.TestProxyImpl;

/**
 * JDK動態代理
 * 被代理的類，需要至少實現一個接口
 * @createTime 2016年1月20日 下午2:44:59 
 * @author yxl
 */
public class DynamicProxy implements InvocationHandler {
	/** 被代理的對象 */
	private Object target;
	/** 代理對象 */
	private Object proxy;
	/** 獲取代理對象 */
	public Object getProxy() {
		return proxy;
	}

	/** 創建動態代理類 */
	public DynamicProxy(Object target) {
		this.target = target;
		this.proxy = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		System.out.println("--Proxy start...");
		System.out.println("--method name:" + method.getName());
		result = method.invoke(target, args);
		System.out.println("--Proxy end...");
		return result;
	}
	
	public static void main(String[] args) {
		TestProxy testProxy = (TestProxy) new DynamicProxy(new TestProxyImpl()).getProxy();
		testProxy.println();
		System.out.println();
	}
}