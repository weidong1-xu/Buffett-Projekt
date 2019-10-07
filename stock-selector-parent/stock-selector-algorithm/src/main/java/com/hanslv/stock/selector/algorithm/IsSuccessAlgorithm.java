package com.hanslv.stock.selector.algorithm;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.algorithm.AbstractAlgorithm;
import com.hanslv.stock.selector.algorithm.constants.AlgorithmDbConstants;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmResultRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.algorithm.util.DbTabSelectLogicUtil;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

/**
 * 算法结果计算模块
 * 每实例化一个实例会创建一个私有的线程池
 * 使用完毕需要关闭线程池
 * 
 * @author hanslv
 *
 */
@Component
public class IsSuccessAlgorithm extends AbstractAlgorithm{
	static Logger logger = Logger.getLogger(IsSuccessAlgorithm.class);
	
	/*
	 * 每个算法都包含一个用于存放计算结果的消息队列
	 */
	private static BlockingQueue<TabAlgorithmResult> resultBlockingQueue;
	
	/*
	 * 状态为UNKNOWN的算法结果List
	 */
	private static List<TabAlgorithmResult> unknownResultList;
	
	/*
	 * 下标计数器
	 */
	private static AtomicInteger indexCounter;
	
	static {
		/*
		 * 获取全部状态为UNKNOWN的算法结果
		 */
		TabAlgorithmResultRepository algorithmResultMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabAlgorithmResultRepository.class);
		try {
			unknownResultList = Collections.synchronizedList(algorithmResultMapper.getAllDataByIsSuccess(AlgorithmDbConstants.ALGORITHM_RESULT_TYPE_UNKNOWN));
			logger.info("获取到了UNKNOKN信息：" + unknownResultList.size() + "条");
		}finally {
			MyBatisUtil.getInstance().closeConnection();
		}
		
		indexCounter = new AtomicInteger();
		
		/*
		 * 实例化内置消息队列
		 */
		resultBlockingQueue = new ArrayBlockingQueue<>(CommonsOtherConstants.BASIC_BLOCKING_QUEUE_SIZE);
	}
	
	
	@Override
	void algorithmLogic() {
		/*
		 * 当前unKnownResultList下标
		 */
		int currentIndex = 0;
		try {
			/*
			 * 循环直到unKnownResultList最后一个元素
			 */
			while((currentIndex = indexCounter.getAndIncrement()) < unknownResultList.size()) {
				/*
				 * 状态为unknown的算法结果
				 */
				TabAlgorithmResult currentUnknownResult = unknownResultList.get(currentIndex);
				
				/*
				 * 查询回的股票价格信息
				 */
				List<TabStockPriceInfo> priceInfoList = getStockPriceInfoByCurrentUnknownResult(currentUnknownResult);
				
				/*
				 * 获取结果
				 */
				compareLogic(priceInfoList , currentUnknownResult);
				
				
				/*
				 * 将结果放入到消息队列
				 */
				writeToResultBlockingQueue(currentUnknownResult);
			}
		}finally {
			/*
			 * 关闭数据库连接
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
	 * 根据当前标记为Unknown的算法结果信息获取需要的股票价格信息List
	 * @param currentUnknownResult
	 * @return
	 */
	private List<TabStockPriceInfo> getStockPriceInfoByCurrentUnknownResult(TabAlgorithmResult currentUnknownResult){
		TabStockPriceInfoRepository priceInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockPriceInfoRepository.class);
		/*
		 * 查询样本信息
		 */
		TabStockPriceInfo paramPriceInfo = new TabStockPriceInfo();
		paramPriceInfo.setStockId(currentUnknownResult.getStockId());
		paramPriceInfo.setStockPriceDate(currentUnknownResult.getRunDate());
		
		/*
		 * 查询回的股票价格信息
		 */
		return priceInfoMapper.selectByStockIdAndAfterDateLimit(paramPriceInfo , "3");
	}
	
	
	
	
	
	/**
	 * 判断第三天收盘价是否大于第二天
	 * @param priceInfoList 包含第一天和第二天和第三天的股票价格信息
	 * @return true 第三天收盘价大于第二天收盘价
	 */
	private void compareLogic(List<TabStockPriceInfo> priceInfoList , TabAlgorithmResult currentUnknownResult) {
		/*
		 * 跳过判断条件不满足要求的数据
		 */
		if(priceInfoList.size() < 3)
			return;
		
		/*
		 * 算法执行日期后第一天股票价格（日期升序排列第二天）大于等于算法执行后第二天股票价格（日期升序排列第三天）
		 */
		if(priceInfoList.get(1).getStockPriceEndPrice().compareTo(priceInfoList.get(2).getStockPriceEndPrice()) >= 0)
			currentUnknownResult.setIsSuccess(AlgorithmDbConstants.ALGORITHM_RESULT_TYPE_FAIL);
		else
			currentUnknownResult.setIsSuccess(AlgorithmDbConstants.ALGORITHM_RESULT_TYPE_SUCCESS);
		
		
		/*
		 * 更新该条记录
		 */
		TabAlgorithmResultRepository algorithmResultMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabAlgorithmResultRepository.class);
		algorithmResultMapper.updateIsSuccess(
				DbTabSelectLogicUtil.tableSelector4AlgorithmResult(currentUnknownResult) , 
				currentUnknownResult);
		MyBatisUtil.getInstance().commitConnection();
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
