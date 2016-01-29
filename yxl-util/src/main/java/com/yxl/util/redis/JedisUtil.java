package com.yxl.util.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.yxl.util.math.NumberUtil;
import com.yxl.util.redis.adapter.JedisAdapter;
import com.yxl.util.redis.adapter.impl.JedisAdapterClusterImpl;
import com.yxl.util.redis.adapter.impl.JedisAdapterImpl;
import com.yxl.util.resource.PropertiesUtil;

/**
 *     ┏┓　　　┏┓
 *   ┏┛┻━━━┛┻┓
 *   ┃　　　　　　　┃ 　
 *   ┃　　　━　　　┃
 *   ┃　┳┛　┗┳　┃
 *   ┃　　　　　　　┃
 *   ┃　　　┻　　　┃
 *   ┃　　　　　　　┃
 *   ┗━┓　　　┏━┛
 *   　　┃　　　┃神兽保佑
 *   　　┃　　　┃永无BUG！
 *   　　┃　　　┗━━━┓
 *   　　┃　　　　　　　┣┓
 *   　　┃　　　　　　　┏┛
 *   　　┗┓┓┏━┳┓┏┛
 *   　　　┃┫┫　┃┫┫
 *   　　　┗┻┛　┗┻┛
 *
 * Jedis工具类 jedis最低版本2.7.2
 * @author 杨雪令
 */
public class JedisUtil {

