package com.hanslv.stock.selector.algorithm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;

/**
 * TabAlgorithmResultMapper
 *
 * ------------------------------------------------------
 * 1、获取全部指定is_success字段值的数据													public List<TabAlgorithmResult> getAllDataByIsSuccess(@Param("type")String type)
 * ------------------------------------------------------
 *
 * @author hanslv
 *
 */
@Mapper
public interface TabAlgorithmResultRepository {
	/**
	 * 1、获取全部指定is_success字段值的数据
	 * @param type
	 * @return
	 */
	public List<TabAlgorithmResult> getAllDataByIsSuccess(@Param("type")String type);
}
