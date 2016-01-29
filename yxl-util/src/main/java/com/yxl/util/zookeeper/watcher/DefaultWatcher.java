package com.yxl.util.zookeeper.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认事件监控
 * @createTime 2016年1月25日 下午6:00:45 
 * @author yxl
 */
public class DefaultWatcher implements Watcher{

	private static Logger logger = LoggerFactory.getLogger(DefaultWatcher.class);
	
	@Override
	public void process(WatchedEvent event) {
		logger.info("ZooKeeper Watcher : " + event.getPath() + " / " + event.getType().name());
	}

}
