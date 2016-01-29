/**
 * 文件名： MailCreator.java
 * 描述： 描述该文件做什么
 * 修改人：   杨雪令 
 * 修改时间： 2011-4-7
 * 修改内容：创建类
 * @author: 杨雪令
 * @date: 2011-4-7
 * @version V1.0
 */

package com.yxl.util.mail;

import java.util.Random;

/**
 * 描述： 邮件地址生成器
 * @author: 杨雪令
 * @version 1.0
 */
public class MailAddrCreator {
	
	//目标服务器
	protected String[] mailServer = {"@163.com","@qq.com","@gmail.com","@126.com","@sina.com"};
	
	/**
	 * 描述：获取一组邮件地址   
	 * @author: 杨雪令
	 * @version: 2011-4-7 下午03:05:59
	 */
	public String[] getAddrGroup(){
		
		Random rand = new Random();
		String []mails = mailServer;
		String addr = "";//163,gmail等大部份是字符串+2-3位数字，或者没有数字
		boolean hasNum = rand.nextInt(2)==0;//是否要数字
		int count = rand.nextInt(4)+1;//数字个数
		String qqaddr = "";//qq邮箱都是7-10位数字组成的
		
		
		int length = rand.nextInt(16)+3;
		for (int i=0;i<length;i++){//生成以字符串为主的邮件地址
			if (hasNum && length-i<=count) {//如果最后有数字
				char c = (char) (rand.nextInt(10) + 48);
				addr+=c;
			} else {
				char c = (char) (rand.nextInt(24) + 97);
				addr+=c;
			}
			
		}
		
		if (length<7 || length>10) length = rand.nextInt(4)+7;
		for (int i=0;i<length;i++){//生成全数字邮件地址
			char c = (char) (rand.nextInt(10) + 48);
			qqaddr+=c;
		}
		
		for (int i=0;i<mails.length;i++){
			if (mails[i].indexOf("qq.com")!=-1){
				mails[i] = qqaddr + mails[i];
			} else
				mails[i] = addr + mails[i];
		}
		return mails;
	}

	/**描述：   48-57[0-9] 65-90[A-Z] 97-122[a-z]
	 * @author: 杨雪令
	 * @param args
	 * @version: 2011-4-7 下午02:41:12    
	 */
	public static void main(String[] args) {
		String addr[] = new MailAddrCreator().getAddrGroup();
		System.out.println(addr[0]);
		System.out.println(addr[1]);
	}

}
