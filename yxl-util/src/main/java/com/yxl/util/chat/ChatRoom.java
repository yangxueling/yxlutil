package com.yxl.util.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yxl.util.date.DateUtil;

/**
 * 聊天室
 * @author yxl
 */
public class ChatRoom {
	//所有聊天室集合
	private static Map<String, ChatRoom> chatRoomMap = new HashMap();
	
	/**
	 * 获取一个聊天室
	 * @param roomKey 房间钥匙
	 */
	public static ChatRoom getChatRoom(String roomKey){
		ChatRoom chatRoom = chatRoomMap.get(roomKey);
		if(chatRoom == null) chatRoom = ChatRoom.createChatRoom(roomKey);
		return chatRoom;
	}

	/**
	 * 创建一个聊天室
	 * @param roomKey 房间钥匙
	 */
	private static ChatRoom createChatRoom(String roomKey) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoomMap.put(roomKey, chatRoom);
		
		return chatRoom;
	}
	
	//最大消息记录
	private static int maxMsgCount = 5;
	//消息列表
	private List<Message> msgList = new ArrayList();
		
	/**
	 * 发送消息
	 * @param message 消息内容
	 * @param userName 用户名称
	 * @autor yxl
	 */
	public void sendMessage(String message, String userName){
		if(message == null || message.trim().equals("")) return;
		Message msg = new Message();
		msg.setMessage(message);
		msg.setUserName(userName);
		msg.setSendTime(DateUtil.getTime());
		
		msgList.add(msg);
		//如果消息记录太多，清除最前面的一条消息
		if(msgList.size() > maxMsgCount)	msgList.remove(0);
	}
	
	
	/**
	 * 获取所有消息
	 * @autor yxl
	 */
	public List getAllMessage(){
		return msgList;
	}
}
