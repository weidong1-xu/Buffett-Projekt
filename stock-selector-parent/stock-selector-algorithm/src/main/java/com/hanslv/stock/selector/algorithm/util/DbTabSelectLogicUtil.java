package com.hanslv.stock.selector.algorithm.util;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmDbConstants;
import com.hanslv.stock.selector.algorithm.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

/**
 * 数据库分表逻辑，
 * 由外部关闭传入的数据库连接
 * 
 * 分表逻辑：
 * 插入：
 * 根据股票价格日期，
 * 由于每个交易所分为三张表，因此可以将当前日期中的DAY对3取余数，
 * 例如:
 * 2019-09-01，%=1
 * 2019-09-02，%=2
 * 2019-09-03，%=3
 * 2019-09-04，%=1
 * 2019-09-05，%=2
 * 2019-09-06，%=3
 * 2019-09-07，%=1
 * 2019-09-08，%=2
 * 2019-09-09，%=3
 * 2019-09-10，%=1
 * .
 * .
 * .
 * 以此类推
 * 
 * 
 * ---------------------------------------------------
 * 1、返回当前价格信息应该插入的表名称														public static String tableSelector4PriceInfo(TabStockPriceInfo currentPriceInfo , TabStockInfoRepository stockInfoMapper)
 * 2、返回当前算法结果应该插入的表名称														public static String tableSelector4AlgorithmResult(TabAlgorithmResult currentAlgorithmResult)
 * ---------------------------------------------------
 * @author hanslv
 *
 */
public class DbTabSelectLogicUtil {
	
	/**
	 * 1、返回当前价格信息应该插入的表名称
	 * 由外部关闭传入的数据库连接
	 * @param currentPriceInfo
	 * @return
	 */
	public static String tableSelector4PriceInfo(TabStockPriceInfo currentPriceInfo , TabStockInfoRepository stockInfoMapper) {
		String tableName = "";
		
		/*
		 * 获取当前价格日期
		 */
		Integer currentPriceInfoDay = getCurrentPriceInfoDay4PriceInfo(currentPriceInfo);
		
		/*
		 * 表后缀
		 */
		int tabSuffix = currentPriceInfoDay % 3 + 1;
		
		/*
		 * 获取当前股票基本信息
		 */
		TabStockInfo currentStockInfo = null;
		stockInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockInfoRepository.class);
		currentStockInfo = stockInfoMapper.getStockInfoById(currentPriceInfo.getStockId());
		
		/*
		 * 判断是否为上证股票
		 */
		if(currentStockInfo.getStockCode().indexOf("6") != 0) 
			tableName = AlgorithmDbConstants.PRICE_INFO_SHENZHENG_TAB_NAME_PREFIX + "000" + tabSuffix;
		else
			tableName = AlgorithmDbConstants.PRICE_INFO_SHANGZHENG_TAB_NAME_PREFIX + "000" + tabSuffix;
		
		return tableName;
	}
	
	
	/**
	 * 2、返回当前算法结果应该插入的表名称
	 * 由外部关闭传入的数据库连接
	 * @param currentAlgorithmResult
	 * @return
	 */
	public static String tableSelector4AlgorithmResult(TabAlgorithmResult currentAlgorithmResult) {
		/*
		 * 获取当前价格日期
		 */
		Integer currentPriceInfoDay = getCurrentPriceInfoDay4AlgorithmResult(currentAlgorithmResult);
		
		/*
		 * 表后缀
		 */
		int tabSuffix = currentPriceInfoDay % 3 + 1;
		
		return "tab_algorithm_result_00" + tabSuffix;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 价格信息对象中价格日期的日期
	 * @param currentPriceInfo
	 * @return
	 */
	private static Integer getCurrentPriceInfoDay4PriceInfo(TabStockPriceInfo currentPriceInfo) {
		return new Integer(currentPriceInfo.getStockPriceDate().split("-")[2]);
	}
	
	
	/**
	 * 价格信息对象中价格日期的日期
	 * @param currentPriceInfo
	 * @return
	 */
	private static Integer getCurrentPriceInfoDay4AlgorithmResult(TabAlgorithmResult currentAlgorithmResult) {
		return new Integer(currentAlgorithmResult.getRunDate().split("-")[2]);
	}
}
