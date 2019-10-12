package com.hanslv.stock.selector.algorithm;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmOtherConstants;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmInfoRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

/**
 * 算法模板
 * 包含一个公用线程池、需要执行的算法队列、执行完毕算法计数器、上证指数StockID
 * 
 * 执行前准备：
 * 实例化公用线程池
 * 实例化需要执行的算法队列(泛型：TabAlgorithmInfo)
 * 实例化执行完毕算法计数器
 * 查询数据库中全部的算法ID、算法名称、算法类全名、算法时间区间并放入需要执行的算法队列中并将计数器
 * 
 * runAlgorithm()方法逻辑：
 * 提交N个任务到线程池：
 *  获取当前计数器的值
 * 	while(算法计数器!=0)循环：
 * 		从算法队列中取出一个TabAlgorithmInfo
 * 		数据库中比当前日期currentLastRunDate大的上证指数信息数量是否大于等于时间区间
 * 			是
 * 				用最大时间执行algorithmLogic()方法
 * 				将计算结果插入数据库
 * 				将当前算法TabAlgorithmInfo放入到需要执行的算法队列
 * 			否
 * 				执行完毕计数器-1
 * 				判断当前算法计数器是否为0
 * 					是
 * 						立即关闭线程池（shutDownNow）
 * 
 * 
 * 新创建的算法需要在数据库中初始化，包括（算法名称、算法类全名、算法时间区间）
 * 子类需要重写当前算法的算法逻辑algorithmLogic()
 * 
 * @author hanslv
 */
@Component
public class AlgorithmLogic {
	static Logger logger = Logger.getLogger(AlgorithmLogic.class);
	
	/*
	 * 公用线程池，调用runAlgorithm()方法向该线程池提交一个算法逻辑并运算
	 */
	private static ExecutorService publicThreadPool;
	
	/*
	 * 计数器
	 */
	private static AtomicInteger counter;
	
	/*
	 * 未完成算法队列
	 */
	private static BlockingQueue<TabAlgorithmInfo> incompleteBlockingQueue;
	
	/*
	 * 上证指数StockID
	 */
	private static String shangzhengStockId;
	
