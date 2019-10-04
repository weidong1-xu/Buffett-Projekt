package com.hanslv.stock.selector.algorithm.util;

import com.hanslv.stock.selector.commons.util.KafkaUtil;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmDbConstants;
import com.hanslv.stock.selector.algorithm.constants.AlgorithmKafkaConstants;
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
		 * 创建N个Consumer并设置在同一消费者组，每个Consumer对应一个线程池向数据库插入数据
		 */
		for(int i = 0 ; i < AlgorithmKafkaConstants.CONSUMER_COUNT ; i++) {
			/*
			 * 新起线程池
			 */
			ExecutorService executor = Executors.newFixedThreadPool(AlgorithmDbConstants.STOCK_PRICE_INFO_INSERTING_THREAD_POOL_SIZE);
			
			
			KafkaUtil<String , TabStockPriceInfo> kafkaUtil = new KafkaUtil<>(AlgorithmKafkaConstants.KAFKA_PROP_PATH);
			
			/*
			 * 消费消息
			 */
			kafkaUtil.pollMessage(topicList , AlgorithmKafkaConstants.BROKER_TIME_OUT_LIMIT);
			
			
			/*
			 * 向线程池提交Runnable，从对应KafkaUtil中的消息队列中取出并插入数据
			 */
			for(int j = 0 ; j < AlgorithmDbConstants.STOCK_PRICE_INFO_INSERTING_THREAD_POOL_SIZE ; j++) {
				executor.execute(() -> {
					TabStockPriceInfoRepository priceInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockPriceInfoRepository.class);
					TabStockInfoRepository stockInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockInfoRepository.class);
					try {
						while(true) {
							/*
							 * 获取一条股票价格信息
							 */
							TabStockPriceInfo currentPriceInfoMessage = kafkaUtil.takeValueFromConsumerBlockingQueue();
							
							/*
							 * 计算分表表名
							 */
							String tableName = DbTabSelectLogicUtil.tableSelector(currentPriceInfoMessage , stockInfoMapper);
							
							/*
							 * 判断当前信息是否存在（已爬取过）
							 */
							if(priceInfoMapper.selectOne(tableName, currentPriceInfoMessage) == null) {
								/*
								 * 将价格信息落库
								 */
								priceInfoMapper.insertOne(tableName, currentPriceInfoMessage);
								MyBatisUtil.getInstance().commitConnection();
								logger.info("插入了一条数据：" + currentPriceInfoMessage);
							}else
								logger.error("当前数据存在，已经跳过：" + currentPriceInfoMessage);
						}
					}finally {
						MyBatisUtil.getInstance().closeConnection();
					}
				});
			}
		}
	}
}
