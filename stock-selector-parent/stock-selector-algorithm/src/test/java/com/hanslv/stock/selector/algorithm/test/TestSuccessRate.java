package com.hanslv.stock.selector.algorithm.test;

import com.hanslv.stock.selector.algorithm.result.IsSuccessAlgorithm;
import com.hanslv.stock.selector.algorithm.result.SuccessRateAlgorithm;

/**
 * 计算算法成功率
 * @author hanslv
 *
 */
public class TestSuccessRate {
	public static void main(String[] args) {
		new Thread(new IsSuccessAlgorithm()).start();;
		new Thread(new SuccessRateAlgorithm()).start();
		while(true) {
			System.out.println(SuccessRateAlgorithm.getDoneResultFormBlockingQueue());
		}
	}
}
