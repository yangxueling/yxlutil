package com.yxlisv.util.api;

/**
 * <p>数据相应对象</p>
 * @author 杨雪令
 * @time 2016年3月11日上午11:41:56
 * @version 1.0
 */
public class ResponeData<T> {
	
	//status：状态码（200：成功，404：token失效，500：系统出错）
	private int status;
	
	/**
	 * <p>获取状态码</p>
	 * <p>status：（200：成功，404：token失效，500：系统出错）</p>
	 * @return int 状态码
	 * @author 杨雪令
	 * @time 2016年3月11日上午11:46:54
	 * @version 1.0
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * <p>设置状态码</p>
	 * <p>status：（200：成功，404：token失效，500：系统出错）</p>
	 * @author 杨雪令
	 * @time 2016年3月11日上午11:47:30
	 * @version 1.0
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	//系统消息
	private String message;
	
	/**
	 * <p>获取系统消息</p>
	 * @return String 系统消息
	 * @author 杨雪令
	 * @time 2016年3月11日上午11:48:36
	 * @version 1.0
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * <p>设置系统消息</p>
	 * @param message 系统消息
	 * @author 杨雪令
	 * @time 2016年3月11日上午11:48:55
	 * @version 1.0
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	//结果数据
	private T result;
	
	/**
	 * <p>获取结果数据</p>
	 * @return T 结果数据
	 * @author 杨雪令
	 * @time 2016年3月11日上午11:49:55
	 * @version 1.0
	 */
	public T getResult() {
		return result;
	}
	
	/**
	 * <p>设置结果数据</p>
	 * @param result 结果数据
	 * @author 杨雪令
	 * @time 2016年3月11日上午11:50:24
	 * @version 1.0
	 */
	public void setResult(T result) {
		this.result = result;
	}
}
