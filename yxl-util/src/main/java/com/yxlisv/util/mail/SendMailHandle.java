/**
 * 文件名： SendMailHandle.java
 * 描述： 发送邮件处理类，兼容GMAIL
 * 修改人：   杨雪令 
 * 修改时间： 2011-3-30
 * 修改内容：创建类
 * @author: 杨雪令
 * @date: 2011-3-30
 * @version V1.0
 */

package com.yxlisv.util.mail;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


public class SendMailHandle {
	public static final String TEXT = "text/plain;charset=gb2312";
	public static final String HTML = "text/html;charset=gb2312";
	
	/**
	 * 设置邮件内容的形式
	 * 
	 * @param string
	 * @param contentType
	 * @throws MessagingException 
	 */
	public BodyPart getBody(String string, String contentType) throws MessagingException {
		BodyPart body = new MimeBodyPart();
		DataHandler dh = new DataHandler(string, contentType);
		body.setDataHandler(dh);
		return body;
	}

	/**
	 * 设置邮件的内容的格式为文本格式
	 * 
	 * @param string
	 * @return 
	 * @throws MessagingException 
	 */
	public BodyPart getBodyAsText(String string) throws MessagingException {
		return getBody(string, TEXT);
	}

	/**
	 * 以HTMl的形式存放内容
	 * 
	 * @param string
	 * @return 
	 * @throws MessagingException 
	 */
	public BodyPart getBodyAsHTML(String string) throws MessagingException {
		return getBody(string, HTML);
	}

	/**
	 * 从文件中自动导入邮件内容
	 * 
	 * @param filename
	 * @throws MessagingException 
	 */
	public BodyPart getBodyFromFile(String filename) throws MessagingException {
		BodyPart mdp = new MimeBodyPart();
		FileDataSource fds = new FileDataSource(filename);
		DataHandler dh = new DataHandler(fds);
		mdp.setDataHandler(dh);
		return mdp;
	}

	/**
	 * 从一个URL导入邮件的内容
	 * 
	 * @param url
	 * @throws MessagingException 
	 * @throws MalformedURLException 
	 */
	public BodyPart getBodyFromUrl(String url) throws MessagingException, MalformedURLException {
		BodyPart mdp = new MimeBodyPart();
		URLDataSource ur = new URLDataSource(new URL(url));
		DataHandler dh = new DataHandler(ur);
		mdp.setDataHandler(dh);
		return mdp;
	}

	/**
	 * 将String中的内容存放入文件showname，并将这个文件作为附件发送给收件人
	 * 
	 * @param string
	 *            为邮件的内容
	 * @param showname
	 *            显示的文件的名字
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	public BodyPart addAttachFromString(String string, String showname) throws UnsupportedEncodingException, MessagingException {
		BodyPart mdp = new MimeBodyPart();
		DataHandler dh = new DataHandler(string, TEXT);
		mdp.setFileName(MimeUtility.encodeWord(showname, "gb2312", null));
		mdp.setDataHandler(dh);
		return mdp;
	}

	/**
	 * filename为邮件附件 在收信人的地方以showname这个文件名来显示
	 * 
	 * @param filename
	 * @param showname
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	public BodyPart addAttachFromFile(String filename, String showname) throws UnsupportedEncodingException, MessagingException {
		if (showname == null || showname.trim().equals("")) showname = filename.substring(filename.indexOf("/")!=-1?filename.lastIndexOf("/")+1:0);
		BodyPart mdp = new MimeBodyPart();
		FileDataSource fds = new FileDataSource(filename);
		DataHandler dh = new DataHandler(fds);
		mdp.setFileName(MimeUtility.encodeWord(showname, "gb2312", null));
		mdp.setDataHandler(dh);
		return mdp;
	}

	/**
	 * 将互联网上的一个文件作为附件发送给收信人 并在收信人处显示文件的名字为showname
	 * 
	 * @param url
	 * @param showname
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 */
	public BodyPart addAttachFromUrl(String url, String showname) throws UnsupportedEncodingException, MessagingException, MalformedURLException {
		if (showname == null || showname.trim().equals("")) showname = url.substring(url.indexOf("/")!=-1?url.lastIndexOf("/")+1:0);
		BodyPart mdp = new MimeBodyPart();
		URLDataSource ur = new URLDataSource(new URL(url));
		DataHandler dh = new DataHandler(ur);
		mdp.setFileName(MimeUtility.encodeWord(showname, "gb2312", null));
		mdp.setDataHandler(dh);
		return mdp;
	}

