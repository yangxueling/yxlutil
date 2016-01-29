package com.yxlisv.util.map;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * map 工具类
 * @author yxl
 */
public class MapUtil {

    /** 
     * 将一个 JavaBean 对象转化为一个  Map 
     * @param bean 要转化的JavaBean 对象 
     * @return 转化出来的  Map 对象 
     * @throws IntrospectionException 如果分析类属性失败 
     * @throws IllegalAccessException 如果实例化 JavaBean 失败 
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败 
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map beanToMap(Object bean)  
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {  
        Class type = bean.getClass();  
        Map returnMap = new HashMap();  
        BeanInfo beanInfo = Introspector.getBeanInfo(type);  
  
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();  
        for (int i = 0; i< propertyDescriptors.length; i++) {  
            PropertyDescriptor descriptor = propertyDescriptors[i];  
            String propertyName = descriptor.getName();  
            if (!propertyName.equals("class")) {  
                Method readMethod = descriptor.getReadMethod();  
                Object result = readMethod.invoke(bean, new Object[0]);  
                if (result != null) {  
                    returnMap.put(propertyName, result);  
                } else {  
                    returnMap.put(propertyName, "");  
                }  
            }  
        }  
        return returnMap;  
    }
    
    /**
	 * Map<String, String[]> 转换 Map<String, String><br/>
	 * 如果数组中的元素等于1，直接取出来存为String
	 * @param srcMap 源map
	 * @autor yxl
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map parse(Map srcMap){
		if(srcMap == null) return null;
		Map newMap = new HashMap();//构造一个新的map
		for(Iterator it=srcMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			if(entry.getValue() == null) continue;
			//获取string数组值
			if(entry.getValue() instanceof String[]){
				String[] valArray = (String[]) entry.getValue();
				if(valArray.length<1) continue;
				//如果数组中只有一个元素
				if(valArray.length==1) newMap.put(entry.getKey().toString(), valArray[0]);
				else newMap.put(entry.getKey().toString(), valArray);
			} else 
				newMap.put(entry.getKey().toString(), entry.getValue().toString());
		}
		
		return newMap;
	}
	
	/**
	 * 清理val为 null/空字符串/全空格 的项<br/>
	 * @param srcMap 源map
	 * @autor yxl
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> trim(Map<String, String> srcMap){
		if(srcMap == null) return null;
		Map<String, String> newMap = new HashMap();//构造一个新的map
		for(Iterator it=srcMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			if(entry.getValue() == null) continue;
			//获取string数组值
			String val = entry.getValue().toString().trim();
			if(val.length()<1) continue;
			newMap.put(entry.getKey().toString(), val);
		}
		
		return newMap;
	}

	/**
	 * 转换为字符串
	 * @param srcMap 要转换的Map
	 * @param maxLength value最大长度，0为不限制
	 * @date 2015年12月2日 下午8:01:35 
	 * @author yxl
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Map srcMap, int maxLength) {
		if(srcMap == null) return null;
		StringBuffer mapSb = new StringBuffer();
		for(Iterator it=srcMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			if(entry.getValue() == null) continue;
			if(mapSb.length()>0) mapSb.append(", ");
			//获取string数组值
			if(entry.getValue() instanceof String[]){
				String[] valArray = (String[]) entry.getValue();
				if(valArray.length<1) continue;
				mapSb.append(entry.getKey().toString() + "=");
				for(int i=0; i<valArray.length; i++) {
					if(i>0) mapSb.append(",");
					if(maxLength>0 && valArray[i].length()>maxLength) mapSb.append(valArray[i].substring(0, maxLength) + "...");
					else mapSb.append(valArray[i]);
				}
			} else 
				if(maxLength>0 && entry.getValue().toString().length()>maxLength) mapSb.append(entry.getValue().toString().substring(0, maxLength) + "...");
				else mapSb.append(entry.getKey().toString() + "=" + entry.getValue());
		}
		return mapSb.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		Map map = new HashMap();
		map.put("f", "1333333333333");
		map.put("f1", new String[]{"2","5"});
		map.put("f2", new String("2"));
		System.out.println(toString(map, 5));
	}
}
