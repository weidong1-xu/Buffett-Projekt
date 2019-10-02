package com.hanslv.stock.selector.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.logging.Logger;

import com.hanslv.stock.selector.commons.constants.CommonsKafkaConstants;

/**
 * Kafka工具类
 * 需要在引入commons模块的其他模块下自行配置kafka.properties，
 * 采取非阻塞的方式，在Producer发送消息、Consumer接收消息时都会创建新线程来完成工作，与主线程分开，
 * --------------------------------------------
 * 1、向指定的topic发送多条消息												public void sendMessage(String topic , K key , List<V> messageList , Callback callbackLogic)
 * 2、从Topic中获取消息														public void pollMessage(List<String> topicList , long timeout)
 * 3、从Consumer消息队列中获取一个消息										public V takeValueFromConsumerBlockingQueue()
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
	 * 当前KafkaUtil实例中包含的Producer、Consumer实例
	 */
	private ThreadLocal<KafkaProducer<K , V>> producerThreadLocal;
	private ThreadLocal<KafkaConsumer<K , V>> consumerThreadLocal;
	
	
	/*
	 * 存放全部线程从Kafka取回的数据
	 */
	BlockingQueue<V> priceInfoBlockingQueue;
	
	
	/**
	 * 构造方法，需要传入一个当前KafkaUtil实例对应的Properties在当前项目中的相对路径并且文件为UTF-8编码格式，
	 * 例如当前位于src/main/resource目录的根路径下，则传入/kafka.properties
	 * @param propPath
	 */
	public KafkaUtil(String propPath) {
		/*
		 * 加载当前实例的配置文件
		 */
		currentProp = new Properties();
		try(InputStream inputStream = KafkaUtil.class.getResourceAsStream(propPath);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8")){
			currentProp.load(inputStreamReader);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		producerThreadLocal = new ThreadLocal<>();
		consumerThreadLocal = new ThreadLocal<>();

		
		priceInfoBlockingQueue = new ArrayBlockingQueue<>(CommonsKafkaConstants.CONSUMER_BLOCKINGQUEUE_SIZE);
	}
	
	
	/**
	 * 1、向指定的topic发送多条消息
	 * 创建一个新线程，采用同步发送的方式等待发送结束后再发送下一条消息，在新创建的线程中处理可重试异常
	 * @param topic 
	 * @param key partition策略Key
	 * @param messageList 包含全部要发送消息的集合
	 * @param callbackLogic 重试逻辑
	 */
	public void sendMessage(String topic , K key , List<V> messageList , Callback callbackLogic) {
		new Thread(() -> {
			/*
			 * 实例化currentProducerInstance
			 */
			KafkaProducer<K , V> currentProducerInstance = createKafkaProducer();
			/*
			 * 遍历要发送的消息List
			 */
			try {
				for(V message : messageList) {
					logger.info(Thread.currentThread() + " 向Topic：" + topic + "发送了一条消息：" + String.valueOf(message));
					try {
						if(key != null)
							currentProducerInstance.send(new ProducerRecord<>(topic , key , message) , callbackLogic).get();
						else
							currentProducerInstance.send(new ProducerRecord<>(topic , message) , callbackLogic).get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			}finally {
				closeProducerConnection();
			}
		}).start();
	}
	
	
	/**
	 * 2、从Topic中获取消息
	 * 将会创建一个新线程并创建Consumer订阅指定的Topic，并将接收到的Topic写入到消息队列中
	 * @param topicList
	 * @param timeout
	 * @return
	 */
	public void pollMessage(List<String> topicList , long timeout) {
		/*
		 * 启动新线程订阅
		 */
		new Thread(() -> {
			/*
			 * 实例化currentConsumerInstance
			 */
			KafkaConsumer<K , V> currentConsumerInstance = createKafkaConsumer(topicList);
			
			/*
			 * 订阅消息并写入到消息队列
			 */
			try {
				while(true) {
					ConsumerRecords<K , V> records = currentConsumerInstance.poll(Duration.ofMillis(timeout));
					for(ConsumerRecord<K , V> record : records) {
						try {
							/*
							 * 存入消息队列
							 */
							priceInfoBlockingQueue.put(record.value());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}finally {
				closeConcumerConnection();
			}
		}).start();
	}
	
	
	/**
	 * 3、从Consumer消息队列中获取一条消息
	 * @return
	 */
	public V takeValueFromConsumerBlockingQueue() {
		try {
			return priceInfoBlockingQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 关闭当前currentProducerInstance
	 */
	private void closeProducerConnection() {
		KafkaProducer<K , V> currentProducerInstance = producerThreadLocal.get();
		if(currentProducerInstance != null) {
			currentProducerInstance.close();
			producerThreadLocal.remove();
			logger.info(Thread.currentThread() + " 关闭了一个KafkaProducer");
		}
	}
	
	/**
	 * 关闭当前currentConsumerInstance
	 */
	private void closeConcumerConnection() {
		KafkaConsumer<K , V> currentConsumerInstance = consumerThreadLocal.get();
		if(currentConsumerInstance != null) {
			currentConsumerInstance.close();
			consumerThreadLocal.remove();
			logger.info(Thread.currentThread() + " 关闭了一个KafkaConsumer");
		}
	}
	
	
	
	
	
	
	
	/**
	 * 实例化currentProducerInstance
	 */
	private KafkaProducer<K , V> createKafkaProducer(){
		KafkaProducer<K , V> currentProducerInstance = producerThreadLocal.get();
		if(currentProducerInstance == null) {
			logger.info(Thread.currentThread() + " 创建了一个KafkaProducer");
			currentProducerInstance = new KafkaProducer<>(currentProp);
			producerThreadLocal.set(currentProducerInstance);
		}
		return currentProducerInstance;
	}
	
	
	/**
	 * 实例化currentConsumerInstance
	 * @param topicList 订阅的TopicList
	 */
	private KafkaConsumer<K , V> createKafkaConsumer(List<String> topicList){
		KafkaConsumer<K , V> currentConsumerInstance = consumerThreadLocal.get();
		if(currentConsumerInstance == null) {
			
			logger.info(Thread.currentThread() + " 创建了一个KafkaConsumer");
			currentConsumerInstance = new KafkaConsumer<>(currentProp);
			
			/*
			 * 设置当前Consumer订阅某些Topic
			 */
			currentConsumerInstance.subscribe(topicList);
			consumerThreadLocal.set(currentConsumerInstance);
		}
		return currentConsumerInstance;
	}
	
	
}
