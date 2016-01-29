package com.yxlisv.util.security.jvm;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内存清理监听器
 */
public class MemoryCleanListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(MemoryCleanListener.class);

	protected Timer timer = null;
	protected TimerTask timerTask = null;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		//从web.xml中加载间隔时间(秒)
		String sPeriod = event.getServletContext().getInitParameter("period");
		//延迟时间（秒）
		String sDelay = event.getServletContext().getInitParameter("delay");
		int period = 120;
		int delay = 10;
		try{
			period = Integer.parseInt(sPeriod);
			delay = Integer.parseInt(sDelay);
		}catch(Exception e){}
		timer = new Timer(true);
		timerTask = new MemoryCleanTask();
		//1000 * 60 * 1  1分钟
		timer.schedule(timerTask, 1000 * delay, 1000 * period);
		
		logger.info("Initializing MemoryCleanListener, period=" + period + ", delay=" + delay);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {

		timer.cancel();
	}
}