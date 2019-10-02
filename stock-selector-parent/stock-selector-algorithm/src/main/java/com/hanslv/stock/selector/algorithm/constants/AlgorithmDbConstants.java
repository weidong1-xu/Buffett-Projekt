package com.hanslv.stock.selector.algorithm.constants;

/**
 * 数据库常量
 * 
 * --------------------------------------------------
 * 1、价格信息分表上证表名称前缀												PRICE_INFO_SHANGZHENG_TAB_NAME_PREFIX
 * 2、价格信息分表深证表名称前缀												PRICE_INFO_SHENZHENG_TAB_NAME_PREFIX
 * --------------------------------------------------
 * @author hanslv
 *
 */
public abstract class AlgorithmDbConstants {
	public static final String PRICE_INFO_SHANGZHENG_TAB_NAME_PREFIX = "tab_stock_price_shangzheng_";//价格信息分表上证表名称前缀
	public static final String PRICE_INFO_SHENZHENG_TAB_NAME_PREFIX = "tab_stock_price_shenzheng_";//价格信息分表深证表名称前缀
}
