package com.hanslv.stock.selector.commons.constants;

/**
 * Redis常量类
 * 
 * -----------------------------------------------
 * 1、股票价格信息消费者开关Key													PRICE_INFO_CONSUMER_SIGN_REDIS_KEY
 * 2、股票价格信息消费者停止value													PRICE_INFO_CONSUMER_SIGN_REDIS_VALUE
 * 3、股票价格信息消费者开关超时时间												PRICE_INFO_CONSUMER_SIGN_EXPIRE_TIME
 * 4、股票价格信息数据超时时间													PRICE_INFO_EXPIRE
 * 5、超时时间随机值范围开始														PRICE_INFO_EXPIRE_RANDOM_START
 * 6、超时时间随机值范围结束														PRICE_INFO_EXPIRE_RANDOM_END
 * -----------------------------------------------
 * @author hanslv
 *
 */
public abstract class CommonsRedisConstants {
	public static final String PRICE_INFO_CONSUMER_SIGN_REDIS_KEY = "priceInfoSign";//股票价格信息消费者开关Key
	public static final String PRICE_INFO_CONSUMER_SIGN_REDIS_VALUE = "on";//股票价格信息消费者停止value
	public static final long PRICE_INFO_CONSUMER_SIGN_EXPIRE_TIME = 20 * 1000;//股票价格信息消费者开关超时时间
	
	public static final long PRICE_INFO_EXPIRE = 60 * 60 * 24;//股票价格信息数据超时时间
	public static final int PRICE_INFO_EXPIRE_RANDOM_START = 0;//超时时间随机值范围开始
	public static final int PRICE_INFO_EXPIRE_RANDOM_END = 300;//超时时间随机值范围结束
}
