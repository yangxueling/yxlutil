package com.yxlisv.util.reflect;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>注解反射工具类</p>
 * @author 杨雪令
 * @time 2016年7月12日下午1:20:09
 * @version 1.0
 */
public class AnnotationUtil {
	
	// 定义一个全局的记录器，通过LoggerFactory获取
	protected static Logger logger = LoggerFactory.getLogger(AnnotationUtil.class);
	
	/**
	 * <p>获取方法的值</p>
	 * <p>找到第一个配置了注解annotationClass的方法，invoke并返回结果</p>
	 * @param obj 要获取的对象
	 * @param annotationClass 方法使用的注解
	 * @return Object 
	 * @author 杨雪令
	 * @time 2016年7月12日下午1:20:24
	 * @version 1.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getMethodValue(Object obj, Class annotationClass) {
		if (obj == null || annotationClass == null) return null;
		Method[] methods = obj.getClass().getMethods();
		if (methods.length <= 0) return null;
		for (Method method : methods) {
			try{
				if(method.isAnnotationPresent(annotationClass)){
					return method.invoke(obj, new Class[]{});
				}
			} catch(Exception e){
				logger.error("", e);
				return null;
			}
			
		}
		return null;
	}
}