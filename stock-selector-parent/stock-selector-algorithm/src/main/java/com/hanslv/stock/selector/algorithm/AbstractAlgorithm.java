package com.hanslv.stock.selector.algorithm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;

/**
 * 全部算法类的模板
 * 
 * ------------------------------------------
 * 1、运行算法，向当前内置线程池提交一个任务												public void runAlgorithm()
 * 2、关闭内置线程池，在算法结束时需要关闭内置线程池										public void shutdownInnerThreadPool()
 * ------------------------------------------
 * @author hanslv
 *
 */
public abstract class AbstractAlgorithm {
	/*
	 * 每个算法都包含一个内置线程池
	 */
	private ExecutorService innerThreadPool;
	
	
	/**
	 * 1、运行算法，向当前内置线程池提交一个任务
	 */
	public void runAlgorithm() {
		/*
		 * 初始化内置线程池和消息队列
		 */
		initInnerThreadPool();
		
		/*
		 * 提交一个任务
		 */
		innerThreadPool.execute(() -> {
			/*
			 * 计算逻辑
			 */
			algorithmLogic();
		});
	}
	
	
	/**
	 * 2、关闭内置线程池，在算法结束时需要关闭内置线程池
	 */
	public void shutdownInnerThreadPool() {
		if(innerThreadPool != null)
			innerThreadPool.shutdown();
	}
	
	
	
	
	/**
	 * 算法逻辑，需要子类重写
	 * @return 计算后的结果
	 */
	abstract void algorithmLogic();
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 初始化内置线程池
	 */
	private void initInnerThreadPool() {
		if(innerThreadPool == null || innerThreadPool.isTerminated())
			innerThreadPool = Executors.newFixedThreadPool(CommonsOtherConstants.BASIC_THREAD_POOL_SIZE);
	}
}
