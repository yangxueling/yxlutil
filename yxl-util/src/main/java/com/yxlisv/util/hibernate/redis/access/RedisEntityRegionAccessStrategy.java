package com.yxlisv.util.hibernate.redis.access;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.hibernate.redis.util.KeyUtil;
import com.yxlisv.util.redis.JedisUtil;

/**
 * <p>实体类访问策略</p>
 * @author 杨雪令
 * @time 2016年3月22日上午10:54:12
 * @version 1.0
 */
@SuppressWarnings("unused")
public class RedisEntityRegionAccessStrategy implements EntityRegionAccessStrategy {
	
	private static Logger logger = LoggerFactory.getLogger(RedisEntityRegionAccessStrategy.class);
	
	//访问策略类型
	private AccessType accessType;
	//实体类区域包装器
	private EntityRegion entityRegion;
	//实体类的region名称
	private String regionName;

	/**
	 * <p>构造方法</p>
	 * @param regionName 实体类的region名称
	 * @param accessType 访问策略类型
	 * @param entityRegion 实体类区域包装器
	 * @author 杨雪令
	 * @time 2016年3月22日上午10:56:30
	 * @version 1.0
	 */
	public RedisEntityRegionAccessStrategy(String regionName, AccessType accessType, EntityRegion entityRegion) {
		this.accessType = accessType;
		this.entityRegion = entityRegion;
		this.regionName = regionName;
	}

	@Override
	public Object get(SessionImplementor session, Object key, long txTimestamp) throws CacheException {
		logger.debug("Redis get entry [regionName="+ regionName +", key="+ key +"]");
		return JedisUtil.get(KeyUtil.formatKey(regionName, key));
	}

	@Override
	public boolean putFromLoad(SessionImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
		putFromLoad(session, key, value, txTimestamp, version, false);
		return true;
	}

	@Override
	public boolean putFromLoad(SessionImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
		logger.debug("Redis put entry [regionName="+ regionName +", key="+ key +", value="+ value +"]");
		JedisUtil.set(KeyUtil.formatKey(regionName, key), value);
		return true;
	}

	@Override
	public SoftLock lockItem(SessionImplementor session, Object key, Object version) throws CacheException {
		return null;
	}

	@Override
	public SoftLock lockRegion() throws CacheException {
		return null;
	}

	@Override
	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
		logger.debug("unlockItem : " + key);
	}

	@Override
	public void unlockRegion(SoftLock lock) throws CacheException {
		logger.debug("unlockRegion");
	}

	@Override
	public void remove(SessionImplementor session, Object key) throws CacheException {
		logger.debug("Redis delete entry [regionName="+ regionName +", key="+ key +"]");
		JedisUtil.delete(KeyUtil.formatKey(regionName, key));
	}

	@Override
	public void removeAll() throws CacheException {
		logger.debug("Redis set entry removeAll : " + regionName + " - " + this.getClass());
		JedisUtil.delete(KeyUtil.keyPrefix + regionName + "*");
	}

	@Override
	public void evict(Object key) throws CacheException {
		logger.debug("Redis set evict [key="+ key +"]");
		JedisUtil.delete(KeyUtil.formatKey(regionName, key));
	}

	@Override
	public void evictAll() throws CacheException {
		logger.debug("Redis set entry evictAll : " + regionName + " - " + this.getClass());
		JedisUtil.delete(KeyUtil.keyPrefix + regionName + "*");
	}

	/**
	 * <p>生成实体类缓存Key</p>
	 * @param id 实体类ID
	 * @param persister 实体类状态
	 * @param factory SessionFactory
	 * @param tenantIdentifier
	 * @return Object 实体类缓存Key
	 * @author 杨雪令
	 * @time 2016年3月22日上午11:06:06
	 * @version 1.0
	 */
	@Override
	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
		return persister.getEntityName() + "." + id;
	}

	@Override
	public Object getCacheKeyId(Object cacheKey) {
		return cacheKey;
	}

	@Override
	public EntityRegion getRegion() {
		return entityRegion;
	}

	@Override
	public boolean insert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
		return true;
	}

	@Override
	public boolean afterInsert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
		logger.debug("Redis put entry by insert [key="+ key +", value="+ value +"]");
		JedisUtil.set(KeyUtil.formatKey(regionName, key), value);
		return true;
	}

	@Override
	public boolean update(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
		return true;
	}

	@Override
	public boolean afterUpdate(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
		logger.debug("Redis put entry by update [key="+ key +", value="+ value +"]");
		JedisUtil.set(KeyUtil.formatKey(regionName, key), value);
		return true;
	}
}