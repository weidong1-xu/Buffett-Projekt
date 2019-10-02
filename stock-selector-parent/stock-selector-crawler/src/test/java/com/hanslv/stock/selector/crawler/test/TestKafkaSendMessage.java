package com.hanslv.stock.selector.crawler.test;

import com.hanslv.stock.selector.crawler.StockPriceCrawler;

/**
 * 测试向KafkaConsumer发送消息
 * @author hanslv
 *
 */
public class TestKafkaSendMessage {
	/**
	 * 调用StockPriceCrawler向KafkaConsumer发送消息
	 */
	public static void main(String[] args) {
		new Thread(new StockPriceCrawler()).start();
	}
}
