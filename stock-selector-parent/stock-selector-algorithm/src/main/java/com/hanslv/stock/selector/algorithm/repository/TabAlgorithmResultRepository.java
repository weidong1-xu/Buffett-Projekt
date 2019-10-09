package com.hanslv.stock.selector.algorithm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.hanslv.stock.selector.commons.dto.TabAlgorithmInfo;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;

/**
 * TabAlgorithmResultMapper
 *
 * ------------------------------------------------------
 * 1、获取全部指定is_success字段值的数据													public List<TabAlgorithmResult> getAllDataByIsSuccess(@Param("type")String type)
 * 2、获取在当前参数TabAlgorithmResult的runDate之前并且不为UNKNOWN的数据					public List<TabAlgorithmResult> getAllDataNotUnknownAndByDate(@Param("paramResultInfo") TabAlgorithmResult paramResultInfo)
 * 3、更新一条记录的IsSuccess															public void updateIsSuccess(@Param("tableName")String tableName , @Param("updateParam")TabAlgorithmResult updateParam)
 * 4、获取一个算法的当前最后执行时间														public String getMaxRunDateByAlgorithmId(@Param("algorithmInfo")TabAlgorithmInfo algorithmInfo)
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
	
	
	/**
	 * 2、获取在当前参数TabAlgorithmResult的runDate之前并且不为UNKNOWN的数据
	 * @param paramResultInfo
	 * @return
	 */
	public List<TabAlgorithmResult> getAllDataNotUnknownAndByDate(@Param("paramResultInfo") TabAlgorithmResult paramResultInfo);
	
	
	/**
	 * 3、更新一条记录的IsSuccess
	 * @param tableName
	 * @param updateParam
	 */
	@Update("UPDATE ${tableName} SET is_success = #{updateParam.isSuccess} WHERE algorithm_id = #{updateParam.algorithmId} AND run_date = #{updateParam.runDate}")
	public void updateIsSuccess(@Param("tableName")String tableName , @Param("updateParam")TabAlgorithmResult updateParam);
	
	/**
	 * 4、获取一个算法的当前最后执行时间
	 * @param algorithmInfo
	 * @return
	 */
	public String getMaxRunDateByAlgorithmId(@Param("algorithmInfo")TabAlgorithmInfo algorithmInfo);
}
