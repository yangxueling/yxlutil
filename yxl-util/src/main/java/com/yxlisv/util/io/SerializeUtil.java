package com.yxlisv.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;

/**
 * 序列号工具类
 * @createTime 2015年10月15日 下午3:50:59 
 * @author yxl
 */
public class SerializeUtil {

	private static Logger logger = LoggerFactory.getLogger(SerializeUtil.class);

	/**
	 * 序列号对象
	 * @date 2015年10月15日 下午3:51:28 
	 * @author yxl
	 */
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();
		} catch (Exception e) {
			logger.error("序列化出错", e);
		} finally {
			try {
				if (baos != null) baos.close();
				if (oos != null) oos.close();
			} catch (IOException e) {
				logger.error("反序列化出错", e);
			}
		}
		return null;
	}

	/**
	 * 读取序列化对象
	 * @date 2015年10月15日 下午3:51:50 
	 * @author yxl
	 */
	public static Object unserialize(byte[] bytes) {
		if (bytes == null) return null;
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			logger.error("反序列化出错", e);
		} finally {
			try {
				if (bais != null) bais.close();
				if (ois != null) ois.close();
			} catch (IOException e) {
				logger.error("反序列化出错", e);
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static void main(String[] args) throws JsonGenerationException, IOException {
		Map map = new HashMap();
		for (int i = 1; i <= 3000; i++)
			map.put("k" + i, "v_" + i);

		// 测试序列化
		byte[] bytes = null;
		long startTime = System.currentTimeMillis();
		for (int i = 1; i <= 1000; i++) {
			bytes = SerializeUtil.serialize(map);
		}
		// System.out.println("serialize size " + bytes.length);
		System.out.println("serialize use " + (System.currentTimeMillis() - startTime));

		// 测试反序列化
		startTime = System.currentTimeMillis();
		for (int i = 1; i <= 1000; i++) {
			Map map2 = (Map) SerializeUtil.unserialize(bytes);
		}
		System.out.println("unserialize use " + (System.currentTimeMillis() - startTime));

		// Jackson 序列化
		/*
		 * String json = null; startTime = System.currentTimeMillis();
		 * ObjectMapper mapper = new ObjectMapper(); for(int i=1; i<=1000; i++)
		 * { json = mapper.writeValueAsString(map); } //System.out.println(
		 * "Jackson serialize size " + json.length()); System.out.println(
		 * "Jackson serialize use " + (System.currentTimeMillis() - startTime));
		 * 
		 * //Jackson 反序列化 startTime = System.currentTimeMillis(); for(int i=1;
		 * i<=100; i++) { Map map2 = mapper.readValue(json, Map.class); }
		 * System.out.println("Jackson unserialize use " +
		 * (System.currentTimeMillis() - startTime));
		 * 
		 * 
		 * //Fastjson 序列化 startTime = System.currentTimeMillis(); byte[]
		 * jsonByte = null; for(int i=1; i<=1000; i++) { jsonByte =
		 * JSON.toJSONBytes(map); } //System.out.println(
		 * "Fastjson serialize size " + jsonByte.length); System.out.println(
		 * "Fastjson serialize use " + (System.currentTimeMillis() -
		 * startTime));
		 * 
		 * //Fastjson 反序列化 startTime = System.currentTimeMillis(); for(int i=1;
		 * i<=100; i++) { Map map2 = JSON.parseObject(json, Map.class); }
		 * System.out.println("Fastjson unserialize use " +
		 * (System.currentTimeMillis() - startTime));
		 */
	}
}
