package com.hanslv.stock.selector.commons.constants;
/**
 * Kafka相关常量
 * 
 * ------------------------------------
 * 1、Consumer阻塞队列大小										CONSUMER_BLOCKINGQUEUE_SIZE
 * 2、Kafka中股票价格Topic										PRICE_INFO_TOPCI_NAME
 * 3、Producer线程池大小											PRODUCER_THREAD_POOL_SIZE
 * 4、股票价格信息消费者组								PRICE_INFO_CONSUMER_GROUP
 * ------------------------------------
 * @author hanslv
 *
 */
public abstract class CommonsKafkaConstants {
	public static final int CONSUMER_BLOCKINGQUEUE_SIZE = 5000;//Consumer阻塞队列大小
	public static final String PRICE_INFO_TOPCI_NAME = "priceInfo";//Kafka中股票价格Topic
	public static final int PRODUCER_THREAD_POOL_SIZE = 20;//Producer线程池大小
	public static final String PRICE_INFO_CONSUMER_GROUP = "priceInfoGroup";//股票价格信息消费者组
}
