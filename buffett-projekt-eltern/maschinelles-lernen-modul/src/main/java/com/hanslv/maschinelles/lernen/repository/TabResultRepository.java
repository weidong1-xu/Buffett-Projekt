package com.hanslv.maschinelles.lernen.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hanslv.allgemein.dto.TabResult;

/**
 * 结果集数据库操作
 * 
 * 获取结果SQL：
 * SELECT info.stock_code , info.stock_name , result.date , result.suggest_rate , result.success 
 * FROM tab_result result LEFT JOIN tab_stock_info info 
 * ON result.stock_id = info.stock_id
 * WHERE result.date = '2019-12-08'
 * ORDER BY suggest_rate DESC;
 * 
 * 
 * -------------------------------------------------------
 * 1、插入一条结果									public void insert(@Param("result")TabResult result)
 * 2、根据股票Id、时间判断结果是否已存在				public int selectByIdAndDate(@Param("stockId")Integer stockId , @Param("date")String date)
 * -------------------------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabResultRepository {
	/**
	 * 1、插入一条结果
	 * @param result
	 */
	@Insert("INSERT INTO tab_result (stock_id , date , suggest_rate) VALUES (#{result.stockId} , #{result.date} , #{result.suggestRate})")
	public void insert(@Param("result")TabResult result);
	
	/**
	 * 2、根据股票Id、时间判断结果是否已存在
	 * @param stockId
	 * @param date
	 * @return
	 */
	@Select("SELECT COUNT(stock_id) FROM tab_result WHERE stock_id = #{stockId} AND date = #{date}")
	public int selectByIdAndDate(@Param("stockId")Integer stockId , @Param("date")String date);
}
