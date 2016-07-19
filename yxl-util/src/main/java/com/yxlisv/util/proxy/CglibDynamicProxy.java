/*package com.yxlisv.util.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.yxlisv.util.Page;

*//**
 * CGLIB動態代理
 * @createTime 2016年1月20日 下午2:44:59 
 * @author yxl
 *//*
public class CglibDynamicProxy implements MethodInterceptor {
	
	*//** 被代理的對象 *//*
	private Object target;
	*//** 代理對象 *//*
	private Object proxy;
	*//** 獲取代理對象 *//*
	public Object getProxy() {
		return proxy;
	}
	
	*//** 創建動態代理類 *//*
	public CglibDynamicProxy(Object target) {
		this.target = target;
		//創建代理對象
		Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(target.getClass());  
        enhancer.setCallback(this);
		this.proxy = enhancer.create();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Object result = null;
		System.out.println("--Proxy start...");
		System.out.println("--method name:" + method.getName());
		result = method.invoke(target, args);
		System.out.println("--Proxy end...");
		return result;
	}

	public static void main(String[] args) {
		Page page = (Page) new CglibDynamicProxy(new Page()).getProxy();
		System.out.println("pn = " + page.getPn());
		System.out.println();
	}
}*/