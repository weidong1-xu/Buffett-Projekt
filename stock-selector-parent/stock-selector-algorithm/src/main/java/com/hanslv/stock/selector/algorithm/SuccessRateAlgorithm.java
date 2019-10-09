package com.hanslv.stock.selector.algorithm;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.algorithm.AbstractResultAlgorithm;
import com.hanslv.stock.selector.algorithm.constants.AlgorithmDbConstants;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmResultRepository;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

/**
 * 算法成功率计算模块
 * 每实例化一个实例会创建一个私有的线程池
 * 
 * @author hanslv
 *
 */
@Component
public class SuccessRateAlgorithm extends AbstractResultAlgorithm{
	
	/*
	 * 每个算法都包含一个用于存放计算结果的消息队列
	 */
	private static BlockingQueue<TabAlgorithmResult> resultBlockingQueue;
	
	static {
		/*
		 * 实例化内置消息队列
		 */
		resultBlockingQueue = new ArrayBlockingQueue<>(CommonsOtherConstants.BASIC_BLOCKING_QUEUE_SIZE);
	}
	
	
	@Override
	void algorithmLogic() {
		try {
			/*
			 * 执行比较逻辑
			 */
			successRateLogic();
		}finally {
			/*
			 * 关闭全部数据库资源
			 */
			MyBatisUtil.getInstance().closeConnection();
		}
	}
	
	
	
	/**
	 * 从内置消息队列中获取一个结果
	 * @return
	 */
	public static TabAlgorithmResult getResultFromInnerBlockingQueue() {
		TabAlgorithmResult result = null;
		try {
			result = resultBlockingQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 计算当前TabAlgorithmResult的successRate
	 * @return
	 */
	private void successRateLogic() {
		TabAlgorithmResultRepository algorithmResultMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabAlgorithmResultRepository.class);
		while(true) {
			/*
			 * 从IsSuccessAlgorithm中取出一个加工好的TabAlgorithmInfo对象
			 */
			TabAlgorithmResult currentAlgorithmResult = IsSuccessAlgorithm.getResultFromInnerBlockingQueue();
			
			/*
			 * 获取全部run_date<当前run_date并且==algorithm_id，全部不为UNKNOWN的数据
			 */
			List<TabAlgorithmResult> algorithmResultList = algorithmResultMapper.getAllDataNotUnknownAndByDate(currentAlgorithmResult);
			
			/*
			 * 计算结果
			 */
			currentAlgorithmResult.setSuccessRate(getCurrnetRateLogic(algorithmResultList));
		
			/*
			 * 放入消息队列
			 */
			writeToResultBlockingQueue(currentAlgorithmResult);
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
	
	
	/**
	 * 向消息队列中写入一条消息
	 * @param result
	 */
	private void writeToResultBlockingQueue(TabAlgorithmResult result) {
		try {
			resultBlockingQueue.put(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
