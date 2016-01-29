package com.yxl.util.jms.activemq;

import javax.jms.JMSException;

public class TestSend {
	public static void main(String[] args) throws JMSException {
		String nameA = "A：";
		String nameB = "B：";
		String queueName = "test_duihua1";
		ActivemqClient activemqSend = new ActivemqClient();
		activemqSend.sendMessage(nameA + "在嗎？", queueName);
		activemqSend.sendMessage(nameA + "有事", queueName);
		activemqSend.sendMessage(nameB + "在啊！", queueName);
		activemqSend.sendMessage(nameA + "最近怎麼樣啊？", queueName);
		activemqSend.sendMessage(nameB + "還不錯", queueName);
		activemqSend.sendMessage(nameA + "借點錢可以嗎？", queueName);
		activemqSend.sendMessage(nameB + "自動回复：對不起，用戶已經離開電腦", queueName);
	}
}
