package com.yxl.util.exception;

/**
 * 简单消息通知异常，不会输出错误堆栈（捕获此异常，直接显示给用户）
 * @author yxl
 */
@SuppressWarnings("serial")
public class SimpleMessageException extends MessageException{

	/**
	 * 默认构造方法
	 * @param msg 消息
	 */
	public SimpleMessageException(String msg) {
		super(msg);
	}

	/**
	 * 构造方法
	 * @param msg 消息（显示给用户）
	 * @param detail 详细信息（记录到日志，自动包含msg）
	 */
	public SimpleMessageException(String msg, String detail) {
		
		super(msg + MSG_SEPARATOR + detail);
	}
}