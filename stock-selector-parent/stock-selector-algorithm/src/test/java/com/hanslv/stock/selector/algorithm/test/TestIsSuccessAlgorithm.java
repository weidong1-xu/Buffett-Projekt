package com.hanslv.stock.selector.algorithm.test;

import com.hanslv.stock.selector.algorithm.result.IsSuccessAlgorithm;

/**
 * 测试算法结果计算模块
 * @author hanslv
 *
 */
public class TestIsSuccessAlgorithm {
	public static void main(String[] args) {
		new Thread(new IsSuccessAlgorithm()).start();
		while(true) {
			System.out.println(IsSuccessAlgorithm.getknownResultFromBlockingQueue());
		}
	}
}
