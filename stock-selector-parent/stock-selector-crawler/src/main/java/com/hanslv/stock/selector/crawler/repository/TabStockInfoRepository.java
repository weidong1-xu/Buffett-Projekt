package com.hanslv.stock.selector.crawler.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hanslv.stock.selector.commons.dto.TabStockInfo;

/**
 * TabStockInfo Mapper
 * 
 * ----------------------------------------
 * 1、查询数据条数													public int getCount()
 * 2、插入一个集合													public void insertList(List<TabStockInfo> stockInfoList)
 * 3、删除全部数据													public void deleteAll()
 * 4、获取全部股票信息数据											public List<TabStockInfo> selectAll()
 * 5、根据股票ID获取当前股票基本信息									public TabStockInfo getStockInfoById(@Param("stockId")Integer stockId)
 * ----------------------------------------
 * 
 * @author harrylu
 *
 */
@Mapper
public interface TabStockInfoRepository {
	
	/**
	 * 1、查询数据条数
	 * @return
	 */
	@Select("SELECT COUNT(stock_code) FROM tab_stock_info")
	public int getCount();
	
	
	/**
	 * 2、插入一个集合
	 * @param stockInfoList
	 */
	public void insertList(List<TabStockInfo> stockInfoList);
	
	
	/**
	 * 3、删除全部数据
	 */
	public void deleteAll();
	
	
	/**
	 * 4、获取全部股票信息数据
	 * @return
	 */
	@Select("SELECT stock_id , stock_code , stock_name FROM tab_stock_info")
	public List<TabStockInfo> selectAll();
	
	/**
	 * 5、根据股票ID获取当前股票基本信息
	 * @return
	 */
	@Select("SELECT stock_id , stock_code , stock_name FROM tab_stock_info WHERE stock_id=#{stockId}")
	public TabStockInfo getStockInfoById(@Param("stockId")Integer stockId);
}
