package com.hanslv.stock.selector.algorithm.constants;

/**
 * Kafka常量
 * 
 * -------------------------------------
 * 1、kafka配置文件路径									KAFKA_PROP_PATH
 * 2、broker处理超时时间									BROKER_TIME_OUT_LIMIT
 * 3、KafkaConsumer数量									CONSUMER_COUNT
 * -------------------------------------
 * @author hanslv
 *
 */
public abstract class AlgorithmKafkaConstants {
	public static final String KAFKA_PROP_PATH = "/algorithm-kafka.properties";//kafka配置文件路径
	public static final long BROKER_TIME_OUT_LIMIT = 5000;//broker处理超时时间
	public static final int CONSUMER_COUNT = 30;//KafkaConsumer数量
}
