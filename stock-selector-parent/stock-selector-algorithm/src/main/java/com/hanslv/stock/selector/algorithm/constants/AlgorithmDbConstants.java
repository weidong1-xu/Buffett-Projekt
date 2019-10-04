package com.hanslv.stock.selector.algorithm.constants;

/**
 * 数据库常量
 * 
 * --------------------------------------------------
 * 1、价格信息分表上证表名称前缀												PRICE_INFO_SHANGZHENG_TAB_NAME_PREFIX
 * 2、价格信息分表深证表名称前缀												PRICE_INFO_SHENZHENG_TAB_NAME_PREFIX
 * 3、插入股票价格信息的线程池大小											STOCK_PRICE_INFO_INSERTING_THREAD_POOL_SIZE
 * 4、股票算法结果状态-UNKNOWN												ALGORITHM_RESULT_TYPE_UNKNOWN
 * 5、股票算法结果状态-SUCCESS												ALGORITHM_RESULT_TYPE_SUCCESS
 * 6、股票算法结果状态-FAIL													ALGORITHM_RESULT_TYPE_FAIL
 * --------------------------------------------------
 * @author hanslv
 *
 */
public abstract class AlgorithmDbConstants {
	public static final String PRICE_INFO_SHANGZHENG_TAB_NAME_PREFIX = "tab_stock_price_shangzheng_";//价格信息分表上证表名称前缀
	public static final String PRICE_INFO_SHENZHENG_TAB_NAME_PREFIX = "tab_stock_price_shenzheng_";//价格信息分表深证表名称前缀
	public static final int STOCK_PRICE_INFO_INSERTING_THREAD_POOL_SIZE = 10;//插入股票价格信息的线程池大小
	
	public static final String ALGORITHM_RESULT_TYPE_UNKNOWN = "UNKNOWN";//股票算法结果状态-UNKNOWN
	public static final String ALGORITHM_RESULT_TYPE_SUCCESS = "SUCCESS";//股票算法结果状态-SUCCESS
	public static final String ALGORITHM_RESULT_TYPE_FAIL = "FAIL";//股票算法结果状态-FAIL
}
