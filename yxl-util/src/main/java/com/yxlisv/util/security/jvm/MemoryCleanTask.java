package com.yxlisv.util.security.jvm;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内存清理任务
 * @author yxl
 */
public class MemoryCleanTask extends TimerTask{
	private static final Logger logger = LoggerFactory.getLogger(MemoryCleanTask.class);
	
	public MemoryCleanTask(){
		String vStr = Memory.restoreJvm();
		logger.debug(vStr);
	}

	@Override
	public void run() {
		String vStr = Memory.restoreJvm();
		logger.debug(vStr);
	}
}
