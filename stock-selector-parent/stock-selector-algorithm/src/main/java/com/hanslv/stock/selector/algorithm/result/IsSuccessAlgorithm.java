package com.hanslv.stock.selector.algorithm.result;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmDbConstants;
import com.hanslv.stock.selector.algorithm.constants.AlgorithmOtherConstants;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmResultRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

/**
 * 算法结果计算模块
 * -------------------------------------------
 * 1、执行方法Runnable，会创建一个线程池并向该线程池提交多个处理任务													public void run()
 * 2、从计算完毕的阻塞队列中取回一条数据																				public static TabAlgorithmResult getknownResultFromBlockingQueue()
 * -------------------------------------------
 * 
 * @author hanslv
 *
 */
public class IsSuccessAlgorithm implements Runnable{
	static Logger logger = Logger.getLogger(IsSuccessAlgorithm.class);
	
	/*
	 * 状态为UNKNOWN的算法结果List
	 */
	private static List<TabAlgorithmResult> unknownResultList;
	
	/*
	 * 下标计数器
	 */
	private static AtomicInteger indexCounter;
	
	/*
	 * 执行计算的线程池，每个IsSuccessAlgorithm实例包含一个线程池
	 */
	private ExecutorService threadPool;
	
	/*
	 * 计算后结果消息队列
	 */
	private static BlockingQueue<TabAlgorithmResult> knownResultBlockingQueue;
	
	static {
		/*
		 * 获取全部状态为UNKNOWN的算法结果
		 */
		try {
			TabAlgorithmResultRepository algorithmResultMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabAlgorithmResultRepository.class);
			unknownResultList = algorithmResultMapper.getAllDataByIsSuccess(AlgorithmDbConstants.ALGORITHM_RESULT_TYPE_UNKNOWN);
			logger.info("获取到了UNKNOKN信息：" + unknownResultList.size() + "条");
		}finally {
			MyBatisUtil.getInstance().closeConnection();
		}
		
		
		indexCounter = new AtomicInteger();
		
		knownResultBlockingQueue = new ArrayBlockingQueue<>(AlgorithmOtherConstants.BASIC_BLOCKING_QUEUE_SIZE);
	}
	
	/**
	 * 1、执行方法Runnable，会创建一个线程池并向该线程池提交多个处理任务
	 */
	@Override
	public void run() {
		/*
		 * 实例化线程池
		 */
		threadPool = Executors.newFixedThreadPool(AlgorithmOtherConstants.BASIC_THREAD_POOL_SIZE);
		try {
			/*
			 * 向线程池提交任务
			 */
			for(int i = 0 ; i < AlgorithmOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) {
				threadPool.execute(() -> {
					TabStockPriceInfoRepository priceInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockPriceInfoRepository.class);
					
					/*
					 * 当前unKnownResultList下标
					 */
					int currentIndex = 0;
					try {
						List<TabStockPriceInfo> priceInfoList = null;
						TabStockPriceInfo paramPriceInfo = null;
						
						/*
						 * 循环直到unKnownResultList最后一个元素
						 */
						while((currentIndex = indexCounter.getAndIncrement()) < unknownResultList.size()) {
							/*
							 * 状态为unknown的算法结果
							 */
							TabAlgorithmResult currentUnknownResult = unknownResultList.get(currentIndex);
							
							/*
							 * 查询样本信息
							 */
							paramPriceInfo = new TabStockPriceInfo();
							paramPriceInfo.setStockId(currentUnknownResult.getStockId());
							paramPriceInfo.setStockPriceDate(currentUnknownResult.getRunDate());
							
							/*
							 * 查询回的股票价格信息
							 */
							priceInfoList = priceInfoMapper.selectByStockIdAndAfterDateLimit(paramPriceInfo , "2");
							
							/*
							 * 排除使用Limit查询存在条数不够返回空TabStockPriceInfo对象的情况
							 */
							if(priceInfoList.get(0).getStockPriceEndPrice() == null 
									&& priceInfoList.get(1).getStockPriceEndPrice() == null)
								continue;
							
							/*
							 * 获取结果
							 */
							if(compareLogic(priceInfoList))
								currentUnknownResult.setSuccessRate(AlgorithmDbConstants.ALGORITHM_RESULT_TYPE_SUCCESS);
							else
								currentUnknownResult.setSuccessRate(AlgorithmDbConstants.ALGORITHM_RESULT_TYPE_FAIL);
							
							
							/*
							 * 将结果放入到消息队列
							 */
							try {
								knownResultBlockingQueue.put(currentUnknownResult);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}finally {
						/*
						 * 关闭数据库连接
						 */
						MyBatisUtil.getInstance().closeConnection();
					}
				});
			}
		}finally {
			/*
			 * 关闭资源
			 */
			threadPool.shutdown();
		}
	}
	
	
	/**
	 * 2、从计算完毕的阻塞队列中取回一条数据
	 * @return
	 */
	public static TabAlgorithmResult getknownResultFromBlockingQueue() {
		TabAlgorithmResult result = null;
		try {
			result =  knownResultBlockingQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 比较两个交易日股票价格高低
	 * @param priceInfoList 包含第一天和第二天的股票价格信息
	 * @return true 第一天大于第二天
	 */
	private boolean compareLogic(List<TabStockPriceInfo> priceInfoList) {
		/*
		 * 算法执行日期股票价格（日期升序排列第一天）大于等于算法执行后一天股票价格（日期升序排列第二天）
		 */
		if(priceInfoList.get(0).getStockPriceEndPrice().compareTo(priceInfoList.get(1).getStockPriceEndPrice()) >= 0)
			return false;
		else
			return true;
	}
	
	
	
	
}
