package com.yxlisv.util.hibernate.redis;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;

/**
 * <p>Redis 数据交换区域包装器</p>
 * @author 杨雪令
 * @time 2016年3月22日上午11:40:41
 * @version 1.0
 */
public class RedisTransactionalDataRegion extends RedisDataRegion implements TransactionalDataRegion {

	public RedisTransactionalDataRegion(String regionName) {
		super(regionName);
	}

	@Override
	public boolean isTransactionAware() {
		return false;
	}

	@Override
	public CacheDataDescription getCacheDataDescription() {
		return null;
	}
}