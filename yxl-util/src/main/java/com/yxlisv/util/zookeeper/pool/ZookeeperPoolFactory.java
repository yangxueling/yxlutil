package com.yxlisv.util.zookeeper.pool;

import java.util.Properties;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.zookeeper.Watcher;

import com.yxlisv.util.reflect.ReflectionUtils;
import com.yxlisv.util.resource.PropertiesUtil;
import com.yxlisv.util.zookeeper.ZookeeperConnection;

/**
 * Zookeeper 连接池工厂
 * @createTime 2016年1月25日 下午5:01:19 
 * @author yxl
 */
public class ZookeeperPoolFactory implements PooledObjectFactory<ZookeeperConnection>{

	/** zookeeper 配置文件 */
	private static Properties properties = PropertiesUtil.readProperties("zookeeper.pool.properties", ZookeeperPool.class);
	
	/**
	 * 创建ZookeeperAdapter
	 * @createTime 2016年1月25日 下午5:01:19 
	 * @author yxl
	 */
	@Override
	public PooledObject<ZookeeperConnection> makeObject() throws Exception {
		ZookeeperConnection zookeeperAdapter = null;
		if(properties.get("watcher.class")!=null){
			zookeeperAdapter = new ZookeeperConnection((Watcher) ReflectionUtils.newInstance(properties.get("watcher.class").toString()));
		} else {
			zookeeperAdapter = new ZookeeperConnection();
		}
		return new DefaultPooledObject<ZookeeperConnection>(zookeeperAdapter);
	}

	/**
	 * 销毁ZookeeperAdapter
	 * @createTime 2016年1月25日 下午5:01:19 
	 * @author yxl
	 */
	@Override
	public void destroyObject(PooledObject<ZookeeperConnection> p) throws Exception {
	}

	@Override
	public boolean validateObject(PooledObject<ZookeeperConnection> p) {
		return true;
	}

	@Override
	public void activateObject(PooledObject<ZookeeperConnection> p) throws Exception {
	}

	@Override
	public void passivateObject(PooledObject<ZookeeperConnection> p) throws Exception {
	}
}