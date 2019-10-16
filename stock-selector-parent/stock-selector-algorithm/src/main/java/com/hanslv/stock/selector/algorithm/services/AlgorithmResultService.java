package com.hanslv.stock.selector.algorithm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.selector.algorithm.IsSuccessAlgorithm;
import com.hanslv.stock.selector.algorithm.SuccessRateAlgorithm;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;

/**
 * 算法结果模块Service
 * 
 * --------------------------------------------------------
 * 1、执行算法结果更新													public void upDateAlgorithmResult()
 * --------------------------------------------------------
 * @author admin
 *
 */
@Service
public class AlgorithmResultService {
	
	@Autowired
	private IsSuccessAlgorithm isSuccess;
	
	@Autowired
	private SuccessRateAlgorithm successRate;
	
	/**
	 * 1、执行算法结果更新
	 */
	public void updateAlgorithmResult() {
		/*
		 * isSuccess
		 */
		new Thread(() -> {
			for(int i = 0 ; i < CommonsOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) isSuccess.runAlgorithm();
			isSuccess.shutdownInnerThreadPool();
		}).start();
		
		
		/*
		 * successRate
		 */
		new Thread(() -> {
			for(int i = 0 ; i < CommonsOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) successRate.runAlgorithm();
			successRate.shutdownInnerThreadPool();
		}).start();
	}
}
