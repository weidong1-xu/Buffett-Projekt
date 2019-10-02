package com.hanslv.stock.selector.algorithm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 股票价格信息Mapper
 * 
 * ----------------------------------------------
 * 1、插入一个集合											public void insertList(@Param("priceList")List<TabStockPriceInfo> priceList , @Param("tableName")String tableName);
 * 2、插入一条数据											public void insertOne(@Param("tableName")String tableName , @Param("priceInfo")TabStockPriceInfo priceInfo)
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
	
	
}
