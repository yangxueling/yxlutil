package com.yxl.util.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行cmd命令
 * @author yxl
 */
public class SysCmdHandle {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** 超时时间 */
	public int CMD_TIME_OUT = 40;
	
	/** 默认检查间隔（毫秒）*/
	public int DEFAULT_INTERVAL = 1000;
	/** 当执行结束后，等待多少毫秒销毁 */
	public int WAIT_TIME_ON_OVER = 50;
	
	//结束字符串标记（小写）
	public String endKeyStr = null;
	
	/**
	 * 执行命令
	 * @param cmdStr cmd命令
	 * @return 501 超时
	 * @autor yxl
	 */
	public Map excute(String cmdStr) {
		
		final Map map = new HashMap();
		// 处理进程
		Process process = null;
		try {
			if (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1)
				process = Runtime.getRuntime().exec(cmdStr);
			else 
				process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmdStr});
			
			Runtime.getRuntime().addShutdownHook(new ShutdownHook(process));//关闭java时关闭进程
			
			final  InputStream is1 = process.getInputStream();
	        final  InputStream is2 = process.getErrorStream();
			logger.debug("正在执行命令：" + cmdStr);
			
			//获取命令行代码
			new  Thread() {
                public   void  run() {
                    BufferedReader br = new  BufferedReader(new InputStreamReader(is1));
                    String str = "";
                    try	{
                        String line = null ;  
  
                        while  ((line = br.readLine()) != null){
                            if  (line != null ) {
                            	str += line;
                            	logger.debug("命令行返回：" + line);
                            }
                        }
                    } catch  (IOException e){
                        e.printStackTrace();
                    } finally{
                    	map.put("reback", str);
						try {
							if(br != null) br.close();
							if(is1 != null) is1.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
                    }
                }  
            }.start();
            
            //获取错误代码
			new  Thread() {
                public   void  run() {
                    BufferedReader br = new  BufferedReader(new InputStreamReader(is2));
                    String str = "";
                    try	{
                        String line = null ;  
  
                        while  ((line = br.readLine()) != null){
                            if  (line != null ) {
                            	str += line;
                            	logger.error("命令行返回错误：" + line);
                            }
                        }
                    } catch  (IOException e){
                        e.printStackTrace();
                    } finally{
                    	map.put("errors", str);
						try {
							if(br != null) br.close();
							if(is2 != null) is2.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
                    }
                }  
            }.start();
			
			
			
            //等待程序执行完成
			long startTime = System.currentTimeMillis();
			
			//代表有几个进程没有终止
			int exitValue = 0;
			boolean processFinished = false;  
			while(System.currentTimeMillis()-startTime < CMD_TIME_OUT*1000 && !processFinished){  
			    try {
			        exitValue = process.exitValue();  
			    } catch (IllegalThreadStateException e) {  
			        Thread.sleep(DEFAULT_INTERVAL);  
			        continue;
			    }
			    logger.debug("命令执行结束");
			    processFinished = true;
			    Thread.sleep(WAIT_TIME_ON_OVER);//有一些外部程序结束后，他的工作并没有处理完，等待他一下
			}
			if(exitValue > 0) logger.warn("有 " + exitValue + " 个进程没有结束！");
			if(System.currentTimeMillis()-startTime > CMD_TIME_OUT*1000) logger.error("执行超时，强行退出！");
			
			//没有未结束的进程时代表成功
			if(exitValue>1) {
				map.put("status", 501);
			} else {
				map.put("status", 200);
			}
			
			//等待执行结束,不能用这个方法，会阻塞
			//process.waitFor();
		} catch (Exception e) {
			map.put("status", 500);
			e.printStackTrace();
		} finally{
			if(process != null) process.destroy();
			logger.debug("释放资源");
			return map;
		}
		
	}
	
	/**
	 * 关闭线程类
	 * @author yxl
	 */
	private class ShutdownHook extends Thread{
		
		// 处理进程
		private Process process = null;
		
		public ShutdownHook(Process process){
			this.process = process;
		}
		
		@Override
		public void run() {
			if(process != null) process.destroy();
		}
	}
}
