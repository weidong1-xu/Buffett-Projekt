package com.hanslv.stock.selector.algorithm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 股票价格信息Mapper
 * 
 * ----------------------------------------------
 * 1、插入一个集合											public void insertList(@Param("priceList")List<TabStockPriceInfo> priceList , @Param("tableName")String tableName);
 * 2、插入一条数据											public void insertOne(@Param("tableName")String tableName , @Param("priceInfo")TabStockPriceInfo priceInfo)
 * 3、获取一条数据											public TabStockPriceInfo selectOne(@Param("tableName") String tableName , @Param("priceInfo") TabStockPriceInfo currentPriceInfo)
 * 4、获取一只股票在指定日期后指定条数的全部信息				public List<TabStockPriceInfo> selectByStockIdAndAfterDateLimit(@Param("priceInfo")TabStockPriceInfo currentPriceInfo , @Param("limit")String limitCount)
 * 5、获取指定日期后的全部指定股票信息数量						public String selectPriceInfoCountByStockIdAndAfterDate(@Param("dayCondition")String day)
 * ----------------------------------------------
 * @author harrylu
 *
 */
@Mapper
public interface TabStockPriceInfoRepository {
	
	/**
	 * 1、插入一个集合
	 * @param priceList
	 */
	public void insertList(@Param("priceList")List<TabStockPriceInfo> priceList , @Param("tableName")String tableName);
	
	
	/**
	 * 2、插入一条数据
	 * @param priceInfo
	 */
	public void insertOne(@Param("tableName")String tableName , @Param("priceInfo")TabStockPriceInfo priceInfo);
	
	
	/**
	 * 3、获取一条数据
	 * @param currentPriceInfo
	 * @return
	 */
	@Select("SELECT "
			+ "stock_id , "
			+ "stock_price_date , "
			+ "stock_price_start_price ,  "
			+ "stock_price_end_price , "
			+ "stock_price_highest_price , "
			+ "stock_price_lowest_price , "
			+ "stock_price_volume , "
			+ "stock_price_turnover , "
			+ "stock_price_amplitude , "
			+ "stock_price_turnover_rate "
			+ "FROM ${tableName} WHERE stock_id = #{priceInfo.stockId} AND stock_price_date = #{priceInfo.stockPriceDate}")
	public TabStockPriceInfo selectOne(@Param("tableName") String tableName , @Param("priceInfo") TabStockPriceInfo currentPriceInfo);
	
	
	/**
	 * 4、获取一只股票在指定日期后指定条数的全部信息
	 * @param currentPriceInfo
	 * @param limitCount
	 * @return
	 */
	public List<TabStockPriceInfo> selectByStockIdAndAfterDateLimit(@Param("priceInfo")TabStockPriceInfo currentPriceInfo , @Param("limit")String limitCount);
	
	/**
	 * 5、获取指定日期后的全部指定股票信息数量 
	 * @param stockId
	 * @param day
	 * @return
	 */
	public String selectPriceInfoCountByStockIdAndAfterDate(@Param("stock_id")String stockId , @Param("dayCondition")String day);
	
	
}
