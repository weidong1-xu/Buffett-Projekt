package com.hanslv.stock.selector.algorithm.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.algorithm.priceInfo.consumer.PriceInfoConsumer;
import com.hanslv.stock.selector.algorithm.starter.AlgorithmServiceStarter;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;

/**
 * 测试从Kafka中接收股票价格信息
 * @author hanslv
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AlgorithmServiceStarter.class)
public class TestReceivePriceInfoMessageFromTopic {
	@Autowired
	private PriceInfoConsumer messageTransUtil;
	
	
	/**
	 * 接收信息
	 */
	@Test
	public void receiveMessage() {
		/*
		 * 向线程池提交多个任务
		 */
		for(int i = 0 ; i < CommonsOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) {
			messageTransUtil.getPriceInfoFromKafka();
		}
		
		
		while(true) {}
	}
}
