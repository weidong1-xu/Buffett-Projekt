package com.hanslv.maschinelles.lernen.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hanslv.allgemein.dto.TabStockInfo;

/**
 * 股票信息Mapper
 * 
 * --------------------------------------------
 * 1、获取全部股票信息List							public List<TabStockInfo> selectAllStockInfo()	
 * 2、根据ID获取一只股票信息							public TabStockInfo selectById(@Param("stockId")Integer stockId)
 * --------------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabStockInfoRepository {
	/**
	 * 1、获取全部股票信息List
	 * @return
	 */
	@Select("SELECT stock_id , stock_code , stock_name FROM tab_stock_info")
	public List<TabStockInfo> selectAllStockInfo();
	
	/**
	 * 2、根据ID获取一只股票信息
	 * @param stockId
	 * @return
	 */
	@Select("SELECT stock_id , stock_code , stock_name FROM tab_stock_info WHERE stock_id = #{stockId}")
	public TabStockInfo selectById(@Param("stockId")Integer stockId);
}
