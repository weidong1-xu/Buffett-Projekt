package com.hanslv.stock.selector.crawler.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.stock.selector.commons.dto.TabStockIndexMacd;

/**
 * 股票MACD指标信息Mapper
 * 
 * ---------------------------------------------
 * 1、插入一条记录								public void insertOne(@Param("tableName")String tableName , @Param("macdInfo")TabStockIndexMacd macdInfo)
 * 2、获取一条记录								public TabStockIndexMacd selectOne(@Param("stockId")Integer stockId , @Param("date")String date)
 * ---------------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabStockIndexMacdRepository {
	
	/**
	 * 1、插入一条记录
	 * @param tableName
	 * @param macdInfo
	 */
	@Insert("INSERT INTO ${tableName} (stock_id , date , diff , dea , macd) VALUES (#{macdInfo.stockId} , #{macdInfo.date} , #{macdInfo.diff} , #{macdInfo.dea} , #{macdInfo.macd})")
	public void insertOne(@Param("tableName")String tableName , @Param("macdInfo")TabStockIndexMacd macdInfo);
	
	
	/**
	 * 2、获取一条记录
	 * @param stockId
	 * @param date
	 * @return
	 */
	public TabStockIndexMacd selectOne(@Param("stockId")Integer stockId , @Param("date")String date);
}
