package com.hanslv.maschinelles.lernen.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.allgemein.dto.TabStockPriceInfo;

/**
 * 股票价格信息Mapper
 * 
 * -----------------------------------------------------------
 * 2、获取DeepLearning4j股票模型的训练数据、评估数据					public List<TabStockPriceInfo> getTrainAndTestDataDL4j(@Param("stockId")String stockId , @Param("trainDataSize")Integer dataSize)
 * -----------------------------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabStockPriceInfoRepository {
	/**
	 * 2、获取DeepLearning4j股票模型的训练数据、评估数据
	 * @param stockId
	 * @param dataSize
	 * @return
	 */
	public List<TabStockPriceInfo> getTrainAndTestDataDL4j(@Param("stockId")String stockId, @Param("trainDataSize")Integer dataSize);
}