	private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);

	/** redis普通连接 */
	public static JedisPool jedisPool;
	/** redis普通连接数据库索引 */
	public static int dbIndex = 1;
	/** 缓存过期时间，单位秒，大于0有效 */
	public static int expireTime = 180;
	/** CUD不刷新缓存（redis.cud.not.flush.key配置关键字，英文逗号隔开，如果要清理的key包含这些关键字， 则不清理） */
	public static List<String[]> cudNotFlushList = new ArrayList<String[]>();
	/** key 前缀，开发，测试，部署阶段使用不同前缀，避免缓存数据冲突 */
	public static String keyPrefix = "Test:";
	/** 编码 */
	public static final String charsert = "utf-8";
	/** jedis 适配器*/
	public static JedisAdapter jedisAdapter = null;
	/** 集群模式redis地址前缀 */
	public static final String redisClusterAddrPrefix = "redis.cluster.addr";
	/** 进行CUD操作时，不刷新缓存，配置前缀 */
	public static final String cudNotFlushKeyPrefix = "redis.cud.not.flush.key";
	/** 集群模式redis地址正则表达式 */
	public static Pattern redisClusterHostPatt = Pattern.compile("^.+[:]\\d{1,5}\\s*$");
	/** 集群模式redis节点 */
	public static Set<HostAndPort> redisClusterNodes = new HashSet<HostAndPort>();
	/** redis集群连接 */
	public static JedisClusterConnectionHandler connectionHandler;
	/** 集群模式如果一個連接失效，會自動嘗試使用其他連接， maxRedirections配置一個請求最多允許嘗試的次數*/
	public static int maxRedirections = 10;
	/** 最后一次连接redis服务器失败的时间 */
	public static long lastTimeConnectionError = 0;
	/** 连接是否存活 */
	public static boolean connectionAlive = false;
	/** 当连接redis服务器失败时，最少间隔多久才能重新尝试，单位秒 */
	public static int intervalConnectionError = 0;
	/** redis 配置文件 */
	public static Properties properties = null;
	/** 无效的缓存 */
	public static Set<String> invalidkeys= new HashSet<String>();
	/** 最多尝试连接次数 */
	final public static int maxConnectCount = 5;

	// 静态方式加载redis 配置文件
	static {
		//读取配置文件
		readConfig();
		//连接服务器
		if(connectionAlive==false) connectServer();
	}
	
	/**
	 * 读取配置文件
	 * @date 2015年12月15日 上午10:54:01 
	 * @author yxl
	 */
	public static void readConfig(){
		properties = PropertiesUtil.readProperties("redis.properties", JedisUtil.class);
		// 读取缓存配置参数
		keyPrefix = properties.getProperty("redis.key.prefix");
		if (keyPrefix != null && keyPrefix.toLowerCase().equals("auto")) keyPrefix = "TEST-REDIS-" + System.currentTimeMillis();
		maxRedirections = Integer.valueOf(properties.getProperty("redis.maxRedirections"));
		expireTime = Integer.valueOf(properties.getProperty("redis.expire.time"));
		intervalConnectionError = Integer.valueOf(properties.getProperty("interval.connection.error"));

		//读取cudNotFlushKey配置
		cudNotFlushList.clear();
		for (Object key : properties.keySet()) {
			if (!((String) key).startsWith(cudNotFlushKeyPrefix)) continue;
			cudNotFlushList.add(properties.get(key).toString().split(","));
		}
		
		//集群模式
		if (properties.getProperty("redis.cluster").equals("1")) {
			// 读取redis节点配置
			redisClusterNodes.clear();
			for (Object key : properties.keySet()) {
				if (!((String) key).startsWith(redisClusterAddrPrefix)) continue;
				String val = (String) properties.get(key);
				boolean isHost = redisClusterHostPatt.matcher(val).matches();
				if (!isHost) throw new IllegalArgumentException("redis 集群配置 ip 或 port 不合法");
				String[] hostStr = val.split(":");
				HostAndPort host = new HostAndPort(hostStr[0], Integer.valueOf(hostStr[1]));
				redisClusterNodes.add(host);
			}
		}
	}
	
	/**
	 * 连接服务器
	 * @date 2015年12月15日 上午10:54:01 
	 * @author yxl
	 */
	public synchronized static void connectServer(){
		if(connectionAlive) return;
		logger.info("连接redis server...");
		try{
			//集群模式
			if (properties.getProperty("redis.cluster").equals("1")) {
				//初始化连接池
				GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
				genericObjectPoolConfig.setMaxTotal(Integer.valueOf(properties.getProperty("redis.pool.maxTotal")));
				genericObjectPoolConfig.setMaxIdle(Integer.valueOf(properties.getProperty("redis.pool.maxIdle")));
				genericObjectPoolConfig.setMaxWaitMillis(Long.valueOf(properties.getProperty("redis.pool.maxWait")));
				//创建连接
				connectionHandler = new JedisSlotBasedConnectionHandler(redisClusterNodes, genericObjectPoolConfig, Integer.valueOf(properties.getProperty("redis.timeout")));
				jedisAdapter = new JedisAdapterClusterImpl();
			} else {
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxTotal(Integer.valueOf(properties.getProperty("redis.pool.maxTotal")));
				config.setMaxIdle(Integer.valueOf(properties.getProperty("redis.pool.maxIdle")));
				config.setMaxWaitMillis(Long.valueOf(properties.getProperty("redis.pool.maxWait")));
				if (properties.getProperty("redis.password").trim().length() > 0) jedisPool = new JedisPool(config, properties.getProperty("redis.host"), Integer.valueOf(properties.getProperty("redis.port")), Integer.valueOf(properties.getProperty("redis.timeout")), properties.getProperty("redis.password"));
				else jedisPool = new JedisPool(config, properties.getProperty("redis.host"), Integer.valueOf(properties.getProperty("redis.port")), Integer.valueOf(properties.getProperty("redis.timeout")));
				jedisAdapter = new JedisAdapterImpl();
			}
			deleteInvalidKey();
			connectionAlive = true;
			logger.info("连接redis server 成功");
		} catch(Exception e){
			connectionAlive = false;
			lastTimeConnectionError = System.currentTimeMillis();
			logger.error("连接redis server 失败", e);
		}
	}
	
	
	/**
	 * 连接服务器失败
	 * @date 2015年12月15日 上午10:59:35 
	 * @author yxl
	 */
	public static void connectServerFaild(Exception e) {
		lastTimeConnectionError = System.currentTimeMillis();
		connectionAlive = false;
		logger.error("警告：获取redis连接失败，请检查服务器配置", e);
	}
	
	
	/**
	 * 删除无效的缓存
	 * @date 2015年12月15日 上午10:59:35 
	 * @author yxl
	 */
	public static void deleteInvalidKey(){
		for(String key : new HashSet<String>(invalidkeys)){
			try {
				delete(key);
				invalidkeys.remove(key);
			} catch (Exception e) {
				logger.error("删除无效的redis缓存出错：" + key, e);
			}
		}
	}

	/**
	 * 检查redis服务器连接状态
	 * @return 连接正常：true，连接异常：false
	 * @date 2015年11月8日 下午12:17:53 
	 * @author yxl
	 */
	public static boolean checkConnection() {
		if(connectionAlive) return true;
		//当前时间 - 最后一次连接redis服务器失败的时间 < 当连接redis服务器失败时，最少间隔多久才能重新尝试，则不允许和redis服务器建立连接
		long waitTime = (intervalConnectionError * 1000 - (System.currentTimeMillis() - lastTimeConnectionError)) / 1000;
		if (waitTime>0) {
			logger.warn("警告：当前操作未使用redis缓存，获取redis连接失败，请检查服务器配置，系统将在" + waitTime + "秒后重新尝试连接...");
			return false;
		}
		//重新建立连接
		connectServer();
		return true;
	}
	
	/**
	 * 设置缓存
	 * @param key 缓存key
	 * @param value 缓存value，可以传任何对象
	 * @param timeout 超时时间，单位秒
	 * @param tryCount 连接失败重试次数，不用传此参数
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static void set(final String key, final Object value, int timeout, int... tryCount) {
		if (!checkConnection()) return;
		try {
			jedisAdapter.set(JedisUtil.keyPrefix + key, value, timeout);
		} catch (JedisConnectionException e) {
			int tryCounts = 1;
			if(tryCount.length>0) tryCounts = tryCount[0];
			if(tryCounts > maxConnectCount) connectServerFaild(e);
			else set(key, value, timeout, tryCounts+1);
		} catch (Exception e) {
			logger.error("设置redis缓存失败[key : "+ key +"]" + "[value : "+ value +"]" + "[timeout : "+ timeout +"]", e);
		}
	}
	
	/**
	 * 设置缓存过期时间
	 * @param key 缓存key
	 * @param timeout 超时时间，单位秒
	 * @param tryCount 连接失败重试次数，不用传此参数
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static void setTimeout(final String key, int timeout, int... tryCount) {
		if (!checkConnection()) return;
		try {
			jedisAdapter.setTimeout(JedisUtil.keyPrefix + key, timeout);
		} catch (JedisConnectionException e) {
			int tryCounts = 1;
			if(tryCount.length>0) tryCounts = tryCount[0];
			if(tryCounts > maxConnectCount) connectServerFaild(e);
			else setTimeout(key, timeout, tryCounts+1);
		} catch (Exception e) {
			logger.error("设置redis缓存过期时间失败[key : "+ key +"]" + "[timeout : "+ timeout +"]", e);
		}
	}

	/**
	 * 设置缓存(使用配置文件中的缓存超时时间)
	 * @param key 缓存key
	 * @param value 缓存value，可以传任何对象
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static void set(final String key, final Object value) {
		if (!checkConnection()) return;
		int timeout = expireTime;
		//CUD操作如果不清除缓存，那么根据配置的缓存过期时间来做清理
		for (String[] cudNotFlushKey : cudNotFlushList) {
			if (key.contains(cudNotFlushKey[0])) {
				//如果配置了缓存过期时间，则使用
				if(cudNotFlushKey.length==2) timeout = NumberUtil.parseInt(cudNotFlushKey[1]);
				break;
			}
		}
		set(key, value, timeout);
	}
	
	/**
	 * 获取缓存
	 * @param key 缓存key
	 * @param tryCount 连接失败重试次数，不用传此参数
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static Object get(final String key, int... tryCount) {
		if (!checkConnection()) return null;
		try {
			return jedisAdapter.get(JedisUtil.keyPrefix + key);
		} catch (JedisConnectionException e) {
			int tryCounts = 1;
			if(tryCount.length>0) tryCounts = tryCount[0];
			if(tryCounts > maxConnectCount) connectServerFaild(e);
			else return get(key, tryCounts+1);
		} catch (Exception e) {
			logger.error("从redis获取缓存失败[key : "+ key +"]", e);
		}
		return null;
	}
	
	/**
	 * delete缓存
	 * 可以使用*匹配
	 * @param key 缓存key
	 * @param tryCount 连接失败重试次数，不用传此参数
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static void delete(String key, int... tryCount) {
		if (!checkConnection()) return;
		// 根据规则匹配关键字不主动删除缓存
		for (String[] cudNotFlushKey : cudNotFlushList) {
			if (key.contains(cudNotFlushKey[0])) {
				logger.info(cudNotFlushKey[0] + " don't need clear on CUD...");
				return;
			}
		}

		try {
			//自动匹配key
			if (key.contains("*")) {
				for (byte[] childKey : getKeys(key))
					delete(new String(childKey));
			} else {
				if (!key.startsWith(JedisUtil.keyPrefix)) key = JedisUtil.keyPrefix + key;
				logger.info("delete redis by key：" + key);
				jedisAdapter.delete(key);
			}
		} catch (JedisConnectionException e) {
			int tryCounts = 1;
			if(tryCount.length>0) tryCounts = tryCount[0];
			if(tryCounts > maxConnectCount) {
				connectServerFaild(e);
				invalidkeys.add(key);
			} else delete(key, tryCounts+1);
		} catch (Exception e) {
			logger.error("删除redis缓存失败[key : "+ key +"]", e);
		}
	}
	
	/**
	 * 根据条件查询key
	 * @param key 缓存key
	 * @param tryCount 连接失败重试次数，不用传此参数
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static Set<byte[]> getKeys(final String key, int... tryCount) {
		if (!checkConnection()) return new HashSet<byte[]>();
		try {
			return jedisAdapter.getKeys(JedisUtil.keyPrefix + key);
		} catch (JedisConnectionException e) {
			int tryCounts = 1;
			if(tryCount.length>0) tryCounts = tryCount[0];
			if(tryCounts > maxConnectCount) connectServerFaild(e);
			else return getKeys(key, tryCounts+1);
		} catch (Exception e) {
			logger.error("查询redis keys失败[key : "+ key +"]", e);
		}
		return new HashSet<byte[]>();
	}


	/**
	 * 清除redis所有缓存
	 * @param tryCount 连接失败重试次数，不用传此参数
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static void flushAll(int... tryCount) {
		if (!checkConnection()) return;
		try {
			jedisAdapter.flushAll();
		} catch (JedisConnectionException e) {
			int tryCounts = 1;
			if(tryCount.length>0) tryCounts = tryCount[0];
			if(tryCounts > maxConnectCount) connectServerFaild(e);
			else flushAll(tryCounts+1);
		} catch (Exception e) {
			logger.error("redis flushAll失败", e);
		}
	}

	/**
	 * 清除项目所有缓存
	 * @param tryCount 连接失败重试次数，不用传此参数
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public static void clearAllOfProject(int... tryCount) {
		if (!checkConnection()) return;
		try {
			delete("*");
		} catch (JedisConnectionException e) {
			int tryCounts = 1;
			if(tryCount.length>0) tryCounts = tryCount[0];
			if(tryCounts > maxConnectCount) {
				connectServerFaild(e);
				invalidkeys.add("*");
			} else clearAllOfProject(tryCounts+1);
		} catch (Exception e) {
			logger.error("redis clearAllOfProject失败", e);
		}
	}

	/**
	 * test
	 * @date 2015年10月26日 上午10:50:29 
	 * @author yxl
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {

		Map map = new HashedMap();
		map.put("tss", "ss");
		JedisUtil.set("JedisUtil:Test:Map", map);
		JedisUtil.set("JedisUtil:Test:com.hx.dazibo.video.mode.VideoMapper12312", "1");
		JedisUtil.set("JedisUtil:Test:com.hx.dazibo.advert.mode.AdvertPlatformMapper", "2");
		for (int i = 1; i <= 50; i++) {
			JedisUtil.set("com.t_test." + i, "t" + i + ".data...");
		}

		//Map newMap = (Map) JedisUtil.get("JedisUtil:Test:Map");
		//System.out.println(newMap.get("t2"));

		Set<byte[]> keys = getKeys("*.t_test.*");
		for (byte[] key : keys) {
			System.out.println(new String(key));
		}
		System.out.println("total: " + keys.size());

		delete("com.t*");

		long start = System.currentTimeMillis();
		for (int i = 1; i <= 100; i++) {
			keys = getKeys("com.t*");
			for (byte[] key : keys) {
				System.out.println(new String(key));
			}
		}
		System.out.println("total: " + keys.size());
		System.out.println("use " + (System.currentTimeMillis() - start) + " ms...");
	}
}