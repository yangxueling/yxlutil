package com.yxlisv.util.hibernate.redis;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * <p>Redis NaturalId区域包装器</p>
 * @author 杨雪令
 * @time 2016年3月22日上午11:44:37
 * @version 1.0
 */
public class RedisNaturalIdRegion extends RedisTransactionalDataRegion implements NaturalIdRegion {

	public RedisNaturalIdRegion(String regionName) {
		super(regionName);
	}

	@Override
	public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return null;
	}
}