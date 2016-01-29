package com.yxl.util.redis.adapter.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClusterCommand;

import com.yxl.util.io.SerializeUtil;
import com.yxl.util.redis.JedisUtil;
import com.yxl.util.redis.adapter.JedisAdapter;

/**
 * Jedis适配器集群实现
 * @createTime 2015年10月26日 上午11:01:49 
 * @author yxl
 */
public class JedisAdapterClusterImpl implements JedisAdapter {

	private static Logger logger = LoggerFactory.getLogger(JedisAdapterClusterImpl.class);
	
	/** 集群模式redis連接，此連接不關閉，需要輪詢每個redis服務器時使用，避免建立連接花費過長的時間 */
	protected static List<Jedis> clusterConnections = new ArrayList<Jedis>();
	
	/**
	 * 初始化
	 * @date 2015年11月24日 下午3:47:31 
	 * @author yxl
	 */
	public JedisAdapterClusterImpl() throws Exception{
		boolean connected = false;//是否连接成功
		for (HostAndPort hp : JedisUtil.redisClusterNodes) {
			try {
				clusterConnections.add(JedisUtil.connectionHandler.getConnectionFromNode(hp));
				connected = true;
			} catch (Exception e) {
				logger.error("redis 服务器连接超时：" + hp);
			}
		}
		if(connected==false) throw new Exception("建立redis集群连接失败");
	}
	
	/**
	 * 获取集群环境的所有redis连接
	 * @date 2015年11月24日 下午3:28:28 
	 * @author yxl
	 */
	protected List<Jedis> getConnections(){
		return clusterConnections;
	}
	
	/**
	 * 设置缓存
	 * @param key 关键字
	 * @param value 可以把任何对象存放到缓存中
	 * @param timeout 超时时间，单位秒
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	@Override
	public String set(final String key, final Object value, final int timeout) throws UnsupportedEncodingException {
		final byte[] keyByte = key.getBytes(JedisUtil.charsert);
		return new JedisClusterCommand<String>(JedisUtil.connectionHandler, JedisUtil.maxRedirections) {
			@Override
			public String execute(Jedis connection) {
				String result = connection.set(keyByte, SerializeUtil.serialize(value));
				if (timeout > 0) connection.expire(keyByte, timeout);
				return result;
			}
		}.run(key);
	}
	
	/**
	 * 设置缓存过期时间
	 * @param key 关键字
	 * @param timeout 超时时间，单位秒
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	@Override
	public void setTimeout(final String key, final int timeout) throws UnsupportedEncodingException {
		final byte[] keyByte = key.getBytes(JedisUtil.charsert);
		new JedisClusterCommand<String>(JedisUtil.connectionHandler, JedisUtil.maxRedirections) {
			@Override
			public String execute(Jedis connection) {
				if (timeout > 0) connection.expire(keyByte, timeout);
				return "";
			}
		}.run(key);
	}

	/**
	 * get缓存
	 * @param key 关键字
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	@Override
	public Object get(final String key) throws UnsupportedEncodingException {
		final byte[] keyByte = key.getBytes(JedisUtil.charsert);
		return new JedisClusterCommand<Object>(JedisUtil.connectionHandler, JedisUtil.maxRedirections) {
			@Override
			public Object execute(Jedis connection) {
				return SerializeUtil.unserialize(connection.get(keyByte));
			}
		}.run(key);
	}

	/**
	 * delete缓存
	 * @param key 关键字，可以使用*匹配
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public String delete(final String key) throws UnsupportedEncodingException {
		final byte[] keyByte = key.getBytes(JedisUtil.charsert);
		return new JedisClusterCommand<String>(JedisUtil.connectionHandler, JedisUtil.maxRedirections) {
			@Override
			public String execute(Jedis connection) {
				return connection.del(keyByte).toString();
			}
		}.run(key);
	}
	
	/**
	 * 根据条件查询key
	 * @param key 关键字，可以使用*匹配
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	@Override
	public Set<byte[]> getKeys(final String key) throws UnsupportedEncodingException {
		final byte[] keyByte = key.getBytes(JedisUtil.charsert);
		return new JedisClusterCommand<Set<byte[]>>(JedisUtil.connectionHandler, JedisUtil.maxRedirections) {
			@Override
			public Set<byte[]> execute(Jedis connection) {
				Set<byte[]> result = new HashSet<byte[]>();
				
				for(Jedis jedis : getConnections()) result.addAll(jedis.keys(keyByte));
				//去重
				Set<String> resultStrSet = new HashSet<String>();
				for(byte[] resultByte : result) resultStrSet.add(new String(resultByte));
				result.clear();
				for(String resultStr : resultStrSet) result.add(resultStr.getBytes());
				return result;
			}
		}.run(key);
	}

	/**
	 * 清除redis所有缓存
	 * 
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	@Override
	public void flushAll() throws Exception {
		new JedisClusterCommand<String>(JedisUtil.connectionHandler, JedisUtil.maxRedirections) {
			@Override
			public String execute(Jedis connection) {
				for(Jedis jedis : getConnections()) jedis.flushAll();
				return null;
			}
		}.run("flushAll");
	}
}