package com.yxlisv.util.zookeeper;

import java.io.IOException;
import java.util.Properties;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.resource.PropertiesUtil;
import com.yxlisv.util.zookeeper.pool.ZookeeperPool;

/**
 * Zookeeper 连接
 * <dependency>
			<groupId>org.apache.zookeeper</groupId>
			<artifactId>zookeeper</artifactId>
			<version>3.4.6</version>
		</dependency>
		<dependency>
 * @createTime 2016年1月25日 下午3:24:10 
 * @author yxl
 */
public class ZookeeperConnection {

	private static Logger logger = LoggerFactory.getLogger(ZookeeperConnection.class);

	/** zookeeper 配置文件 */
	private static Properties properties = PropertiesUtil.readProperties("zookeeper.properties", ZookeeperConnection.class);
	/** zookeeper 服务器 */
	private ZooKeeper zooKeeper = null;
	
	/**
	 * 构造方法
	 *	@throws IOException
	 * @date 2016年1月25日 下午4:39:39 
	 * @author yxl
	 */
	public ZookeeperConnection() throws IOException{
		zooKeeper = new ZooKeeper(properties.getProperty("zookeeper.address"), NumberUtil.parseInt(properties.getProperty("session.timeout")), new Watcher() {
			// 监控所有被触发的事件
			public void process(WatchedEvent event) {
				logger.info("ZooKeeper Watcher : " + event.getPath() + " / " + event.getType().name());
			}
		});
	}
	
	/**
	 * 构造方法
	 * @param watcher 监听器
	 *	@throws IOException
	 * @date 2016年1月25日 下午4:39:39 
	 * @author yxl
	 */
	public ZookeeperConnection(Watcher watcher) throws IOException{
		zooKeeper = new ZooKeeper(properties.getProperty("zookeeper.address"), NumberUtil.parseInt(properties.getProperty("session.timeout")), watcher);
	}
	 
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(zooKeeper!=null) {
			logger.info("close zooKeeper : " + zooKeeper.getSessionId());
			try {
				zooKeeper.close();
			} catch (InterruptedException e) {
				logger.error("zooKeeper close error", e);
			}
		}
	}
	

	/**
	 * 关闭连接
	 * @date 2016年1月25日 下午5:42:41 
	 * @author yxl
	 */
	public void close(){
		ZookeeperPool.returnObject(this);
	}

	/**
	 * 创建一个Path
	 * @param path 节点路径
	 * @param data 节点数据
	 * @date 2016年1月25日 下午3:37:46 
	 * @author yxl
	 */
	public void createPath(String path, String data) throws Exception{
		zooKeeper.create(path, data.getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	
	/**
	 * 读取节点数据
	 * @param path 节点路径
	 * @return 节点数据
	 * @date 2016年1月25日 下午3:37:46 
	 * @author yxl
	 */
	public String getData(String path) throws Exception{
		return new String(zooKeeper.getData(path, true, null));
	}
	
	/**
	 * 设置节点数据
	 * @param path 节点路径
	 * @param data 节点数据
	 * @date 2016年1月25日 下午3:37:46 
	 * @author yxl
	 */
	public void setData(String path, String data) throws Exception{
		zooKeeper.setData(path, data.getBytes("utf-8"), -1);//版本号-1，无视被修改的数据版本，直接改掉
	}
	
	/**
	 * 设置节点数据
	 * @param path 节点路径
	 * @param data 节点数据
	 * @param version 节点数据版本号
	 * @date 2016年1月25日 下午3:37:46 
	 * @author yxl
	 */
	public void setData(String path, String data, int version) throws Exception{
		zooKeeper.setData(path, data.getBytes("utf-8"), version);
	}
	
	/**
	 * 删除一个Path
	 * @date 2016年1月25日 下午3:37:46 
	 * @author yxl
	 */
	public void delete(String path) throws Exception{
		zooKeeper.delete(path, -1);
	}

	/**
	 * 测试
	 * @date 2016年1月25日 下午3:55:51 
	 * @author yxl
	 */
	public static void main(String[] args) throws Exception {
		for(int i=0; i<3; i++){
			long startTime = System.currentTimeMillis();
			ZookeeperConnection zookeeperConnection = new ZookeeperConnection();
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
		}
	}
}
