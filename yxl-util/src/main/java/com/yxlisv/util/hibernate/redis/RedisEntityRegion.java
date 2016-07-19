package com.yxlisv.util.hibernate.redis;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;

import com.yxlisv.util.hibernate.redis.access.RedisEntityRegionAccessStrategy;

/**
 * <p>Redis 实体类区域包装器</p>
 * @author 杨雪令
 * @time 2016年3月22日上午11:40:41
 * @version 1.0
 */
public class RedisEntityRegion extends RedisTransactionalDataRegion implements EntityRegion {

	public RedisEntityRegion(String regionName) {
		super(regionName);
	}

	@Override
	public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return new RedisEntityRegionAccessStrategy(regionName, accessType, this);
	}
}