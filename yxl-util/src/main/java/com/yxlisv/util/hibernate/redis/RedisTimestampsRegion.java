package com.yxlisv.util.hibernate.redis;

import org.hibernate.cache.spi.TimestampsRegion;

/**
 * <p>Redis 时间戳区域包装器</p>
 * <p>用来标记一张表最后修改的时间，如果Cache的时间小于最后修改时间，缓存无效</p>
 * @author 杨雪令
 * @time 2016年3月22日下午1:07:20
 * @version 1.0
 */
public class RedisTimestampsRegion extends RedisGeneralDataRegion implements TimestampsRegion {

	public RedisTimestampsRegion(String regionName) {
		super(regionName);
	}}