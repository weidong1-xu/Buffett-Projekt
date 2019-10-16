package com.hanslv.stock.selector.algorithm;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.algorithm.AbstractResultAlgorithm;
import com.hanslv.stock.selector.algorithm.constants.AlgorithmDbConstants;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmResultRepository;
import com.hanslv.stock.selector.algorithm.util.DbTabSelectLogicUtil;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;

/**
 * 算法成功率计算模块
 * 每实例化一个实例会创建一个私有的线程池
 * 
 * @author hanslv
 *
 */
@Component
public class SuccessRateAlgorithm extends AbstractResultAlgorithm{
	
	@Autowired
	private TabAlgorithmResultRepository algorithmResultMapper;
	
	@Autowired
	private IsSuccessAlgorithm isSuccessAlgorithm;
	
	@Autowired
	private DbTabSelectLogicUtil tabSelectLogic;
	
	
	@Override
	void algorithmLogic() {
		/*
		 * 执行比较逻辑
		 */
		successRateLogic();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 计算当前TabAlgorithmResult的successRate
	 * @return
	 */
	private void successRateLogic() {
		while(true) {
			/*
			 * 从IsSuccessAlgorithm中取出一个加工好的TabAlgorithmInfo对象
			 */
			TabAlgorithmResult currentAlgorithmResult = isSuccessAlgorithm.getResultFromInnerBlockingQueue();
			
			/*
			 * 获取全部run_date<当前run_date并且==algorithm_id，全部不为UNKNOWN的数据
			 */
			List<TabAlgorithmResult> algorithmResultList = algorithmResultMapper.getAllDataNotUnknownAndByDate(currentAlgorithmResult);
			
			/*
			 * 计算结果
			 */
			currentAlgorithmResult.setSuccessRate(getCurrnetRateLogic(algorithmResultList));
		
			/*
			 * 更新该条记录
			 */
			algorithmResultMapper.updateSuccessRate(tabSelectLogic.tableSelector4AlgorithmResult(currentAlgorithmResult) , currentAlgorithmResult);
		}
	}
	
	/**
	 * 获取当前位置的成功率
	 * @param algorithmResultList
	 * @return
	 */
	private String getCurrnetRateLogic(List<TabAlgorithmResult> algorithmResultList) {
		int successCount = 0;//成功计数
		int failCount = 0;//失败计数
		
		for(TabAlgorithmResult algorithmResult : algorithmResultList) {
			if(AlgorithmDbConstants.ALGORITHM_RESULT_TYPE_SUCCESS.equals(algorithmResult.getIsSuccess()))
				successCount++;
			else
				failCount++;
		}
		
		/*
		 * 排除都为0的情况
		 */
		if(successCount == 0 && failCount ==0)
			return "";
		
		/*
		 * 最终结果
		 */
		BigDecimal resultBigDecimal = new BigDecimal(successCount).divide(new BigDecimal(successCount + failCount) , 2);
		return resultBigDecimal.toString() + "%";
	}
	
	
}
