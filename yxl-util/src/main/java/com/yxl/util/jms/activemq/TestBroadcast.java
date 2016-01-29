package com.yxl.util.jms.activemq;

import javax.jms.JMSException;

public class TestBroadcast {
	public static void main(String[] args) throws JMSException {
		String topicName = "topic.hello";
		ActivemqClient activemqBroadcast = new ActivemqClient("ActivemqBroadcast");
		activemqBroadcast.broadcast("通知：明天油價又要上漲了", topicName);
	}
}
