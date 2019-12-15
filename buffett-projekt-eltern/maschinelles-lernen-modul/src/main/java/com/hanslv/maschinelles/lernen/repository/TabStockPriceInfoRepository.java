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
 * 2、将日期向前移动count个数据量										public List<TabStockPriceInfo> changeDateForward(@Param("stockId") Integer stockId , @Param("currentDate")String currentDate , @Param("count")Integer count);
 * 3、将日期向后移动count个数据量										public List<TabStockPriceInfo> changeDateBackward(@Param("stockId") Integer stockId , @Param("currentDate")String currentDate , @Param("count")Integer count);
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
	 * 2、将日期向前移动count个数据量
	 * @param stockId
	 * @param currentDate
	 * @param count
	 * @return
	 */
	public List<TabStockPriceInfo> changeDateForward(@Param("stockId") Integer stockId , @Param("currentDate")String currentDate , @Param("count")Integer count);
	
	/**
	 * 3、将日期向后移动count个数据量
	 * @param stockId
	 * @param currentDate
	 * @param count
	 * @return
	 */
	public List<TabStockPriceInfo> changeDateBackward(@Param("stockId") Integer stockId , @Param("currentDate")String currentDate , @Param("count")Integer count);
}
