package com.hanslv.stock.selector.commons.constants;

/**
 * Redis常量类
 * 
 * -----------------------------------------------
 * 1、股票价格信息消费者开关Key													PRICE_INFO_CONSUMER_SIGN_REDIS_KEY
 * 2、股票价格信息消费者停止value													PRICE_INFO_CONSUMER_SIGN_REDIS_VALUE
 * 3、股票价格信息消费者开关超时时间												PRICE_INFO_CONSUMER_SIGN_EXPIRE_TIME
 * -----------------------------------------------
 * @author hanslv
 *
 */
public abstract class CommonsRedisConstants {
	public static final String PRICE_INFO_CONSUMER_SIGN_REDIS_KEY = "priceInfoSign";//股票价格信息消费者开关Key
	public static final String PRICE_INFO_CONSUMER_SIGN_REDIS_VALUE = "on";//股票价格信息消费者停止value
	public static final long PRICE_INFO_CONSUMER_SIGN_EXPIRE_TIME = 20 * 1000;//股票价格信息消费者开关超时时间
}
