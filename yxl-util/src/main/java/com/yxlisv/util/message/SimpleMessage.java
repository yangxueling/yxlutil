package com.yxlisv.util.message;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息
 * @author john Local
 */
public class SimpleMessage {
	//message
	private List msgList = new ArrayList();
	private List warningList = new ArrayList();
	private List errorList = new ArrayList();
	
	
	/**
	 * 添加消息
	 * @param msg 消息内容
	 * @autor yxl
	 */
	public void addMsg(String msg){
		msgList.add(msg);
	}
	
	/**
	 * 添加错误消息
	 * @param msg 消息内容
	 * @autor yxl
	 */
	public void addError(String msg){
		errorList.add(msg);
	}
	
	/**
	 * 添加警告消息
	 * @param msg 消息内容
	 * @autor yxl
	 */
	public void addWarning(String msg){
		warningList.add(msg);
	}
	
	/**
	 * 读取一条消息
	 * @autor yxl
	 */
	public String getReadMsg(){
		return this.getRead(1, 1);
	}
	
	/**
	 * 读取一条警告消息
	 * @autor yxl
	 */
	public String getReadWarning(){
		return this.getRead(2, 1);
	}
	
	/**
	 * 读取一条错误消息
	 * @autor yxl
	 */
	public String getReadError(){
		return this.getRead(3, 1);
	}
	
	
	
	
	/**
	 * 读取最后一条消息
	 * @autor yxl
	 */
	public String getReadLastMsg(){
		return this.getRead(1, -1);
	}
	
	/**
	 * 读取最后一条警告消息
	 * @autor yxl
	 */
	public String getReadLastWarning(){
		return this.getRead(2, -1);
	}
	
	/**
	 * 读取最后一条错误消息
	 * @autor yxl
	 */
	public String getReadLastError(){
		return this.getRead(3, -1);
	}
	
	/**
	 * 获取消息类别（单条消息），先读错误消息，再读警告，最后读消息
	 * @autor yxl
	 */
	public int getType(){
		
		if(errorList.size() > 0) return 3;
		else if(warningList.size() > 0) return 2;
		else if(msgList.size() > 0) return 1;
		else return 0;
	}
	
	/**
	 * 读取消息（单条消息），先读错误消息，再读警告，最后读消息
	 * @autor yxl
	 */
	public String getReadLast(){
		String msg = "";
		if(errorList.size() > 0) {
			msg = getReadLastError();
			warningList.clear();
			msgList.clear();
			errorList.clear();
		} else if(warningList.size() > 0) {
			msg = getReadLastWarning();
			warningList.clear();
			msgList.clear();
			errorList.clear();
		} else if(msgList.size() > 0) {
			msg = getReadLastMsg();
			warningList.clear();
			msgList.clear();
			errorList.clear();
		}
		return msg;
	}
	
	
	/**
	 * 读取最后一条消息
	 * @param type 1:消息, 2:警告, 3:错误
	 * @param order 顺序  1：顺序， -1：倒序
	 * @autor yxl
	 */
	public String getRead(int type, int order){
		//选择消息类别
		List tempMsgList = null;
		switch (type) {
			//消息
			case 1: {
				tempMsgList = msgList;
				break;
			}
			case 2: {
				tempMsgList = warningList;
				break;
			}
			case 3: {
				tempMsgList = errorList;
				break;
			}
		}
		
		//读取消息
		if(tempMsgList == null || tempMsgList.size()<1) return null;
		//读取之后就移除
		String msg = "";
		
		//倒序读还是正序读
		try{
			if(order == 1){
				msg = tempMsgList.get(0).toString();
				tempMsgList.remove(0);
			}else {
				msg = tempMsgList.get(tempMsgList.size()-1).toString();
				tempMsgList.remove(tempMsgList.size()-1);
			}
		} catch(Exception e){}
		return msg;
	}
	
	
	
	
	
	
	
	/**
	 * 读取所有消息
	 * @autor yxl
	 */
	public List getAllMsg(){
		return this.msgList;
	}
	
	/**
	 * 读取所有警告消息
	 * @autor yxl
	 */
	public List getAllWarning(){
		return this.warningList;
	}
	
	/**
	 * 读取所有错误消息
	 * @autor yxl
	 */
	public List getAllError(){
		return this.errorList;
	}
	
	
	/**
	 * 移除所有消息
	 * @autor yxl
	 */
	public void removeAllMsg(){
		msgList.clear();
	}
	
	/**
	 * 移除所有警告消息
	 * @autor yxl
	 */
	public void removeAllWarning(){
		warningList.clear();
	}
	
	/**
	 * 移除所有错误消息
	 * @autor yxl
	 */
	public void removeAllError(){
		errorList.clear();
	}
}
