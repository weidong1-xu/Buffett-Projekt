package com.hanslv.stock.selector.algorithm;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmOtherConstants;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmInfo;

/**
 * 算法模板
 * 包含一个公用线程池、需要执行的算法队列、执行完毕算法计数器
 * 
 * 执行前准备：
 * 实例化公用线程池
 * 实例化需要执行的算法队列(泛型：TabAlgorithmInfo)
 * 实例化执行完毕算法计数器
 * 查询数据库中全部的算法ID、算法名称、算法类全名、算法时间区间并放入需要执行的算法队列中并将计数器+1
 * 
 * 调用runAlgorithm()方法执行全部算法：
 * 提交N个任务到线程池：
 *  获取当前计数器的值
 * 	while(算法计数器!=0)循环：
 * 		从算法队列中取出一个TabAlgorithmInfo
 * 		查询数据库中对应算法的最大run_date并与当前日期比较
 * 			小于当前日期-算法时间区间
 * 				用最大时间执行algorithmLogic()方法
 * 				将计算结果插入数据库
 * 				将当前算法TabAlgorithmInfo放入到需要执行的算法队列
 * 			否则
 * 				执行完毕计数器-1
 * 				判断当前算法计数器是否为0
 * 					是
 * 						立即关闭线程池（shutDownNow）
 * 
 * 
 * 新创建的算法需要在数据库中初始化包括（算法名称、算法类全名、算法时间区间）
 * 子类需要重写当前算法的算法逻辑algorithmLogic()
 * 
 * @author hanslv
 */
public abstract class AbstractAlgorithmLogic {
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
	 * 初始化
	 */
	static {
		publicThreadPool = Executors.newFixedThreadPool(AlgorithmOtherConstants.ALGORITHM_THREAD_POOL_SIZE);
		counter = new AtomicInteger();
		incompleteBlockingQueue = new ArrayBlockingQueue<>(CommonsOtherConstants.BASIC_BLOCKING_QUEUE_SIZE);
		initAlgorithms();
	}
	
	/**
	 * 关闭公用线程池
	 */
	void shutdownThreadPool() {
		publicThreadPool.shutdown();
	}
	
	/**
	 * 向队列中放入未完成算法信息
	 * @param incomplateAlgorithmInfo
	 */
	void putIncomplateAlgorithmInfo(TabAlgorithmInfo incomplateAlgorithmInfo) {
		try {
			incompleteBlockingQueue.put(incomplateAlgorithmInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 从未完成队列中取出算法信息
	 * @return
	 */
	TabAlgorithmInfo takeFromIncomplateBlockingQueue() {
		TabAlgorithmInfo incomplateAlgorithmInfo = null;
		try {
			incomplateAlgorithmInfo = incompleteBlockingQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return incomplateAlgorithmInfo;
	}
	
	/**
	 * 计数器加1
	 */
	int addCounter() {
		return counter.incrementAndGet();
	}
	
	/**
	 * 计数器减1
	 */
	int decrCounter() {
		return counter.decrementAndGet();
	}
	
	/**
	 * 计数器获取
	 * @return
	 */
	int getCounter() {
		return counter.get();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 初始化未完成算法队列
	 */
	private static void initAlgorithms() {
		
	}
}
