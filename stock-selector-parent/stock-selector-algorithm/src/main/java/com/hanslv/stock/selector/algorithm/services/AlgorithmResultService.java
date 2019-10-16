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
 * 
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
	 * 执行算法结果更新
	 */
	public void upDateAlgorithmResult() {
		/*
		 * isSuccess
		 */
		new Thread(() -> {
			for(int i = 0 ; i < CommonsOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) isSuccess.runAlgorithm();
		}).start();
		
		
		/*
		 * successRate
		 */
		new Thread(() -> {
			for(int i = 0 ; i < CommonsOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) successRate.runAlgorithm();
		}).start();
		
		
		/*
		 * 更新结果
		 */
		new Thread(() -> {
			
		}).start();
	}
}
