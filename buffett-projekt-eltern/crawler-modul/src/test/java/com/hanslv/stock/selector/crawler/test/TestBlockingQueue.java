package com.hanslv.stock.selector.crawler.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestBlockingQueue {
    static BlockingQueue<String> test = new ArrayBlockingQueue<>(1);

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                test.put("Hello World");
                test.put("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {
            try {
                while (true) {
                    String message = test.take();
                    if (!"".equals(message)) System.out.println(message);
                    else test.notifyAll();
                }
            } catch (InterruptedException e) {
                System.out.println("停止接收消息");
            }
        }).start();
    }
}
