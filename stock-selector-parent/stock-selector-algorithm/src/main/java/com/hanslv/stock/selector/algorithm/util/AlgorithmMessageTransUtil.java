package com.hanslv.stock.selector.algorithm.util;

import com.hanslv.stock.selector.commons.util.KafkaUtil;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.algorithm.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 数据传输工具类
 * 
 * ------------------------------------------
 * 1、从Kafka消费消息并插入数据库										public void getPriceInfoFromKafka()
 * ------------------------------------------
 * @author hanslv
 *
 */
@Component
public class AlgorithmMessageTransUtil {
	
	Logger logger = Logger.getLogger(AlgorithmMessageTransUtil.class);
	
	
	@Autowired
	private KafkaUtil<String , TabStockPriceInfo> kafkaUtil;
	
	/**
	 * 1、从Kafka消费消息并插入数据库
	 */
	public void getPriceInfoFromKafka() {
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
	}
	
}
