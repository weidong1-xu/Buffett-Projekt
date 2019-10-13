package com.hanslv.stock.selector.algorithm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hanslv.stock.selector.commons.dto.TabAlgorithmInfo;

/**
 * 算法信息Mapper
 * 
 * --------------------------------------
 * 1、获取全部算法信息List													public List<TabAlgorithmInfo> getAllAlgorithmInfo()
 * 2、更新当前算法的最后更新时间												public void updateAlgorithmInfoUpdateDate(@Param("currentAlgorithmName")String currentAlgorithmName , @Param("currentLastRunDate")String currentLastRunDate)
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
	
	
	/**
	 * 2、更新当前算法的最后更新时间
	 * @param algorithmInfoParam
	 * @param currentLastRunDate
	 */
	@Update("UPDATE tab_algorithm_info SET update_date = #{currentLastRunDate} WHERE algorithm_class_name = #{currentAlgorithmClassName}")
	public void updateAlgorithmInfoUpdateDate(@Param("currentAlgorithmClassName")String currentAlgorithmClassName , @Param("currentLastRunDate")String currentLastRunDate);
}
