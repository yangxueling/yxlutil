package com.yxl.util.chat;

/**
 * 消息
 * @author yxl
 */
public class Message {
	/** 消息内容 */
	private String message;
	/** 用户名称 */
	private String userName;
	/** 发送时间 */
	private String sendTime;
	
	
	public String getMessage() {
		return message;
	}
	
	public String getUserName() {
		return userName;
	}

	public String getSendTime() {
		return sendTime;
	}
	
	
	public void setMessage(String message) {
		this.message = message;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
}
