package com.yxlisv.util.hibernate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.collection.internal.PersistentBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.reflect.ReflectionUtils;

/**
 * <p>hibernate 工具类</p>
 * @author 杨雪令
 * @time 2016年4月26日下午3:58:05
 * @version 1.0
 */
public class HibernateUtil {

	// 定义一个全局的记录器，通过LoggerFactory获取
	protected static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

	// 清理hibernate相关对象，目标类中已经处理过的对象
	private static final ThreadLocal<Set<Object>> cleanHibernateObjectHandled = new ThreadLocal<Set<Object>>();
	
	
	/**
	 * <p>清理对象中和hibernate相关的对象</p>
	 * @param obj 要清理的对象
	 * @author 杨雪令
	 * @time 2016年4月26日下午3:58:51
	 * @version 1.0
	 */
	public static void cleanHibernateObject(Object obj) {
		if (obj == null) return;
		long startTime = System.currentTimeMillis();
		cleanHibernateObjectHandled.set(new HashSet<Object>());
		startCleanHibernateObject(obj);
		long useTime = System.currentTimeMillis() - startTime;
		if (useTime >= 10) logger.warn("cleanHibernateObject : " + obj.getClass() + " " + useTime + " ms");
	}

	/**
	 * <p>开始清理对象中和hibernate相关的对象</p>
	 * @param obj 要清理的对象
	 * @author 杨雪令
	 * @time 2016年4月26日下午3:58:51
	 * @version 1.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void startCleanHibernateObject(Object obj) {
		if (obj == null) return;

		// 解析List和Set
		if (obj instanceof java.util.List || obj instanceof java.util.Set) {
			Collection<Object> objectArray = (Collection<Object>) obj;
			for (Object child : objectArray) {
				startCleanHibernateObject(child);
			}
			return;
		}

		// 如果是JDK自带的对象，直接返回
		if (ReflectionUtils.isJDKType(obj.getClass())) return;

		// 解析成员变量
		for (Field field : obj.getClass().getDeclaredFields()) {
			//System.out.println(field.getType() + " / " + field.getName());
			try {
				field.setAccessible(true);
				Object fieldObj = field.get(obj);
				if (fieldObj == null) continue;

				// 如果对象已经被处理过，则不再处理
				if (cleanHibernateObjectHandled.get().contains(fieldObj)) {
					//logger.debug("cleanHibernateObject " + fieldObj + " has handled");
					continue;
				}
				cleanHibernateObjectHandled.get().add(fieldObj);

				// PersistentBag 转换为 ArrayList
				if (fieldObj instanceof org.hibernate.collection.internal.PersistentBag) {
					PersistentBag pb = (PersistentBag) field.get(obj);
					List newList = new ArrayList();
					newList.addAll(pb);
					field.set(obj, newList);
					continue;// OneToMany 关联集合内部不需要继续处理，节省性能
				}

				startCleanHibernateObject(fieldObj);
			} catch (Exception e) {
				logger.error("HibernateUtil cleanHibernateObject error", e);
			}
		}
	}
}