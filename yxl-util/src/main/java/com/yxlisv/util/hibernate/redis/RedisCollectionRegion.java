package com.yxlisv.util.hibernate.redis;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;

public class RedisCollectionRegion extends RedisTransactionalDataRegion implements CollectionRegion {

	public RedisCollectionRegion(String regionName) {
		super(regionName);
	}

	@Override
	public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return null;
	}
}