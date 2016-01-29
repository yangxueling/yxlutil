/**
 * 文件名： MailAutoSend.java
 * 描述： 描述该文件做什么
 * 修改人：   杨雪令 
 * 修改时间： 2011-3-31
 * 修改内容：创建类
 * @author: 杨雪令
 * @date: 2011-3-31
 * @version V1.0
 */

package com.yxlisv.util.mail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 描述： 描述该文件做什么
 * @author: 杨雪令
 * @version 1.0
 */
public class MailAutoSend implements Runnable {

	//邮件发送工具类
	protected static String STATTIME = "";//启动时间
	protected static int COUNT = 0;//发送次数
	protected static boolean MAILLOCK = false;//生成邮件资源是否被锁
	protected static SendMailHandle smh = null;
	
	protected static String host = "";
	protected static String port = "25";
	protected static String from = "";
	protected static String username = "";
	protected static String password = "";
	protected static boolean isAuth = false;
	
	static String subject = "";
	static String content = "";
	static String[] attachfile = null;
	static String type = "html";
	//变量初始化
	static {
		smh = new SendMailHandle();
		Properties p = new Properties();
		try {
			p.load(MailAutoSend.class.getResourceAsStream("mail.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("配置文件没有找到");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("读取配置文件时发生错误");
			e.printStackTrace();
		}
		host = p.getProperty("host").trim();
		port = p.getProperty("port").trim();
		from = p.getProperty("from").trim();
		username = p.getProperty("username").trim();
		password = p.getProperty("password").trim();
		isAuth = p.getProperty("isAuth").trim().equals("true");
		subject = p.getProperty("subject").trim();
		
		try {//初试化内容
			initContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 描述：   初试化内容
	 * @author: 杨雪令
	 * @version: 2011-3-31 下午03:43:05
	 * @throws IOException 
	 */
	public static void initContent() throws IOException{
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		InputStreamReader ips;
		ips = new InputStreamReader(MailAutoSend.class.getResourceAsStream("content.html"),"UTF-8");
		br = new BufferedReader(ips);

		/** 读取文件内容 */
		String line = "";
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		
		br.close();
		ips.close();
		content = sb.toString();
	}
	
	
	
	@Override
	public void run() {
		String addrs[] = getMail();
		for(int i=0;i<addrs.length;i++){
			String to = addrs[i];
			try {
				smh.send(host, port, username, password, from, to, subject, content, attachfile, type, isAuth);
				wait(3000);
			} catch (Exception e) {
				System.out.println("发送邮件到 \"" +to + "\" 时发生不可预料的错误！");
				continue;
			}
			System.out.println("成功发送邮件到 \"" +to + "\"");
		}
		COUNT++;
		System.out.println("已发送" + COUNT + "次!");
	}

	/**
	 * 描述：   随机计算邮件地址
	 * @author: 杨雪令
	 * @return
	 * @version: 2011-3-31 下午04:02:25
	 */
	private String[] getMail() {

		String addr[] = new MailAddrCreator().getAddrGroup();
		
		String mailAddr[] = {"1326598912@qq.com"}; 
		//return mailAddr;
		return addr;

		
	}
	
	/**
	 * 描述：启动count个线程   
	 * @author: 杨雪令
	 * @version: 2011-4-7 下午03:47:59
	 */
	public void startTheard(int count){
		//设置启动时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
        STATTIME = df.format(new Date());
        //打开监听器
		Listenler listenler = new Listenler(count);
		listenler.start();
		//打开管理员系统汇报监听线程
		new SysMail().start();
	}
	
	
	//线程监听类
	private class Listenler extends Thread{
		int count =0;
		public Listenler(int count) {
			this.count = count;
		}
		@Override
		public void run() {
			while (true){
				
				//main 方法是一个线程，监听类又是一个线程，所以开始就已经有3个线程，所以+3
				if (Thread.activeCount()<count+3){
					MailAutoSend ms = new MailAutoSend();
					new Thread(ms).start();
					System.out.println("============>> 当前活动线程：" + Thread.activeCount() + "个");
				}
				try {
					this.sleep(6000);//节省资源
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//发送系统信息到管理邮件
	private class SysMail extends Thread{
		String to = "1326598912@qq.com";
		String subject = "(启动时间)邮件自动发送工具系统报告";
		String content = "邮件自动发送工具自启动以来工作正常";
		@Override
		public void run() {
			while (true){
				try {
					String contentNew = "启动时间：" + STATTIME + "<br/>" + content + " <br/>共发送邮件\""+COUNT+"\"次!";
					smh.send(host, port, username, password, from, to, STATTIME+subject, contentNew, null, "html", isAuth);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					//this.sleep(3600);//节省资源,每隔1小时发一次
					this.sleep(3600000);//节省资源,每隔1小时发一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
 
	/**描述：   主线程
	 * @author: 杨雪令
	 * @param args
	 * @version: 2011-3-31 下午03:08:13    
	 */
	public static void main(String[] args) {

		new MailAutoSend().startTheard(1);
	}

}
