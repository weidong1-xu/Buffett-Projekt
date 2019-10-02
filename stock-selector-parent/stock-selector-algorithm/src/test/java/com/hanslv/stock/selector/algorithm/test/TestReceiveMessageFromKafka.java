package com.hanslv.stock.selector.algorithm.test;

import org.junit.Test;

import com.hanslv.stock.selector.algorithm.util.AlgorithmMessageTransUtil;

public class TestReceiveMessageFromKafka {
	/**
	 * 测试从Kafka接收消息并落库
	 */
	@Test
	public void getMessageFromKafkaAndIntoDb() {
		AlgorithmMessageTransUtil.getInstance().getPriceInfoFromKafka();
	}
}
