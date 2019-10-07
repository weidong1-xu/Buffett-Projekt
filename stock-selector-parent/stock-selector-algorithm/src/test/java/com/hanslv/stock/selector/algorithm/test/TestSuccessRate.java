package com.hanslv.stock.selector.algorithm.test;

import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.algorithm.IsSuccessAlgorithm;
import com.hanslv.stock.selector.algorithm.SuccessRateAlgorithm;
import com.hanslv.stock.selector.algorithm.starter.AlgorithmServiceStarter;

/**
 * 计算算法成功率
 * @author hanslv
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AlgorithmServiceStarter.class)
public class TestSuccessRate {
	Logger logger = Logger.getLogger(TestSuccessRate.class);
	
	/*
	 * isSuccess算法执行模块
	 */
	@Autowired
	private IsSuccessAlgorithm isSuccess;
	
	/*
	 * successRate算法执行模块
	 */
	@Autowired
	private SuccessRateAlgorithm successRate;
	
	/**
	 * 测试算法执行
	 */
	@Test
	public void test() {
		/*
		 * 执行isSuccess算法
		 */
		isSuccess.runAlgorithm();
		isSuccess.shutdownInnerThreadPool();
		
		
		/*
		 * 执行SuccessRate算法
		 */
		successRate.runAlgorithm();
		successRate.shutdownInnerThreadPool();
		
		/*
		 * 获取结果
		 */
		while(true) 
			logger.info(SuccessRateAlgorithm.getResultFromInnerBlockingQueue());
	}
}
