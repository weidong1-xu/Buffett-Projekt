package com.hanslv.stock.selector.commons.util;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.commons.constants.CommonsKafkaConstants;

/**
 * Kafka工具类
 * 
 * --------------------------------------------
 *1、向指定的topic发送多条消息												public void sendMessage(String topic , K key , List<V> messageList , String flagName , String flagValue)
 * 2、向Consumer消息队列中写入消息											public void writeToConsumerBlockingQueue(V value)
 * 3、从Consumer消息队列中获取一个消息										public V takeValueFromConsumerBlockingQueue()
 * --------------------------------------------
 * @author hanslv
 *
 */
@Component
public class KafkaUtil<K , V> {
	Logger logger = Logger.getLogger(KafkaUtil.class);
	
	/*
	 * KafkaTemplate对象
	 */
	@Autowired
	private KafkaTemplate<K , V> kafkaTemplate;
	
	/*
	 * 存放全部线程从Kafka取回的数据
	 */
	private BlockingQueue<V> priceInfoBlockingQueue;
	
	public KafkaUtil() {
		/*
		 * 实例化阻塞队列
		 */
		priceInfoBlockingQueue = new ArrayBlockingQueue<>(CommonsKafkaConstants.CONSUMER_BLOCKINGQUEUE_SIZE);
	}
	
	/**
	 * 1、向指定的topic发送多条消息
	 * @param topic 
	 * @param key partition策略Key
	 * @param messageList 包含全部要发送消息的集合
	 * @param flagName
	 * @param flagValue
	 */
	public void sendMessage(String topic , K key , List<V> messageList , String flagName , String flagValue) {
//		kafkaTemplate.setProducerListener(producerListener);//指定回调逻辑
		/*
		 * 遍历要发送的消息List
		 */
		for(V message : messageList) {
//			logger.info(Thread.currentThread() + " 向Topic：" + topic + "发送了一条消息：" + String.valueOf(message));
			try {
				if(key != null)
					kafkaTemplate.send(topic , key , message).get();
				else
					kafkaTemplate.send(topic , message).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	

	/*
	 * 2、向Consumer消息队列中写入消息
	 */
	public void writeToConsumerBlockingQueue(V value) {
		try {
			priceInfoBlockingQueue.put(value);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
}
