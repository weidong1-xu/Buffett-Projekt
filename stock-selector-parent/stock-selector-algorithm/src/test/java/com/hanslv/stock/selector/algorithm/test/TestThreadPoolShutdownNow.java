package com.hanslv.stock.selector.algorithm.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestThreadPoolShutdownNow {
	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		threadPool.execute(() -> {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
			}finally {
				System.out.println("This is finally");
			}
		});
		
		System.out.println(threadPool.isShutdown());
		threadPool.shutdownNow();
		System.out.println(threadPool.isShutdown());
		threadPool.shutdownNow();
	}
}