	/**
	 * 描述：   发送邮件
	 * @author: 杨雪令
	 * @param host 服务器
	 * @param port 端口
	 * @param username 用户名
	 * @param password 密码
	 * @param from 发送者邮箱地址
	 * @param to 收件人地址
	 * @param subject 主题
	 * @param content 内容
	 * @param attachfile 附件
	 * @param cc 抄送
	 * @param bc 密送
	 * @param type 类型 html,text...
	 * @param isAuth 是否要验证，GMAIL需要验证，端口号为 587
	 * @throws Exception
	 * @version: 2011-3-30 下午11:01:31
	 */
	public void send(String host,String port,String username,String password,String from,String to,String subject,String content,String[] attachfile,String type,boolean isAuth,String cc,String bc) throws Exception{
		    
		try {
			// *****会话类*****//
			Properties props = new Properties();
			if (host != null && !host.trim().equals("")){
				
				props.setProperty("mail.smtp.host", host);// key value
				props.put("mail.smtp.port", port);
			}
				
			else
				throw new Exception("没有指定发送邮件服务器");
			if (isAuth){
				props.put("mail.smtp.starttls.enable", "true");
				props.setProperty("mail.smtp.auth", "true");
			}
				
			Session s = Session.getInstance(props, null);
			// *****消息类*****//
			MimeMessage msg = new MimeMessage(s);
			msg.setSubject(subject);// 设置邮件主题
			msg.setSentDate(new Date());// 设置邮件发送时间
			// *****地址类*****//
			if (from != null)
				msg.addFrom(InternetAddress.parse(from));// 指定发件人
			else
				throw new Exception("没有指定发件人");
			if (to != null)
				msg.addRecipients(Message.RecipientType.TO, InternetAddress
						.parse(to));// 指定收件人
			else
				throw new Exception("没有指定收件人地址");
			if (cc != null)
				msg.addRecipients(Message.RecipientType.CC, InternetAddress
						.parse(cc));// 指定抄送
			if (bc != null)
				msg.addRecipients(Message.RecipientType.BCC, InternetAddress
						.parse(bc));// 指定密送
			Multipart mm = new MimeMultipart();
			
			//设置邮件内容s
			if (content != null && !content.trim().equals("")){
				if (type ==null || type.trim().equals("")){
				    mm.addBodyPart(this.getBodyAsText(content));//默认以Text形式存放内容
			    } else if (type.trim().toUpperCase().equals("HTML")){
			    	mm.addBodyPart(this.getBodyAsHTML(content));
			    } else if (type.trim().toUpperCase().equals("TEXT")){
			    	mm.addBodyPart(this.getBodyAsText(content));
			    } else {
			    	mm.addBodyPart(this.getBody(content,type.trim().toUpperCase()));
			    }
			}
			
			// 设置邮件的附件
			if (attachfile!=null)
				for (int i = 0; i < attachfile.length; i++) {
					String fileName = attachfile[i];
					BodyPart part = (BodyPart) this.addAttachFromFile(fileName, null);
					mm.addBodyPart(part);
				}
			msg.setContent(mm);// 设置邮件的内容
			// *****传输类*****//
			msg.saveChanges();// 保存所有改变
			Transport transport = s.getTransport("smtp");// 发送邮件服务器（SMTP）
			transport.connect(host, username, password);// 访问邮件服务器
			transport.sendMessage(msg, msg.getAllRecipients());// 发送信息
			transport.close();// 关闭邮件传输
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("发送邮件失败:", e);
		}
	}
	
	/**
	 * 描述：   没有抄送和密送
	 * @author: 杨雪令
	 * 没有附件
	 * @version: 2011-3-31 上午08:39:44
	 */
	public synchronized void send(String host, String port, String username,
			String password, String from, String to, String subject,
			String content, String[] attachfile, String type, boolean isAuth) throws Exception {
		this.send(host, port, username, password, from, to, subject, content, attachfile, type, isAuth, null, null);
	}
	
	/**
	 * 描述：   
	 * @author: 杨雪令
	 * 没有附件
	 * @version: 2011-3-31 上午08:39:44
	 */
	public void send(String host, String port, String username,
			String password, String from, String to, String subject,
			String content, String type, boolean isAuth) throws Exception {
		this.send(host, port, username, password, from, to, subject, content, null, type, isAuth, null, null);
	}
	
	
	
	//测试
	public static void main(String[] args) {
		
		//gmail
		String host ="smtp.gmail.com";
	    String port ="587";
	    String from ="noforgetit@gmail.com";
	    String username = "noforgetit";
	    String password = "123";
	    String to ="1326598912@qq.com";
	    //String to ="784263610@qq.com";
	    //String to ="813354922@qq.com";
	    String subject ="<a>测试GMAIL发送</a>";
	    String content = "<a href=\"http://boolen.taobao.com\">淘宝服装城</a>";
	    String type = "html";
	    boolean isAuth = true;
	    
	    
		//163
		/*String host ="smtp.163.com";
	    String port ="25";
	    String from ="3yxl@163.com";
	    String username = "3yxl";
	    String password = "901226";
	    String to ="1326598912@qq.com";
	    String subject ="<a>测试163发送</a>";
	    String content = "<a href=\"http://boolen.taobao.com\">淘宝服装城</a>";
	    String type = "html";
	    boolean isAuth = false;*/
	    
		//String[] attachfile = {"D:/down/fucker.zip"};
	    String[] attachfile = null;
		//out.println("<br>"+password);
		try {
		    SendMailHandle send = new SendMailHandle();
		    send.send(host, port, username, password, from, to, subject, content, attachfile, type, isAuth);
		    System.out.println("系统信息邮件发送成功！！！");
		   } catch (Exception e) {
			   System.out.println(e.getMessage());
			   System.out.println("系统信息邮件发送失败！！！");
		   }

	}

}