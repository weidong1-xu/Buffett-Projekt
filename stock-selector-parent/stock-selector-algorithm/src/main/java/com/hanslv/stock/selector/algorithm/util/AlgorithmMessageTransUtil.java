package com.hanslv.stock.selector.algorithm.util;

import com.hanslv.stock.selector.commons.util.KafkaUtil;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmKafkaConstants;
import com.hanslv.stock.selector.algorithm.dbTabSelectLogic.DbTabSelectLogic;
import com.hanslv.stock.selector.algorithm.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.commons.constants.CommonsKafkaConstants;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 数据传输工具类，单例
 * 
 * ------------------------------------------
 * 1、返回MessageTransUtil单例										public static AlgorithmMessageTransUtil getInstance()
 * 2、从Kafka消费消息并插入数据库										public void getPriceInfoFromKafka()
 * ------------------------------------------
 * @author hanslv
 *
 */
public class AlgorithmMessageTransUtil {
	Logger logger = Logger.getLogger(AlgorithmMessageTransUtil.class);
	
	private static class Singleton{
		private static AlgorithmMessageTransUtil INSTANCE = new AlgorithmMessageTransUtil();
	}
	
	private AlgorithmMessageTransUtil() {}
	
	
	
	/**
	 * 1、返回MessageTransUtil单例
	 * @return
	 */
	public static AlgorithmMessageTransUtil getInstance() {
		return Singleton.INSTANCE;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 2、从Kafka消费消息并插入数据库
	 */
	public void getPriceInfoFromKafka() {
		/*
		 * 设置Topic
		 */
		List<String> topicList = new ArrayList<>();
		topicList.add(CommonsKafkaConstants.PRICE_INFO_TOPCI_NAME);
		
		/*
		 * 创建N个Consumer并设置在同一消费者组，同时消费消息并落库
		 */
		for(int i = 0 ; i < AlgorithmKafkaConstants.CONSUMER_COUNT ; i++) {
//		for(int i = 0 ; i < 1 ; i++) {
			KafkaUtil<String , TabStockPriceInfo> kafkaUtil = new KafkaUtil<>(AlgorithmKafkaConstants.KAFKA_PROP_PATH);
			
			/*
			 * 消费消息
			 */
			kafkaUtil.pollMessage(topicList , AlgorithmKafkaConstants.BROKER_TIME_OUT_LIMIT);
			
			/*
			 * 创建新线程将消费消息插入数据库
			 */
			new Thread(() -> {
				TabStockPriceInfoRepository priceInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockPriceInfoRepository.class);
				TabStockInfoRepository stockInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockInfoRepository.class);
				try {
					while(true) {
						/*
						 * 获取一条股票价格信息
						 */
						TabStockPriceInfo currentPriceInfoMessage = kafkaUtil.takeValueFromConsumerBlockingQueue();
						logger.info("-------------------" + Thread.currentThread() + " 从Kafka获取到一条消息：" + currentPriceInfoMessage + "-------------------");
						
						/*
						 * 计算分表表名
						 */
						String tableName = DbTabSelectLogic.tableSelector(currentPriceInfoMessage , stockInfoMapper);
						logger.info(tableName);
						
						/*
						 * 将价格信息落库
						 */
						priceInfoMapper.insertOne(tableName, currentPriceInfoMessage);
						MyBatisUtil.getInstance().commitConnection();
					}
				}finally {
					MyBatisUtil.getInstance().closeConnection();
				}
			}).start();
		}
	}
}
