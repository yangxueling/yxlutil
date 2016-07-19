package com.yxlisv.util.hibernate.redis;

import org.hibernate.cache.spi.QueryResultsRegion;

/**
 * <p>Redis 查询结果区域包装器</p>
 * @author 杨雪令
 * @time 2016年3月22日上午11:44:37
 * @version 1.0
 */
public class RedisQueryResultsRegion extends RedisGeneralDataRegion implements QueryResultsRegion {

	public RedisQueryResultsRegion(String regionName) {
		super(regionName);
	}

}