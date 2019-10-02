package com.hanslv.stock.selector.algorithm.test;

import com.hanslv.stock.selector.algorithm.util.AlgorithmMessageTransUtil;

public class TestReceiveMessageFromKafka {
	/**
	 * 测试从Kafka接收消息并落库
	 */
	public static void main(String[] args) {
		AlgorithmMessageTransUtil.getInstance().getPriceInfoFromKafka();
	}
}
