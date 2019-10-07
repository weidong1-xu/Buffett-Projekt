package com.hanslv.stock.selector.algorithm.util;

import com.hanslv.stock.selector.commons.util.KafkaUtil;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.algorithm.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.commons.constants.CommonsKafkaConstants;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 数据传输工具类
 * 在使用后需要关闭内置线程池
 * ------------------------------------------
 * 1、从Kafka消费消息并插入数据库										public void getPriceInfoFromKafka()
 * 2、关闭内置线程池													public void shutdownThreadPool()
 * ------------------------------------------
 * @author hanslv
 *
 */
@Component
public class AlgorithmMessageTransUtil {
	
	Logger logger = Logger.getLogger(AlgorithmMessageTransUtil.class);
	
	@Autowired
	private KafkaUtil<String , TabStockPriceInfo> kafkaUtil;
	
	/*
	 * 内置线程池
	 */
	private static ExecutorService threadPool;
	
	/**
	 * 1、从Kafka消费消息并插入数据库
	 */
	public void getPriceInfoFromKafka() {
		/*
		 * 启动内置线程池
		 */
		startupThreadPool();
		
		/*
		 * 向线程池提交任务
		 */
		threadPool.execute(() -> {
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
					String tableName = DbTabSelectLogicUtil.tableSelector4PriceInfo(currentPriceInfoMessage , stockInfoMapper);
					
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
	
	
	/**
	 * 2、关闭内置线程池
	 */
	public void shutdownThreadPool() {
		threadPool.shutdown();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 从Topic中获取股票价格消息并写入到消息队列中
	 * @param topicList
	 * @param timeout
	 * @return
	 */
	@KafkaListener(topics = CommonsKafkaConstants.PRICE_INFO_TOPCI_NAME , containerFactory = "stockPriceInfoKafkaListenerContainerFactory")
	public void pollMessageFromPriceInfoTopic(ConsumerRecord<String , TabStockPriceInfo> priceInfoMessage) {
		kafkaUtil.writeToConsumerBlockingQueue(priceInfoMessage.value());
	}	
	
	
	
	
	/**
	 * 启动内置线程池
	 */
	private void startupThreadPool() {
		if(threadPool == null || threadPool.isTerminated())
			threadPool = Executors.newFixedThreadPool(CommonsOtherConstants.BASIC_THREAD_POOL_SIZE);
	}
	
}
