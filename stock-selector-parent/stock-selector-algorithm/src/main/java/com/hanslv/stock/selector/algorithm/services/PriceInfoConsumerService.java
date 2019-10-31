package com.hanslv.stock.selector.algorithm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.selector.algorithm.priceInfo.consumer.PriceInfoConsumer;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;

/**
 * 股票价格信息消费者Service
 * 
 * -------------------------------------------
 * 1、运行股票价格信息消费者端									public void runConsumer()
 * -------------------------------------------
 * @author admin
 *
 */
@Service
@Deprecated
public class PriceInfoConsumerService {
	@Autowired
	private PriceInfoConsumer priceInfoConsumer;
	
	/**
	 * 1、运行股票价格信息消费者端
	 */
	public void runConsumer() {
		for(int i = 0 ; i < CommonsOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) priceInfoConsumer.getPriceInfoFromKafka();
	}
}
