package com.hanslv.stock.selector.crawler.util;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.commons.constants.CommonsKafkaConstants;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.constants.CommonsRedisConstants;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.KafkaUtil;

/**
 * 消息传输工具类
 * 包括股票价格信息消息队列以及与其他中间件模块交互的方法
 * ---------------------------------------
 * 1、向队列中写入股票价格信息List						public void writePriceInfoToQueue(List<TabStockPriceInfo> priceInfoList)
 * ---------------------------------------
 * @author hanslv
 *
 */
@Component
public class CrawlerMessageTransUtil {
	Logger logger = Logger.getLogger(CrawlerMessageTransUtil.class);
	
	@Autowired
	@Qualifier("stockPriceInfoBlockingQueue")
	private BlockingQueue<List<TabStockPriceInfo>> stockPriceInfoQueue;
	
	/**
	 * 1、向队列中写入股票价格信息List
	 * @param priceInfoList
	 */
	public void writePriceInfoToQueue(List<TabStockPriceInfo> priceInfoList) {
		/*
		 * 尝试初始化线程池
		 */
//		startThreadPool();
		
		/*
		 * 将股票价格信息写入消息队列
		 */
		try {
			stockPriceInfoQueue.put(priceInfoList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 启动内置线程池
	 */
	@Deprecated
	private void startThreadPool() {
		if(threadPool == null || threadPool.isTerminated())
			threadPool = Executors.newFixedThreadPool(CommonsOtherConstants.BASIC_THREAD_POOL_SIZE);
	}
	
	/**
	 * 向Kafka的Topic写入消息
	 * @param topic
	 * @param key
	 * @param value
	 */
	@Deprecated
	private void writeToKafkaTopic(String topic , String key , List<TabStockPriceInfo> value) {
		/*
		 * 发送一个MessageList
		 */
		kafkaUtil.sendMessage(topic, key, value , CommonsRedisConstants.PRICE_INFO_CONSUMER_SIGN_REDIS_KEY , CommonsRedisConstants.PRICE_INFO_CONSUMER_SIGN_REDIS_VALUE);
	}
	
	/**
	 * @param priceInfoList
	 */
	@Autowired
	private KafkaUtil<String , TabStockPriceInfo> kafkaUtil;
	@Deprecated
	public void writePriceInfoListToKafkaByThreadPool(List<TabStockPriceInfo> priceInfoList) {
		/*
		 * 启动内置线程池
		 */
		startThreadPool();
		
		/*
		 * 向线程池提交任务，将消息List插入Kafka
		 */
		threadPool.execute(() -> {
			/*
			 * 向Kafka broker发送消息
			 */
			writeToKafkaTopic(CommonsKafkaConstants.PRICE_INFO_TOPCI_NAME , null , priceInfoList);
		});
	}
	/**
	 * 
	 */
	private static ExecutorService threadPool;
	@Deprecated
	public void shudDownThreadPool() {
		threadPool.shutdown();
	}
}
