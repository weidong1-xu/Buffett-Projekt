package com.hanslv.stock.selector.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.logging.Logger;

import com.hanslv.stock.selector.commons.constants.KafkaConstants;

/**
 * Kafka工具类
 * 需要在引入commons模块的其他模块下自行配置kafka.properties，
 * 采取非阻塞的方式，在Producer发送消息、Consumer接收消息时都会创建新线程来完成工作，与主线程分开，
 * 每个KafkaUtil实例只支持实例化一个Consumer，多个会出现异常
 * 在使用后需要关闭资源
 * --------------------------------------------
 * 1、关闭当前currentProducerInstance										public void closeProducerConnection()
 * 2、关闭当前currentConsumerInstance										public void closeConcumerConnection()
 * 3、向指定的topic发送多条消息												public void sendMessage(String topic , K key , List<V> messageList , Callback callbackLogic)
 * 4、从Topic中获取消息														public void pollMessage(List<String> topicList , long timeout)
 * 5、从Consumer消息队列中获取一个消息										public V takeValueFromConsumerBlockingQueue()
 * --------------------------------------------
 * @author hanslv
 *
 */
public class KafkaUtil<K , V> {
	Logger logger = Logger.getLogger(KafkaUtil.class);
	
	/*
	 * 当前KafkaUtil实例的properties对象
	 */
	private Properties currentProp;
	
	/*
	 * 当前实例中包含的Producer、Consumer实例
	 */
	private KafkaProducer<K , V> currentProducerInstance;
	private KafkaConsumer<K , V> currentConsumerInstance;
	
	
	/*
	 * 记录Consumer消费消息的消息队列
	 */
	private BlockingQueue<V> consumerBlockingQueue;

	
	/**
	 * 构造方法，需要传入一个当前KafkaUtil实例对应的Properties在当前项目中的相对路径并且文件为UTF-8编码格式，
	 * 例如当前位于src/main/resource目录的根路径下，则传入/kafka.properties
	 * @param propPath
	 */
	public KafkaUtil(String propPath) {
		/*
		 * 加载当前实例的配置文件
		 */
		try(InputStream inputStream = KafkaUtil.class.getResourceAsStream(propPath);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8")){
			currentProp.load(inputStreamReader);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 1、关闭当前currentProducerInstance
	 */
	public void closeProducerConnection() {
		if(currentProducerInstance != null) {
			currentProducerInstance.close();
			currentProducerInstance = null;
			logger.info(Thread.currentThread() + " 关闭了一个KafkaProducer");
		}
	}
	
	/**
	 * 2、关闭当前currentConsumerInstance
	 */
	public void closeConcumerConnection() {
		if(currentConsumerInstance != null) {
			if(consumerBlockingQueue.size() > 0) {
				try {
					throw new Exception("当前Consumer消息队列中仍然存在未消费消息！");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			currentConsumerInstance.close();
			currentConsumerInstance = null;
			logger.info(Thread.currentThread() + " 关闭了一个KafkaConsumer");
		}
	}
	
	
	/**
	 * 3、向指定的topic发送多条消息
	 * 创建一个新线程，采用异步发送的方式，在新创建的线程中处理可重试异常
	 * @param topic 
	 * @param key partition策略Key
	 * @param messageList 包含全部要发送消息的集合
	 * @param callbackLogic 重试逻辑
	 */
	public void sendMessage(String topic , K key , List<V> messageList , Callback callbackLogic) {
		/*
		 * 实例化currentProducerInstance
		 */
		createKafkaProducer();
		new Thread(() -> {
			/*
			 * 遍历要发送的消息List
			 */
			for(V message : messageList) {
				logger.info(Thread.currentThread() + " 向Topic：" + topic + "发送了一条消息：" + String.valueOf(message));
				currentProducerInstance.send(new ProducerRecord<>(topic , key , message) , callbackLogic);
			}
		}).start();
	}
	
	
	/**
	 * 4、从Topic中获取消息
	 * 将会创建一个新线程并创建Consumer订阅指定的Topic，并将接收到的Topic写入到消息队列中
	 * @param topicList
	 * @param timeout
	 * @return
	 */
	public void pollMessage(List<String> topicList , long timeout) {
		/*
		 * 实例化currentConsumerInstance
		 */
		createKafkaConsumer(topicList);
		
		/*
		 * 启动新线程订阅
		 */
		new Thread(() -> {
			/*
			 * 订阅消息并写入到消息队列
			 */
			while(true) {
				ConsumerRecords<K , V> records = currentConsumerInstance.poll(Duration.ofMillis(timeout));
				for(ConsumerRecord<K , V> record : records) {
					try {
						/*
						 * 存入消息队列
						 */
						consumerBlockingQueue.put(record.value());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	/**
	 * 5、从Consumer消息队列中获取一个消息
	 * @return
	 */
	public V takeValueFromConsumerBlockingQueue() {
		try {
			return consumerBlockingQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 实例化currentProducerInstance
	 */
	private void createKafkaProducer(){
		if(currentProducerInstance == null) {
			logger.info(Thread.currentThread() + " 创建了一个KafkaProducer");
			currentProducerInstance = new KafkaProducer<>(currentProp);
		}
	}
	
	
	/**
	 * 实例化currentConsumerInstance
	 * @param topicList 订阅的TopicList
	 */
	private void createKafkaConsumer(List<String> topicList){
		if(currentConsumerInstance == null) {
			/*
			 * 实例化Consumer消息队列
			 */
			consumerBlockingQueue = new ArrayBlockingQueue<>(KafkaConstants.CONSUMER_BLOCKINGQUEUE_SIZE);
			
			logger.info(Thread.currentThread() + " 创建了一个KafkaConsumer");
			currentConsumerInstance = new KafkaConsumer<>(currentProp);
			
			/*
			 * 设置当前Consumer订阅某些Topic
			 */
			currentConsumerInstance.subscribe(topicList);
		}else {
			try {
				throw new Exception("只支持实例化一个Consumer，多个会出现异常！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
