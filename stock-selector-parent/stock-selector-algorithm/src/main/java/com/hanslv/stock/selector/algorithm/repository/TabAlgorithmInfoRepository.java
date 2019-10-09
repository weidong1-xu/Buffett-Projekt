package com.hanslv.stock.selector.algorithm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hanslv.stock.selector.commons.dto.TabAlgorithmInfo;

/**
 * 算法信息Mapper
 * 
 * --------------------------------------
 * 1、获取全部算法信息List													public List<TabAlgorithmInfo> getAllAlgorithmInfo()
 * --------------------------------------
 * @author hanslv
 *
 */
@Mapper
public interface TabAlgorithmInfoRepository {
	
	/**
	 * 1、获取全部算法信息List
	 * @return
	 */
	@Select("SELECT algorithm_id , algorithm_name , algorithm_class_name , algorithm_day_count , algorithm_comment , update_date FROM tab_algorithm_info")
	public List<TabAlgorithmInfo> getAllAlgorithmInfo();
}
