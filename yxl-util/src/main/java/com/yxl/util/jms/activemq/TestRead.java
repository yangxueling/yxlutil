package com.yxl.util.jms.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TestRead implements MessageListener {
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println("-" + textMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws JMSException {

		String queueName = "test_duihua1";
		ActivemqClient ActivemqRead1 = new ActivemqClient();
		MessageConsumer consumer1 = ActivemqRead1.getConsumer(queueName, ActivemqClient.MsgType.P2P);
		consumer1.setMessageListener(new TestRead());
		
		String topicName = "topic.hello";
		ActivemqClient ActivemqRead2 = new ActivemqClient("ActivemqRead2");
		MessageConsumer consumer2 = ActivemqRead2.getConsumer(topicName, ActivemqClient.MsgType.Broadcast);
		consumer2.setMessageListener(new TestRead());
		System.out.println("TestRead jx");
	}
}
