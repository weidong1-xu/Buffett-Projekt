package com.hanslv.maschinelles.lernen.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hanslv.allgemein.dto.TabResult;

/**
 * 结果集数据库操作
 * 
 * 获取结果SQL：
 * SELECT info.stock_code , info.stock_name , result.date , result.suggest_buy_price , result.suggest_sell_price , result.suggest_rate , result.success 
 * FROM tab_result result LEFT JOIN tab_stock_info info 
 * ON result.stock_id = info.stock_id
 * WHERE result.date = '2019-11-15'
 * ORDER BY suggest_rate DESC;
 * 
 * 
 * -------------------------------------------------------
 * 1、插入一条结果									public void insert(@Param("result")TabResult result)
 * 2、根据股票Id、时间判断结果是否已存在				public int selectByIdAndDate(@Param("stockId")Integer stockId , @Param("date")String date)
 * 3、获取全部结果为null的结果记录					public List<TabResult> selectAllZeroResult()
 * 4、更新当前结果是否成功							public void updateSuccess(@Param("result")TabResult result)
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
	@Insert("INSERT INTO tab_result (stock_id , date , suggest_buy_price , suggest_sell_price , suggest_rate) VALUES (#{result.stockId} , #{result.date} , #{result.suggestBuyPrice} , #{result.suggestSellPrice} , #{result.suggestRate})")
	public void insert(@Param("result")TabResult result);
	
	/**
	 * 2、根据股票Id、时间判断结果是否已存在
	 * @param stockId
	 * @param date
	 * @return
	 */
	@Select("SELECT COUNT(stock_id) FROM tab_result WHERE stock_id = #{stockId} AND date = #{date}")
	public int selectByIdAndDate(@Param("stockId")Integer stockId , @Param("date")String date);
	
	/**
	  * 3、获取全部结果为null的结果记录
	  * @return
	  */
	@Select("SELECT stock_id , date , suggest_buy_price , suggest_sell_price , suggest_rate , success FROM tab_result WHERE success IS NULL")
	public List<TabResult> selectAllZeroResult();
	 
	/**
	 * 4、更新当前结果是否成功
	 * @param result
	 */
	@Update("UPDATE tab_result SET success = #{result.success} WHERE stock_id = #{result.stockId} AND date = #{result.date}")
	public void updateSuccess(@Param("result")TabResult result);
}
