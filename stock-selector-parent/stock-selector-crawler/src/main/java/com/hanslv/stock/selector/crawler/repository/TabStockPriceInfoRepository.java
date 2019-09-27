package com.hanslv.stock.selector.crawler.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 股票价格信息Mapper
 * 
 * ----------------------------------------------
 * 1、插入一个集合											public void insertList(@Param("priceList")List<TabStockPriceInfo> priceList , @Param("tableName")String tableName);
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
	
	
}
