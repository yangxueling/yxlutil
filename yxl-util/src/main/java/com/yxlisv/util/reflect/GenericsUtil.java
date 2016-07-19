package com.yxlisv.util.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>泛型工具类</p>
 * @author 杨雪令
 * @time 2016年3月8日下午5:10:00
 * @version 1.0
 */
public class GenericsUtil {

	/**
	 * <p>获取targetClass中传入的泛型所属Class</p>
	 * @param targetClass 要解析的class	
	 * @return Class 泛型所属的class
	 * @author 杨雪令
	 * @time 2016年3月8日下午5:10:10
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClass(Class targetClass) {
		return getClass(targetClass, 0);
	}

	/**
	 * 获取类后面传入泛型的Class
	 *
	 * @param targetClass 目标类
	 * @param index 第几个泛型
	 * @return 泛型的class
	 */

	/**
	 * <p>获取targetClass中传入的泛型所属Class</p>
	 * @param targetClass 要解析的class
	 * @param index	第几个泛型参数
	 * @return Class 泛型所属的class
	 * @author 杨雪令
	 * @time 2016年3月8日下午5:13:05
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClass(Class targetClass, int index) {
		Type genType = targetClass.getGenericSuperclass();

		if (genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

			if ((params != null) && (params.length >= (index - 1))) { return (Class) params[index]; }
		}
		return null;
	}
}