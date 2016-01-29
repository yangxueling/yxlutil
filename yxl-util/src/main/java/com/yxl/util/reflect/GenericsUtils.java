package com.yxl.util.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 泛型工具类
 * @author yxl
 */
public class GenericsUtils {
    /**
     * 获取类后面传入泛型的Class
     *
     * @param clazz 目标类
     * @return 类后面的第一个泛型的class
     */
    public static Class getGenericClass(Class clazz) {
        return getGenericClass(clazz, 0);
    }

    /**
     * 获取类后面传入泛型的Class
     *
     * @param clazz 目标类
     * @param index 第几个泛型
     * @return 泛型的class
     */
    public static Class getGenericClass(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (genType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

            if ((params != null) && (params.length >= (index - 1))) {
                return (Class) params[index];
            }
        }
        return null;
    }
}