	/**
	 * 运行全部算法
	 */
	public static void runAlgorithm() {
		/*
		 * 初始化
		 */
		initAlgorithms();
		
		/*
		 * 提交N个任务
		 */
		for(int i = 0 ; i < AlgorithmOtherConstants.ALGORITHM_THREAD_POOL_SIZE ; i++) {
			publicThreadPool.execute(() -> {
				/*
				 * 获取当前计数器，while(算法计数器!=0)循环
				 * 
				 * 保持线程池最后被关闭的思路：
				 * counter>0，则说明当前队列中仍然存在算法信息，或仍然有算法正在被其他线程计算
				 * 
				 * 当有线程正在计算算法时counter不可能为0。
				 * 因为有线程正在计算说明当前存在未完成计算的算法，并且该算法对象是唯一的，只有在执行完当前算法时才可能对counter进行操作
				 * 
				 * 若当前存在未完成算法（counter不为0）但是该算法对象被其他线程持有，当前算法可能进入while循环，并且在从阻塞队列中取出算法信息时阻塞，
				 * 但最终会因为其他线程执行完毕后插入新的算法信息而继续，或因其他线程判断当前算法已经执行完毕并且将counter-1得0而关闭线程池结束
				 */
				try {
					while(getCounter() > 0) {
						TabAlgorithmInfo currentAlgorithmInfo = takeFromIncomplateBlockingQueue();
						
						/*
						 * 数据库中比当前日期currentLastRunDate大的上证指数信息数量是否大于等于时间区间
						 */
						if(checkLastRunDate(currentAlgorithmInfo.getAlgorithmDayCount() , currentAlgorithmInfo.getUpdateDate())) {
							/**
							 * 利用反射执行当前算法的algorithmLogic()方法
							 */
							try {
								AlgorithmLogic currentAlgorithm = (AlgorithmLogic) Class.forName(currentAlgorithmInfo.getAlgorithmClassName()).newInstance();
								currentAlgorithm.algorithmLogic(currentAlgorithmInfo.getUpdateDate());
								
								/*
								 * 将当前算法放回阻塞队列
								 */
								putIncomplateAlgorithmInfo(currentAlgorithmInfo);
								
							} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
								e.printStackTrace();
							}
						}else {
							/*
							 * 执行完毕计数器-1
							 */
							decrCounter();
							
							/*
							 * 判断当前算法计数器是否为0，是则关闭线程池
							 */
							if(getCounter() == 0) shutdownThreadPool();
							
						}
					}
				}finally {
					/*
					 * 当前线程结束后关闭数据库连接
					 */
					MyBatisUtil.getInstance().closeConnection();
				}
			});
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 需要子类实现的算法逻辑
	 * 执行后需要更新当前算法的最后执行时间TabAlgorithmInfo.updateDate
	 * 数据库连接交由外层runAlgorithm()方法管理，方法中不可关闭
	 */
	void algorithmLogic(String lastRunDate) {}
	
	
	/**
	 * 关闭公用线程池
	 */
	static void shutdownThreadPool() {
		if(!publicThreadPool.isShutdown()) publicThreadPool.shutdown();
	}
	
	
	/**
	 * 从未完成队列中取出算法信息
	 * @return
	 */
	static TabAlgorithmInfo takeFromIncomplateBlockingQueue() {
		TabAlgorithmInfo incomplateAlgorithmInfo = null;
		try {
			incomplateAlgorithmInfo = incompleteBlockingQueue.take();
		} catch (InterruptedException e) {
			logger.info("线程池关闭，停止等待从incompleteBlockingQueue中获取算法信息........");
		}
		return incomplateAlgorithmInfo;
	}
	
	/**
	 * 向队列中放入未完成算法信息
	 * @param incomplateAlgorithmInfo
	 */
	static void putIncomplateAlgorithmInfo(TabAlgorithmInfo incomplateAlgorithmInfo) {
		try {
			incompleteBlockingQueue.put(incomplateAlgorithmInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	
	/**
	 * 计数器减1
	 */
	static int decrCounter() {
		return counter.decrementAndGet();
	}
	
	/**
	 * 计数器获取
	 * @return
	 */
	static int getCounter() {
		return counter.get();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 初始化
	 */
	private static void initAlgorithms() {
		publicThreadPool = Executors.newFixedThreadPool(AlgorithmOtherConstants.ALGORITHM_THREAD_POOL_SIZE);
		counter = new AtomicInteger();
		incompleteBlockingQueue = new ArrayBlockingQueue<>(CommonsOtherConstants.BASIC_BLOCKING_QUEUE_SIZE);
		
		/*
		 * 获取全部算法信息LIst
		 */
		TabAlgorithmInfoRepository algorithmInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabAlgorithmInfoRepository.class);
		List<TabAlgorithmInfo> algorithmInfoList = algorithmInfoMapper.getAllAlgorithmInfo();
		
		/*
		 * 将算法信息放入阻塞队列并初始化计数器
		 */
		for(TabAlgorithmInfo algorithmInfo : algorithmInfoList) {
			putIncomplateAlgorithmInfo(algorithmInfo);
			addCounter();
		}
		
		/*
		 * 上证指数StockId
		 */
		TabStockInfoRepository stockInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockInfoRepository.class);
		shangzhengStockId = String.valueOf(stockInfoMapper.getStockInfoByCode(CommonsOtherConstants.SHANGZHENG_ZHISHU_STOCK_CODE).getStockId());
	}
	
	
	/**
	 * 计数器加1
	 */
	private static int addCounter() {
		return counter.incrementAndGet();
	}
	
	/**
	 * 数据库中比当前日期currentLastRunDate大的上证指数信息数量是否大于等于时间区间
	 * @param algorithmDayCount
	 * @param currentLastRunDate
	 * @return true需要继续执行
	 */
	private static boolean checkLastRunDate(String algorithmDayCount , String currentLastRunDate) {
		TabStockPriceInfoRepository stockPriceInfoMapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockPriceInfoRepository.class);
		int currentShangzhengDayCount = Integer.parseInt(stockPriceInfoMapper.selectPriceInfoCountByStockIdAndAfterDate(shangzhengStockId , currentLastRunDate));
		int currentDayCountInt = Integer.parseInt(algorithmDayCount);
		return currentShangzhengDayCount >= currentDayCountInt ? true : false;
	}
}
