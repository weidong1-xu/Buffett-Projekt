package com.hanslv.stock.selector.crawler.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadPool {
	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		threadPool.shutdown();
		System.out.println(threadPool.isTerminated());
	}
}
