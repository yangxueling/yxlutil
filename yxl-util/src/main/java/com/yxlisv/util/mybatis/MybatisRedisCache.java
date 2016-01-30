package com.yxlisv.util.mybatis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.db.SqlUtil;
import com.yxlisv.util.redis.JedisUtil;

/**
 <pre>
      ┏┓　　　┏┓
    ┏┛┻━━━┛┻┓
    ┃　　　　　　　┃ 　
    ┃　　　━　　　┃
    ┃　┳┛　┗┳　┃
    ┃　　　　　　　┃
    ┃　　　┻　　　┃
    ┃　　　　　　　┃
    ┗━┓　　　┏━┛
    　　┃　　　┃神兽保佑
    　　┃　　　┃永无BUG！
    　　┃　　　┗━━━┓
    　　┃　　　　　　　┣┓
    　　┃　　　　　　　┏┛
    　　┗┓┓┏━┳┓┏┛
    　　　┃┫┫　┃┫┫
    　　　┗┻┛　┗┻┛
    </pre>
 *
 * Mybatis Redis 缓存实现
 * namespace的命名规则：如namespace为：org.*.*.HotUserMapper / org.*.*.HotUser，那么表名为hot_user / t_hot_user
 * 
 * @createTime 2015年10月15日 下午3:53:07
 * @author yxl
 */
public class MybatisRedisCache implements Cache {

	private static Logger logger = LoggerFactory.getLogger(MybatisRedisCache.class);
	/** The ReadWriteLock. */
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	/** ID （Mapper） */
	private String id;
	/** 表名前缀 */
	private static String tableNamePrefix = "T_";
	/** 表名 */
	private String tableName;
	/** 查询方法包含的表，key：查询方法名，value：表名（.talbe1.table2.） */
	private Map<String, String> methodTableMap = new HashMap<String, String>();
	
	/**
	 * 构造方法
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	public MybatisRedisCache(final String id) {
		if (id == null) throw new IllegalArgumentException("初始化MybatisRedisCache必须传入Mapper ID");
		this.id = id;
		tableName = id;
		tableName = tableName.substring(tableName.lastIndexOf(".")+1);
		tableName = tableName.replaceAll("(?i)Mapper", "");
		tableName = tableName.replaceAll("(\\B[A-Z])", "_$0").toUpperCase();
	}
	
	/**
	 * 组合key
	 * namespace.method.table1.table2.table3...md5
	 * 
	 * @date 2015年10月15日 下午6:22:57
	 * @author yxl
	 */
	private String getCacheKey(Object key) {
		
		//截取方法名
		String methodStr = key.toString();
		methodStr = methodStr.substring(methodStr.indexOf(id) + id.length());
		methodStr = methodStr.substring(0, methodStr.indexOf(":"));
		
		StringBuilder sb = new StringBuilder();
		//拼装key
		sb.append(id + methodStr);
		//拼装表名
		if(methodTableMap.containsKey(methodStr)) sb.append(methodTableMap.get(methodStr));
		else {
			String methodTables = SqlUtil.getTableNames(key.toString(), tableNamePrefix).toUpperCase();
			sb.append(methodTables);
			methodTableMap.put(methodStr, methodTables);
		}
		//key对应的md5
		sb.append(DigestUtils.md5Hex(String.valueOf(key)));
		return sb.toString();
	}
	
	/**
	 * 获取ID（Mapper）
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * 获取缓存数量
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	@Override
	public int getSize() {
		int result = 0;
		String cacheKey = id + "*";
		Set<byte[]> keys = JedisUtil.getKeys(cacheKey);
		if (null != keys && !keys.isEmpty()) result = keys.size();
		logger.info("MybatisRedisCache getSize：" + cacheKey);
		return result;
	}

	/**
	 * 添加缓存
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	@Override
	public void putObject(Object key, Object value) {
		String cacheKey = getCacheKey(key);
		logger.info("MybatisRedisCache putObject：" + cacheKey);
		JedisUtil.set(cacheKey, value);
	}

	/**
	 * 获取缓存
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	@Override
	public Object getObject(Object key) {
		String cacheKey = getCacheKey(key);
		logger.info("MybatisRedisCache getObject：" + cacheKey);
		return JedisUtil.get(cacheKey);
	}

	/**
	 * 移除缓存
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	@Override
	public Object removeObject(Object key) {
		String cacheKey = getCacheKey(key);
		logger.info("MybatisRedisCache removeObject：" + cacheKey);
		JedisUtil.delete(cacheKey);
		return null;
	}

	/**
	 * 清理缓存（CUD）
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	@Override
	public void clear() {
		String cacheKey = "*." + tableNamePrefix + tableName + ".*";
		logger.info("MybatisRedisCache clear：" + cacheKey);
		JedisUtil.delete(cacheKey);
	}

	/**
	 * 加锁
	 * 
	 * @date 2015年10月15日 下午6:23:24
	 * @author yxl
	 */
	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}
	
	
	/**
	 * 测试
	 */
	public static void main(String[] args) {
		MybatisRedisCache mrc = new MybatisRedisCache("org.*.*.hotTooUserMapper");
		String key = "org.*.*.UserMapper.testQuery: selece *,(select id from J_t1 jt1, user, t_User, j_t2 right out join j_t3 jt3 on jt2.id=jt3.id left join on j_t4 jt4 on jt4.id=jt3.id from tt1, J_t1  , tt2 as t2 , tt3 , tt4 left join tt5 on t2.ds=sd where 1=1 and f2.name=f3.name or f4.t=f5.t and id in (select id from w_t1,w_t2 right out join w_t3)";
		//key = "org.*.*.UserMapper.testQuery: select id from J_t1";
		long start = System.currentTimeMillis();
		System.out.println(mrc.getCacheKey(key));
		System.out.println(System.currentTimeMillis()-start + "ms");
		
		start = System.currentTimeMillis();
		for(int i=1; i<=10000; i++) mrc.getCacheKey(key);
		System.out.println(mrc.getCacheKey(key));
		System.out.println(System.currentTimeMillis()-start + "ms");
	}
}