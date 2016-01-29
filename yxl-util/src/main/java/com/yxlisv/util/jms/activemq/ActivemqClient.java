package com.yxlisv.util.jms.activemq;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.resource.PropertiesUtil;

/**
 * Jms Activemq 客戶端
 * 測試時去掉：javaee-api包的引用
 * 		<dependency>
		  <groupId>org.apache.activemq</groupId>
		  <artifactId>activemq-all</artifactId>
		  <version>5.9.0</version>
		</dependency>
 * @createTime 2016年1月18日 下午5:47:15 
 * @author yxl
 */
public class ActivemqClient {
	
	private static Logger logger = LoggerFactory.getLogger(ActivemqClient.class);
	/** 配置文件 */
	private static Properties properties = PropertiesUtil.readProperties("activemq.properties", ActivemqClient.class);
	/** JMS连接工厂 */
	private static ConnectionFactory connectionFactory = null;
	/** 消息類別：點對點（隊列）/廣播（主題） */
	public static enum MsgType {P2P, Broadcast}
	
	/** JMS 客户端到JMS Provider 的连接 */
	private Connection connection = null;
	/** 一个发送或接收消息的线程 */
	private Session session = null;
	/** 消息生產者，key為隊列名稱 */
	private Map<String, MessageProducer> producerMap = new HashMap<String, MessageProducer>();
	/** 消息消費者，key為隊列名稱 */
	private Map<String, MessageConsumer> consumerMap = new HashMap<String, MessageConsumer>();
	/** 消息生產者鎖 */
	private Lock producerLock = new ReentrantLock();
	/** 消息消費者鎖 */
	private Lock consumerLock = new ReentrantLock();
	
	/**
	 * 初始化
	 * @date 2016年1月19日 上午9:16:13 
	 * @author yxl
	 */
	public synchronized static void init(){
		if(connectionFactory!=null) return;//若已初始化，不執行下面代碼
		//讀取用戶密碼配置
		String user = ActiveMQConnection.DEFAULT_USER;
		if(properties.getProperty("SERVER_USER")!=null) user = properties.getProperty("SERVER_USER");
		String password = ActiveMQConnection.DEFAULT_PASSWORD;
		if(properties.getProperty("SERVER_PASSWORD")!=null) password = properties.getProperty("SERVER_PASSWORD");
		//初始化連接工廠
		connectionFactory = new ActiveMQConnectionFactory(user, password, properties.getProperty("SERVER_ADDR"));
	}
	
	/**
	 * 創建ActivemqClient
	 * @date 2016年1月19日 上午10:40:26 
	 * @author yxl
	 */
	public ActivemqClient(){
		this(null);
	}
	
	/**
	 * 創建ActivemqClient
	 *	@param clientId 客戶端ID
	 * @date 2016年1月19日 上午10:40:26 
	 * @author yxl
	 */
	public ActivemqClient(String clientId){
		if(connectionFactory==null) init();//若連接工廠沒有初始化，先初始化
		try {
			connection = connectionFactory.createConnection();//創建連接
			if(clientId!=null) connection.setClientID(clientId);//設置客戶端ID
			connection.start();//開啟連接
			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);//創建session，開啟事務
		} catch (JMSException e) {
			logger.error("Create ActivemqClient error", e);
		}
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(connection!=null) connection.close();//對象被回收時，關閉連接
	}
	
	
	/**
	 * 根據隊列/主題名稱獲取消息生產者
	 * 一個Activemq客戶端可以有多個生產者，把已經實例化的生產者緩存到對象中
	 * @param name 隊列/主題 名稱
	 * @param msgType 消息類別
	 * @date 2016年1月19日 上午9:11:16 
	 * @author yxl
	 * @return 
	 */
	public MessageProducer getProducer(String name, MsgType msgType){
		if(producerMap.containsKey(name)) return producerMap.get(name);//如果緩存中存在生產者，直接從緩存中獲取
		
		producerLock.lock();
		//獲取到鎖
		try{
			if(producerMap.containsKey(name)) return producerMap.get(name);//獲取到鎖之後，有必要再從緩存中獲取一次
			//根據隊列創建消息生產者
			MessageProducer producer = null;
			if(msgType==MsgType.P2P) producer = session.createProducer(session.createQueue(name));
			else if(msgType==MsgType.Broadcast) producer = session.createProducer(session.createTopic(name));
			//是否持久化
			if(properties.getProperty("DELIVERY_MODE").equals("0")) producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			else producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producerMap.put(name, producer);
			return producer;
		} catch (JMSException e) {
			logger.error("Create Activemq MessageProducer error", e);
			return null;
		} finally{
			producerLock.unlock();
		}
	}
	
	
	/**
	 * 根據隊列/主題名稱獲取消息消費者
	 * 一個Activemq客戶端可以有多個消費者，把已經實例化的消費者緩存到對象中
	 * @param name 隊列/主題 名稱
	 * @param msgType 消息類別
	 * @date 2016年1月19日 上午9:11:16 
	 * @author yxl
	 * @return 
	 */
	public MessageConsumer getConsumer(String name, MsgType msgType){
		if(consumerMap.containsKey(name)) return consumerMap.get(name);//如果緩存中存在消費者，直接從緩存中獲取
		
		consumerLock.lock();
		//獲取到鎖
		try{
			if(consumerMap.containsKey(name)) return consumerMap.get(name);//獲取到鎖之後，有必要再從緩存中獲取一次
			//根據隊列創建消息消費者
			MessageConsumer consumer = null;
			if(msgType==MsgType.P2P) consumer = session.createConsumer(session.createQueue(name));
			else if(msgType==MsgType.Broadcast) consumer = session.createConsumer(session.createTopic(name));
			consumerMap.put(name, consumer);
			return consumer;
		} catch (JMSException e) {
			logger.error("Create Activemq MessageConsumer error", e);
			return null;
		} finally{
			consumerLock.unlock();
		}
	}
	
	
	/**
	 * 發送消息
	 * @param msg 消息內容
	 * @param queueName 隊列名稱
	 * @date 2016年1月19日 上午9:23:50 
	 * @author yxl
	 * @throws JMSException 
	 */
	public void sendMessage(String msg, String queueName) throws JMSException{
		MessageProducer producer = getProducer(queueName, MsgType.P2P);
		producer.send(session.createTextMessage(msg));
		session.commit();
	}
	
	
	/**
	 * 廣播消息
	 * @param msg 消息內容
	 * @param topicName 主題名稱
	 * @date 2016年1月19日 上午9:23:50 
	 * @author yxl
	 * @throws JMSException 
	 */
	public void broadcast(String msg, String topicName) throws JMSException{
		MessageProducer producer = getProducer(topicName, MsgType.Broadcast);
		producer.send(session.createTextMessage(msg));
		session.commit();
	}
}
