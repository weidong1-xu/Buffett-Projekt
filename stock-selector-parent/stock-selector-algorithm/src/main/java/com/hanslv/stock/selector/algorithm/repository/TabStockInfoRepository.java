package com.hanslv.stock.selector.algorithm.repository;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hanslv.stock.selector.commons.dto.TabStockInfo;

/**
 * TabStockInfo Mapper
 * 
 * ----------------------------------------
 * 1、根据股票ID获取当前股票基本信息													public TabStockInfo getStockInfoById(@Param("stockId")Integer stockId)
 * ----------------------------------------
 * 
 * @author harrylu
 *
 */
@Mapper
public interface TabStockInfoRepository {
	
	/**
	 * 1、根据股票ID获取当前股票基本信息
	 * @return
	 */
	@Select("SELECT stock_id , stock_code , stock_name FROM tab_stock_info WHERE stock_id=#{stockId}")
	public TabStockInfo getStockInfoById(@Param("stockId")Integer stockId);
}