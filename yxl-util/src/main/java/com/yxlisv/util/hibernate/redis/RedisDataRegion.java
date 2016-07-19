package com.yxlisv.util.hibernate.redis;

import java.util.Collections;
import java.util.Map;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.hibernate.redis.util.KeyUtil;
import com.yxlisv.util.redis.JedisUtil;

/**
 * <p>Redis 数据区域包装器</p>
 * @author 杨雪令
 * @time 2016年3月22日上午11:40:41
 * @version 1.0
 */
public abstract class RedisDataRegion implements Region {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//实体类的region名称
	protected String regionName;
	
	/**
	 * <p>构造方法</p>
	 * @param regionName 实体类的region名称
	 * @author 杨雪令
	 * @time 2016年5月17日下午12:35:37
	 * @version 1.0
	 */
	public RedisDataRegion(String regionName) {
		this.regionName = regionName;
	}

	@Override
	public String getName() {
		return "redis_hibernate";
	}

	@Override
	public void destroy() throws CacheException {
		logger.debug("RedisDataRegion destroy...");
	}

	@Override
	public long getSizeInMemory() {
		return 0;
	}

	@Override
	public long getElementCountInMemory() {
		return 0;
	}

	@Override
	public long getElementCountOnDisk() {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public long nextTimestamp() {
		return System.currentTimeMillis();
	}

	@Override
	public int getTimeout() {
		return 0;
	}

	@Override
	public boolean contains(Object key) {
		logger.debug("Redis DataRegion contains[regionName="+ regionName +", key="+ key +"]");
		if (JedisUtil.get(KeyUtil.formatKey(regionName, key)) == null) return false;
		return true;
	}
}
