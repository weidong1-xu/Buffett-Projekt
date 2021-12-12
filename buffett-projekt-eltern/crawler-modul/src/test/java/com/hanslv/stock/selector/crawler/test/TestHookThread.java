package com.hanslv.stock.selector.crawler.test;

import java.util.concurrent.TimeUnit;

public class TestHookThread {
    public static void main(String[] args) {
        try {
            TimeUnit.SECONDS.sleep(10);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("正在调用Hook线程");
            }));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
