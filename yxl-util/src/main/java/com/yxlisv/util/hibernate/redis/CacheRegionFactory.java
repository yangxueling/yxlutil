package com.yxlisv.util.hibernate.redis;

import java.util.Properties;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Redis hibernate 缓存提供工厂</p>
 * <p>一个Hibernate SessionFactory创建一个实例</p>
 * @author 杨雪令
 * @time 2016年3月21日下午5:24:03
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CacheRegionFactory implements RegionFactory {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void start(SessionFactoryOptions settings, Properties properties) throws CacheException {
		logger.info("Redis CacheRegionFactory start...");
	}

	@Override
	public void stop() {
		logger.info("Redis CacheRegionFactory stop...");
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		return false;
	}

	@Override
	public AccessType getDefaultAccessType() {
		return AccessType.NONSTRICT_READ_WRITE;
	}

	@Override
	public long nextTimestamp() {
		return System.currentTimeMillis();
	}

	@Override
	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		return new RedisEntityRegion(regionName);
	}

	@Override
	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		return new RedisNaturalIdRegion(regionName);
	}

	@Override
	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		return new RedisCollectionRegion(regionName);
	}

	@Override
	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
		return new RedisQueryResultsRegion(regionName);
	}

	@Override
	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
		return new RedisTimestampsRegion(regionName);
	}
}