package com.hanslv.stock.machine.learning.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hanslv.stock.selector.commons.dto.TabPriceDateMLResultFiveDays;

/**
 * 日期-价格预测结果Mapper
 * 
 * ----------------------------------------------
 * 1、插入List									public void insertList(@Param("mlResultList")List<TabPriceDateMLResultFiveDays> mlResultList)
 * ----------------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabPriceDateMLResultFiveDaysRepository {
	/**
	 * 1、插入List
	 * @param mlResultList
	 */
	public void insertList(@Param("mlResultList")List<TabPriceDateMLResultFiveDays> mlResultList);
}
