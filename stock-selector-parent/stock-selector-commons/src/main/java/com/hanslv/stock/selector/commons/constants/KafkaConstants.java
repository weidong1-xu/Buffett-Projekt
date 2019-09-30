package com.hanslv.stock.selector.commons.constants;
/**
 * Kafka相关常量
 * 
 * ------------------------------------
 * 1、Consumer阻塞队列大小										CONSUMER_BLOCKINGQUEUE_SIZE
 * 2、Kafka中股票价格Topic										PRICE_INFO_TOPCI_NAME
 * ------------------------------------
 * @author hanslv
 *
 */
public class KafkaConstants {
	public static final int CONSUMER_BLOCKINGQUEUE_SIZE = 5000;//Consumer阻塞队列大小
	public static final String PRICE_INFO_TOPCI_NAME = "priceInfo";//Kafka中股票价格Topic
}
