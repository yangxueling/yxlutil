package com.yxlisv.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射代理类
 * @author: 杨雪令
 * @version 1.0
 */
public class ClassProxy {

	private static Logger logger = LoggerFactory.getLogger(ClassProxy.class);

	/**
	 * 根据类的名称打印它包含的方法和方法参数及方法返回值
	 * @author: 杨雪令
	 * @version: 2010-3-24 下午12:42:04
	 */
	@SuppressWarnings("rawtypes")
	public void checkClass(String className) {
		try {
			Class cls = Class.forName(className);
			Method[] methlist = cls.getDeclaredMethods();
			for (int i = 0; i < methlist.length; i++) {
				Method m = methlist[i];
				System.out.println("method name = " + m.getName());
				System.out.println("declarclass = " + m.getDeclaringClass());
				Class params[] = m.getParameterTypes();//参数
				for (int j = 0; j < params.length; j++)
					System.out.println("param #" + j + " " + params[j].getSimpleName() + " " + params[j]);
				Class excep[] = m.getExceptionTypes();//返回异常
				for (int j = 0; j < excep.length; j++)
					System.out.println("exc #" + j + " " + excep[j]);
				System.out.println("return type = " + m.getReturnType());
				System.out.println("-----");
			}
		} catch (Throwable e) {
			logger.error("checkClass error", e);
		}
	}

	/**
	 * 获取对象的属性值
	 * @param obj java对象
	 * @param propName 属性名称
	 * @autor yxl
	 */
	@SuppressWarnings("rawtypes")
	public static Object getProperty(Object obj, String propName) {
		Class objClass = obj.getClass();
		try {
			Field field = objClass.getDeclaredField(propName);
			field.setAccessible(true);
			if (field != null) { return field.get(obj); }
		} catch (Exception e) {
			logger.error("获取属性值出错", e);
		}
		return null;
	}

	/**
	 * 执行对象方法
	 * @param obj 要处理的对象
	 * @param methodName 方法名
	 * @param args 参数
	 * @date 2015年11月20日 上午10:10:19 
	 * @author yxl
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object invoke(Object obj, String methodName, Object[] args) {
		Class objCls = obj.getClass();
		Class[] param = null;
		if (args != null) {
			param = new Class[args.length];
			int i = 0;
			for (Object paramObj : args) {
				param[i] = paramObj.getClass();
			}
		}
		Method method = null;
		try {
			method = objCls.getMethod(methodName, param);
			return method.invoke(obj, args);
		} catch (Exception e) {
			logger.error("执行对象方法出错", e);
		}
		return null;
	}
}