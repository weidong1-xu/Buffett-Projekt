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
}
