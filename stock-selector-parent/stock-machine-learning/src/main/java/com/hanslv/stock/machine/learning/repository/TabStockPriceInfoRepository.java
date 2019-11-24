package com.hanslv.stock.machine.learning.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 股票价格信息Mapper
 * 
 * -----------------------------------------------------------
 * 1、获取指定股票的训练数据											public List<TabStockPriceInfo> getTrainData(@Param("stockId")Integer stockId , @Param("trainDataSize")Integer trainDataSize)
 * 2、获取DeepLearning4j股票模型的训练数据、评估数据					public List<TabStockPriceInfo> getTrainAndTestDataDL4j(@Param("stockId")String stockId , @Param("trainDataSize")Integer dataSize)
 * -----------------------------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabStockPriceInfoRepository {
	/**
	 * 1、获取指定股票的训练数据
	 * @param stockId
	 * @param trainDataSize
	 * @return
	 */
	public List<TabStockPriceInfo> getTrainData(@Param("stockId")Integer stockId , @Param("trainDataSize")Integer trainDataSize);
	
	/**
	 * 2、获取DeepLearning4j股票模型的训练数据、评估数据
	 * @param stockId
	 * @param dataSize
	 * @return
	 */
	public List<TabStockPriceInfo> getTrainAndTestDataDL4j(@Param("stockId")String stockId, @Param("trainDataSize")Integer dataSize);
}
