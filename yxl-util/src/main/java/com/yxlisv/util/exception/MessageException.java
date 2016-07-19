package com.yxlisv.util.exception;

/**
 * 消息通知异常（捕获此异常，直接显示给用户）
 * @author yxl
 */
@SuppressWarnings("serial")
public class MessageException extends RuntimeException{

	/** 消息分隔符 */
	public static final String MSG_SEPARATOR = "###";
	
	/**
	 * 默认构造方法
	 * @param msg 消息
	 */
	public MessageException(String msg) {
		super(msg);
	}

	/**
	 * 构造方法
	 * @param msg 消息（显示给用户）
	 * @param detail 详细信息（记录到日志，自动包含msg）
	 */
	public MessageException(String msg, String detail) {
		
		super(msg + MSG_SEPARATOR + detail);
	}
	
	
	/**
	 * 获取消息
	 * @autor yxl
	 * 2014-6-25
	 */
	public static String getMsg(String msg){
		if(!msg.contains(MSG_SEPARATOR)) return msg;
		return msg.split(MSG_SEPARATOR)[0];
	}
}
