package com.yxlisv.util.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>对象Copy</p>
 * @author 杨雪令
 * @time 2016年3月11日下午5:19:56
 * @version 1.0
 */
public class Copy {

	/**
	 * <p>深度克隆</p>
	 * @param obj 要克隆的对象
	 * @return Object 
	 * @author 杨雪令
	 * @time 2016年3月11日下午5:20:16
	 * @version 1.0
	 */
	public static Object deepClone(Object obj) {
		try{
			// 将对象写到流里
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			// 从流里读出来
			ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
			ObjectInputStream oi = new ObjectInputStream(bi);
			return (oi.readObject());
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}