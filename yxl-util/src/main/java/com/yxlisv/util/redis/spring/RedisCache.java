package com.yxlisv.util.redis.spring;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.Cache;

import com.yxlisv.util.redis.JedisUtil;

/**
 * <p>redis spring cache支持</p>
 * @author 杨雪令
 * @time 2016年3月21日下午4:52:23
 * @version 1.0
 */
public class RedisCache implements Cache {

	// key前缀
	private static final String keyPrefix = "SPRING_";

	/**
	 * <p>格式化Key</p>
	 * @param key key字符串
	 * @return String 
	 * @author 杨雪令
	 * @time 2016年3月24日下午3:58:49
	 * @version 1.0
	 */
	private String formatKey(Object key) {
		return keyPrefix + DigestUtils.md5Hex(key.toString());
	}

	@Override
	public String getName() {
		return "default";
	}

	@Override
	public Object getNativeCache() {
		return null;
	}

	@Override
	public ValueWrapper get(final Object key) {
		ValueWrapper valueWrapper = new ValueWrapper() {
			@Override
			public Object get() {
				return JedisUtil.get(formatKey(key));
			}
		};
		if (valueWrapper.get() == null) return null;
		return valueWrapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Class<T> type) {
		return (T) JedisUtil.get(formatKey(key));
	}

	@Override
	public void put(Object key, Object value) {
		JedisUtil.set(formatKey(key), value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return null;
	}

	@Override
	public void evict(Object key) {
		JedisUtil.delete(formatKey(key));
	}

	@Override
	public void clear() {
		JedisUtil.delete(keyPrefix + "*");
	}
}