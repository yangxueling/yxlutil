package com.yxl.util.zookeeper.lock;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxl.util.resource.PropertiesUtil;

/**
 * 基于Zookeeper的分布式锁
 * 使用时，在代码处先获取锁，使用完之后一定要归还锁
 * 用try  finally包装
    <dependency>
			<groupId>org.apache.curator</groupId>
			<artifactId>curator-framework</artifactId>
			<version>2.4.2</version>
			<exclusions>
				<exclusion>
					<groupId>org.slf4j</groupId>
					<artifactId>slf4j-log4j12</artifactId>
				</exclusion>
				<exclusion>
					<groupId>log4j</groupId>
					<artifactId>log4j</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.apache.curator</groupId>
			<artifactId>curator-recipes</artifactId>
			<version>2.4.2</version>
		</dependency>
 * @createTime 2015年11月19日 上午11:06:18 
 * @author yxl
 */
public class Lock {

	private static Logger logger = LoggerFactory.getLogger(Lock.class);

	/** zookeeperLock 配置文件 */
	private static Properties properties = PropertiesUtil.readProperties("zookeeper.lock.properties", Lock.class);
	/** zookeeper 服务器地址，如：192.168.32.103:2181,192.168.32.104:2181 */
	public static String serverUrl;
	/** Zookeeper framework-style client */
	public static CuratorFramework curatorFramework = null;
	/** 连接失败最大重试次数 */
	public static int connectionRetriesMax;
	/** 连接重试间隔时间，单位毫秒 */
	public static int connectionRetriesInterval;
	/** 获取锁等待时间，单位毫秒 */
	public static int getlockWaitTime;
	/** lock节点根目录 */
	final static private String lockRootPath = "/lock";

	//初始化
	static {
		serverUrl = properties.getProperty("zookeeper.address");
		connectionRetriesMax = Integer.valueOf(properties.getProperty("connection.retries.max"));
		connectionRetriesInterval = Integer.valueOf(properties.getProperty("connection.retries.interval"));
		getlockWaitTime = Integer.valueOf(properties.getProperty("getlock.wait.time"));
		curatorFramework = CuratorFrameworkFactory.newClient(serverUrl, new ExponentialBackoffRetry(connectionRetriesInterval, connectionRetriesMax));
		curatorFramework.start();
	}

	/**
	 * 获取锁
	 * @param key 钥匙
	 * @date 2015年11月19日 上午11:49:08 
	 * @author yxl
	 * @return 
	 */
	public static InterProcessMutex getLock(String key) throws Exception {
		return getLock(key, getlockWaitTime);
	}

	/**
	 * 获取锁
	 * @param key 钥匙
	 * @param getlockWaitTime 获取锁等待时间，单位毫秒
	 * @return 如果获取到锁返回InterProcessMutex对象，如果没有返回null
	 * @date 2015年11月19日 上午11:49:08 
	 * @author yxl
	 */
	public static InterProcessMutex getLock(String key, long getlockWaitTime) throws Exception {
		String path = keyToPath(key);
		logger.info("get lock : " + path);
		//不同的InterProcessMutex实例获取锁互斥，所以每次获取锁重新生成一个实例
		InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework, path);
		if (!interProcessMutex.acquire(getlockWaitTime, TimeUnit.MILLISECONDS)) return null;
		return interProcessMutex;
	}

	/**
	 * 归还锁
	 * @param interProcessMutex 锁工具
	 * @date 2015年11月19日 上午11:49:08 
	 * @author yxl
	 */
	public static void release(InterProcessMutex interProcessMutex) {
		try{
			logger.info("release lock");
			if(interProcessMutex!=null) interProcessMutex.release();
		} catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * key转换为path
	 * @date 2015年11月19日 上午11:49:08 
	 * @author yxl
	 */
	public static String keyToPath(String key) {
		if (!key.startsWith("/")) key = "/" + key;
		if (key.endsWith("/")) key = key.substring(0, key.length() - 1);
		return lockRootPath + key;
	}
	
	public static void main(String[] args) throws Exception {
		
		InterProcessMutex interProcessMutex = null;
		String key = "testKey";
		try{
			interProcessMutex = getLock(key);
			if(interProcessMutex!=null) System.out.println("I get first lock");
			else System.out.println("I don't get first lock");
		} finally{
			release(interProcessMutex);
			System.out.println("release lock...");
		}
		
		try{
			interProcessMutex = getLock(key);
			if(interProcessMutex!=null) System.out.println("I get second lock");
			else System.out.println("I don't get second lock");
		} finally{
			release(interProcessMutex);
			System.out.println("release lock...");
		}
	}
}