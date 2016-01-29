package com.yxlisv.util.zookeeper.pool;

import java.util.Properties;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.yxlisv.util.resource.PropertiesUtil;
import com.yxlisv.util.zookeeper.ZookeeperConnection;

/**
 * Zookeeper连接池
 * 创建一个Zookeeper连接，开销比较大，当个连接不能充分发挥多核CPU的优势，所以使用连接池
 * @createTime 2016年1月25日 下午4:50:09 
 * @author yxl
 */
public class ZookeeperPool {
	
	/** zookeeper 配置文件 */
	private static Properties properties = PropertiesUtil.readProperties("zookeeper.pool.properties", ZookeeperPool.class);
	/** 连接池配置 */
	private static GenericObjectPoolConfig genericObjectPoolConfig = null;
	/** 连接池 */
	private static GenericObjectPool<ZookeeperConnection> internalPool = null;
	
	static {
		connectServer();
	}
	
	/**
	 * 初始化连接
	 * @date 2016年1月25日 下午5:12:43 
	 * @author yxl
	 */
	protected synchronized static void connectServer(){
		genericObjectPoolConfig = new GenericObjectPoolConfig();
		genericObjectPoolConfig.setMaxTotal(Integer.valueOf(properties.getProperty("zookeeper.pool.maxTotal")));
		genericObjectPoolConfig.setMaxIdle(Integer.valueOf(properties.getProperty("zookeeper.pool.maxIdle")));
		genericObjectPoolConfig.setMaxWaitMillis(Long.valueOf(properties.getProperty("zookeeper.pool.maxWait")));
		
		internalPool = new GenericObjectPool<ZookeeperConnection>(new ZookeeperPoolFactory(), genericObjectPoolConfig);
	}
	
	/**
	 * 获取一个连接对象
	 * @date 2016年1月25日 下午5:11:51 
	 * @author yxl
	 */
	public static ZookeeperConnection getConnection() throws Exception{
		return internalPool.borrowObject();
	}
	
	/**
	 * 归还连接
	 * @date 2016年1月25日 下午5:25:33 
	 * @author yxl
	 */
	public static void returnObject(ZookeeperConnection zookeeperConnection){
		internalPool.returnObject(zookeeperConnection);
	}

	
	public static void main(String[] args) throws Exception {
		for(int i=0; i<20; i++){
			ZookeeperConnection zookeeperConnection = null;
			try{
				long startTime = System.currentTimeMillis();
				zookeeperConnection = ZookeeperPool.getConnection();
				System.out.println("create use : " + (System.currentTimeMillis() - startTime));
				
				startTime = System.currentTimeMillis();
				zookeeperConnection.createPath("/testnode1", "hhh");
				System.out.println("create path use : " + (System.currentTimeMillis() - startTime));
				
				startTime = System.currentTimeMillis();
				System.out.println("data : " + zookeeperConnection.getData("/testnode1"));
				System.out.println("get Data use : " + (System.currentTimeMillis() - startTime));
				
				startTime = System.currentTimeMillis();
				zookeeperConnection.delete("/testnode1");
				System.out.println("delete use : " + (System.currentTimeMillis() - startTime));
				
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n\n\n\n\n\n");
			} finally{
				if(zookeeperConnection!=null) zookeeperConnection.close();
			}
		}
	}
}
