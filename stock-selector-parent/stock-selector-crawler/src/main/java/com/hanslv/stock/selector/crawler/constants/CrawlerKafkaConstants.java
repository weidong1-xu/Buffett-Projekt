package com.hanslv.stock.selector.crawler.constants;

/**
 * Kafka常量
 * 
 * --------------------------------
 * 1、股票价格信息消息队列大小										PRICE_INFO_MESSAGE_QUEUE_SIZE
 * --------------------------------
 * @author hanslv
 *
 */
public abstract class CrawlerKafkaConstants {
	public static final int PRICE_INFO_MESSAGE_QUEUE_SIZE = 1000;//股票价格信息消息队列大小
	public static final String KAFKA_PROP_PATH = "/crawler-kafka.properties";//Kafka配置文件路径
}
