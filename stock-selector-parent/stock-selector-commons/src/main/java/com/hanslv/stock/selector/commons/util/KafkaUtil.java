package com.hanslv.stock.selector.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.jboss.logging.Logger;

/**
 * Kafka工具类
 * 需要在引入commons模块的其他模块下自行配置kafka.properties
 * ！！！注意：一个KafkaUtil实例只可充当KafkaProducer或KafkaConsumer之一，不可得兼，除非调用关闭连接方法，否则会出现异常！！！
 * --------------------------------------------
 * 
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
	 * 创建一个KafkaProducer实例，
	 * 如果已经使用当前KafkaUtil创建过一个并且当前是打开状态的KafkaProducer实例，则会返回该KafkaProducer实例，
	 * 否则会创建一个新的实例
	 * @return
	 */
	private KafkaProducer<K , V> createKafkaProducer(){
		if(currentConsumerInstance != null) {
			try {
				throw new Exception("当前KafkaUtil已经充当Consumer，不可以同时使用Producer相关方法，若需要请先关闭当前的Consumer连接！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(currentProducerInstance != null) 
			return currentProducerInstance;
		else {
			logger.info(Thread.currentThread() + " 创建了一个KafkaProducer");
			currentProducerInstance = new KafkaProducer<>(currentProp);
			return currentProducerInstance;
		}
	}
	
	
	/**
	 * 创建一个KafkaConsumer实例，
	 * 如果已经使用当前KafkaUtil创建过一个并且当前是打开状态的KafkaConsumer实例，则会返回该KafkaConsumer实例，
	 * 否则会创建一个新的实例
	 * @return
	 */
	private KafkaConsumer<K , V> createKafkaConsumer(){
		if(currentProducerInstance != null) {
			try {
				throw new Exception("当前KafkaUtil已经充当Producer，不可以同时使用Consumer相关方法，若需要请先关闭当前的Producer连接！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(currentConsumerInstance != null) 
			return currentConsumerInstance;
		else {
			logger.info(Thread.currentThread() + " 创建了一个KafkaProducer");
			currentConsumerInstance = new KafkaConsumer<>(currentProp);
			return currentConsumerInstance;
		}
	}
	
	
}
