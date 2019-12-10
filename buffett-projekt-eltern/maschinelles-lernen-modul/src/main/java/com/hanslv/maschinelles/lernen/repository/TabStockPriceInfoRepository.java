package com.hanslv.maschinelles.lernen.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.allgemein.dto.TabStockPriceInfo;

/**
 * 股票价格信息Mapper
 * 
 * -----------------------------------------------------------
 * 1、获取DeepLearning4j股票模型的训练数据、评估数据					public List<TabStockPriceInfo> getTrainAndTestDataDL4j(@Param("stockId")Integer stockId, @Param("trainDataSize")Integer dataSize , @Param("trainEndDate")String trainEndDate)
 * 2、根据股票ID、开始时间获取指定天数的股票数据						public List<TabStockPriceInfo> getPriceInfoByIdAndAfterDateAndCount(@Param("stockId")Integer stockId , @Param("startDate")String startDate , @Param("count")Integer count)
 * -----------------------------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabStockPriceInfoRepository {
	/**
	 * 1、获取DeepLearning4j股票模型的训练数据、评估数据
	 * @param stockId
	 * @param dataSize
	 * @param trainEndDate
	 * @return
	 */
	public List<TabStockPriceInfo> getTrainAndTestDataDL4j(@Param("stockId")Integer stockId, @Param("trainDataSize")Integer dataSize , @Param("trainEndDate")String trainEndDate);
	
	/**
	  * 2、根据股票ID、开始时间获取指定天数的股票数据
	  * @param stockId
	  * @param startDate
	  * @param count
	  * @return
	  */
	public List<TabStockPriceInfo> getPriceInfoByIdAndAfterDateAndCount(@Param("stockId")Integer stockId , @Param("startDate")String startDate , @Param("count")Integer count);
}
