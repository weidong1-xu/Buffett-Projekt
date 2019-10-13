package com.hanslv.stock.selector.commons.constants;
/**
 * 公用常量
 * 
 * -----------------------------------------------
 * 1、项目根目录									CLASS_PATH
 * 2、基本线程池大小								BASIC_THREAD_POOL_SIZE
 * 3、基本阻塞队列大小							BASIC_BLOCKING_QUEUE_SIZE
 * 4、上证指数股票代码							SHANGZHENG_ZHISHU_STOCK_CODE
 * 5、上证指数股票名称							SHANGZHENG_ZHISHU_STOCK_NAME
 * 6、深证指数股票代码							SHENZHENG_ZHISHU_STOCK_CODE
 * 7、深证指数股票名称							SHENZHENG_ZHISHU_STOCK_NAME
 * 8、创业板指数股票代码							CHUANGYEBAN_ZHISHU_STOCK_CODE
 * 9、创业板指数股票名称							CHUANGYEBAN_ZHISHU_STOCK_NAME
 * -----------------------------------------------
 * @author harrylu
 *
 */
public abstract class CommonsOtherConstants {
	public static final String CLASS_PATH = CommonsOtherConstants.class.getClassLoader().getResource("").toString().replaceAll("file:/", "");//项目根目录
	public static final int BASIC_THREAD_POOL_SIZE = 20;//基本线程池大小
	public static final int BASIC_BLOCKING_QUEUE_SIZE = 500;//基本阻塞队列大小
	
	
	
	public static final String SHANGZHENG_ZHISHU_STOCK_CODE = ".000001";//上证指数股票代码
	public static final String SHANGZHENG_ZHISHU_STOCK_NAME = "上证指数";//上证指数股票名称
	
	public static final String SHENZHENG_ZHISHU_STOCK_CODE = ".399001";//深证指数股票代码
	public static final String SHENZHENG_ZHISHU_STOCK_NAME = "深证指数";//深证指数股票名称
	
	public static final String CHUANGYEBAN_ZHISHU_STOCK_CODE = ".399006";//创业板指数股票代码
	public static final String CHUANGYEBAN_ZHISHU_STOCK_NAME = "创业板指数";//创业板指数股票名称
}
