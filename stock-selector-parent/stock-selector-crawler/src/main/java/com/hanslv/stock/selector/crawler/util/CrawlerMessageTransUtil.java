package com.hanslv.stock.selector.crawler.util;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.commons.constants.CommonsKafkaConstants;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.KafkaUtil;
import com.hanslv.stock.selector.crawler.constants.CrawlerKafkaConstants;

/**
 * 消息传输工具类
 * 包括股票价格信息消息队列以及与其他中间件模块交互的方法
 * 其中监听priceInfoMessageQueue的线程不会被关闭
 * ---------------------------------------
 * 1、获取KafkaUtil实例											public static KafkaUtil getInstance()
 * 2、向priceInfoMessageQueue写入一组消息						public void writeAMessageIntoPriceInfoMessageQueue(List<TabStockPriceInfo> priceInfoList)
 * ---------------------------------------
 * @author hanslv
 *
 */
@Component
public class CrawlerMessageTransUtil {
	Logger logger;
	
	/*
	 * 股票价格信息消息队列，
	 * 爬虫线程会将爬回的股票价格信息（TabStockPriceInfo）以每个线程一次爬回一个List的形式写入到这个队列中，
	 * 在当前Util中会有一个线程持续的监听这个队列，当有消息写入时就take一个List<TabStockPriceInfo>并写入到Kafka对应的Topic中，
	 * 当消息停止写入时，监听的线程会阻塞等待
	 */
	private BlockingQueue<List<TabStockPriceInfo>> priceInfoMessageQueue;
	
	
	@Autowired
	private KafkaUtil<String , TabStockPriceInfo> kafkaUtil;

	
	
	public CrawlerMessageTransUtil() {
		logger = Logger.getLogger(CrawlerMessageTransUtil.class);
		
		/*
		 * priceInfoMessageQueue初始化消息队列
		 */
		priceInfoMessageQueue = new ArrayBlockingQueue<>(CrawlerKafkaConstants.PRICE_INFO_MESSAGE_QUEUE_SIZE);
		
		
		/*
		 * 启动线程，向Kafka的topic中写入消息，该线程不关闭
		 */
		new Thread(() -> {
			try {
				/*
				 * 监听priceInfoMessageQueue，从中获取股票价格List
				 */
				while(true) {
					List<TabStockPriceInfo> currentPriceInfoList = priceInfoMessageQueue.take();
					
					/*
					 * 向Kafka broker发送消息
					 */
					writeToKafkaTopic(CommonsKafkaConstants.PRICE_INFO_TOPCI_NAME , null , currentPriceInfoList);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	
	/**
	 * 2、向priceInfoMessageQueue写入一组消息
	 * @param priceInfoList
	 */
	public void writeAMessageIntoPriceInfoMessageQueue(List<TabStockPriceInfo> priceInfoList) {
		try {
			priceInfoMessageQueue.put(priceInfoList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 向Kafka的Topic写入消息
	 * @param topic
	 * @param key
	 * @param value
	 */
	private void writeToKafkaTopic(String topic , String key , List<TabStockPriceInfo> value) {
		/*
		 * 发送一个MessageList
		 */
		kafkaUtil.sendMessage(topic, key, value);
	}
}
