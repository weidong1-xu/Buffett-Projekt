package com.hanslv.stock.selector.algorithm.result;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmDbConstants;
import com.hanslv.stock.selector.algorithm.constants.AlgorithmOtherConstants;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmResultRepository;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

/**
 * 算法成功率计算模块，继承Runnable
 * 每实例化一个实例会创建一个私有的线程池
 * ----------------------------------------
 * 1、线程执行方法，会向当前私有线程池提交线程池大小相等数量的任务														public void run()
 * 2、从全部计算好TabAlgorithmResult的消息队列取出一个结果															public static TabAlgorithmResult getDoneResultFormBlockingQueue()
 * ----------------------------------------
 * @author hanslv
 *
 */
public class SuccessRateAlgorithm implements Runnable{
	
	/**
	 * 每实例化一个SuccessRateAlgorithm则启动一个线程池
	 */
	private ExecutorService threadPool;
	
	/*
	 * 存放全部计算好TabAlgorithmResult的消息队列
	 */
	private static BlockingQueue<TabAlgorithmResult> algorithmResult;
	
	static {
		/*
		 * 实例化algorithmResult
		 */
		algorithmResult = new ArrayBlockingQueue<>(AlgorithmOtherConstants.BASIC_BLOCKING_QUEUE_SIZE);
	}
	
	
	public SuccessRateAlgorithm() {
		/*
		 * 实例化一个线程池
		 */
		threadPool = Executors.newFixedThreadPool(AlgorithmOtherConstants.BASIC_THREAD_POOL_SIZE);
	}
	
	/**
	 * 1、线程执行方法，会向当前私有线程池提交线程池大小相等数量的任务
	 */
	@Override
	public void run() {
		try {
			/*
			 * 向私有线程池提交任务
			 */
			for(int i = 0 ; i < AlgorithmOtherConstants.BASIC_BLOCKING_QUEUE_SIZE ; i++) {
				threadPool.execute(() -> {
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
				});
			}
		}finally {
			/*
			 * 关闭线程池
			 */
			threadPool.shutdown();
		}
	}
	
	
	
	/**
	 * 2、从全部计算好TabAlgorithmResult的消息队列取出一个结果
	 * @return
	 */
	public static TabAlgorithmResult getDoneResultFormBlockingQueue() {
		TabAlgorithmResult doneResult = null;
		try {
			doneResult =  algorithmResult.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return doneResult;
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
			TabAlgorithmResult currentAlgorithmResult = IsSuccessAlgorithm.getknownResultFromBlockingQueue();
			
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
			try {
				algorithmResult.put(currentAlgorithmResult);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
