package com.yxlisv.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>反射工具类</p>
 * @author 杨雪令
 * @time 2016年3月17日下午1:29:40
 * @version 1.0
 */
public class ReflectionUtils {

	// 定义一个全局的记录器，通过LoggerFactory获取
	protected static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

	// 基础类型
	private static String baseTypes = "short,int,long,float,double,char,byte,boolean";

	/**
	 * <p>是否为JDK的数据类型</p>
	 * @param field field 
	 * @author 杨雪令
	 * @time 2016年4月22日下午4:10:15
	 * @version 1.0
	 */
	public static boolean isJDKType(Class<?> c) {
		if (c.toString().contains(" java.")) return true;
		if (c.toString().contains(" [Ljava.")) return true;
		if (baseTypes.contains(c.toString())) return true;
		return false;
	}

	/**
	 * <p>设置成员变量值</p>
	 * <p>忽略 private/protected 修饰符</p>
	 * @param object 要修改的对象
	 * @param fieldName	成员变量名
	 * @param value 值 
	 * @author 杨雪令
	 * @time 2016年3月17日下午1:31:01
	 * @version 1.0
	 */
	public static void setFieldValue(Object object, String fieldName, Object value) {
		
		if(object == null) return;
		// 可以用小数点匹配子类中的属性
		if (fieldName.contains(".")) {
			Object childObj = getFieldValue(object, fieldName.substring(0, fieldName.indexOf(".")));
			setFieldValue(childObj, fieldName.substring(fieldName.indexOf(".") + 1), value);
		}

		// 根据 对象和属性名通过反射 调用上面的方法获取 Field对象
		Field field = getField(object, fieldName);
		if (field == null) return;
		field.setAccessible(true); // 抑制Java对其的检查
		try {
			// 将 object 中 field 所代表的值 设置为 value
			field.set(object, value);
		} catch (Exception e) {
			logger.error("设置成员变量值出错：Object=" + object + ", fieldName=" + fieldName + ", value=" + value, e);
		}
	}

	/**
	 * <p>获取成员变量值</p>
	 * <p>忽略 private/protected 修饰符</p>
	 * @param object 要读取的对象
	 * @param fieldName	成员变量名
	 * @return Object 结果值
	 * @author 杨雪令
	 * @time 2016年3月17日下午1:33:25
	 * @version 1.0
	 */
	public static Object getFieldValue(Object object, String fieldName) {

		// 可以用小数点匹配子类中的属性
		if (fieldName.contains(".")) {
			Object childObj = getFieldValue(object, fieldName.substring(0, fieldName.indexOf(".")));
			return getFieldValue(childObj, fieldName.substring(fieldName.indexOf(".") + 1));
		}

		// 根据 对象和属性名通过反射 调用上面的方法获取 Field对象
		Field field = getField(object, fieldName);
		if (field == null) return null;
		field.setAccessible(true); // 抑制Java对其的检查
		try {
			// 获取 object 中 field 所代表的属性值
			return field.get(object);
		} catch (Exception e) {
			logger.error("获取成员变量值出错：Object=" + object + ", fieldName=" + fieldName, e);
		}
		return null;
	}

	/**
	 * <p>创建对象</p>
	 * @param name 包名类名
	 * @return 创建好的对象
	 * @throws Exception Object 
	 * @author 杨雪令
	 * @time 2016年3月17日下午1:34:58
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static Object newInstance(String name) throws Exception {
		Class classType = Class.forName(name);
		return classType.newInstance();
	}

	/**
	 * <p>获取属性字段</p>
	 * @param targetClass 目标类
	 * @return String[] 属性字段名称数组
	 * @author 杨雪令
	 * @time 2016年3月9日下午12:59:24
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getFields(Class targetClass) {
		Field[] fields = targetClass.getDeclaredFields();
		if (fields.length <= 0) return null;
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	/**
	 * <p>获取基本类型的属性字段</p>
	 * @param targetClass 目标类
	 * @return String[] 属性字段名称数组
	 * @author 杨雪令
	 * @time 2016年3月9日下午12:59:24
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getBaseFields(Class targetClass) {
		Field[] fields = targetClass.getDeclaredFields();
		if (fields.length <= 0) return null;
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields) {
			String fieldTypeName = field.getType().getName().toUpperCase();
			if (fieldTypeName.contains("LIST") || fieldTypeName.contains("SET")) continue;
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	/**
	 * 循环向上转型, 获取对象的 DeclaredMethod
	 * @param object : 子类对象
	 * @param methodName : 父类中的方法名
	 * @param parameterTypes : 父类中的方法参数类型
	 * @return 父类中的方法对象
	 */
	public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {

		Method method = null;
		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				method = clazz.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {}
		}
		return null;
	}

	/**
	 * 直接调用对象方法, 而忽略修饰符(private, protected, default)
	 * @param object : 子类对象
	 * @param methodName : 父类中的方法名
	 * @param parameterTypes : 父类中的方法参数类型
	 * @param parameters : 父类中的方法参数
	 * @return 父类中方法的执行结果
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) {

		// 根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null) return null;
		// 抑制Java对方法进行检查,主要是针对私有方法而言
		method.setAccessible(true);
		try {
			// 调用object 的 method 所代表的方法，其方法的参数是 parameters
			if (null != method) return method.invoke(object, parameters);
		} catch (Exception e) {
			logger.error("invokeMethod error", e);
		}
		return null;
	}

	/**
	 * <p>获取一个对象的属性</p>
	 * @param targetObject 目标对象
	 * @param fieldName	属性名称
	 * @return Field 属性
	 * @author 杨雪令
	 * @time 2016年3月22日下午2:34:36
	 * @version 1.0
	 */
	public static Field getField(Object targetObject, String fieldName) {

		if (targetObject == null) return null;
		return getField(targetObject.getClass(), fieldName);
	}
	
	
	/**
	 * <p>获取一个对象的属性</p>
	 * @param target 目标类
	 * @param name	名称
	 * @return Field 属性
	 * @author 杨雪令
	 * @time 2016年3月22日下午2:34:36
	 * @version 1.0
	 */
	public static Field getField(@SuppressWarnings("rawtypes") Class target, String name) {

		if (target == null) return null;
		Field[] fields = target.getDeclaredFields();
		if (fields.length <= 0) return null;
		for (Field field : fields) {
			if (field.getName().equals(name)) return field;
		}
		return null;
	}
	
	
	/**
	 * <p>获取一个对象的方法</p>
	 * @param target 目标类
	 * @param name	名称
	 * @return Field 属性
	 * @author 杨雪令
	 * @time 2016年3月22日下午2:34:36
	 * @version 1.0
	 */
	public static Method getMethod(@SuppressWarnings("rawtypes") Class target, String name) {

		if (target == null) return null;
		Method[] methods = target.getMethods();
		if (methods.length <= 0) return null;
		for (Method method : methods) {
			if (method.getName().equals(name)) return method;
		}
		return null;
	}
}