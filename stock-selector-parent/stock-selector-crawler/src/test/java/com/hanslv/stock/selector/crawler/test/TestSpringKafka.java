package com.hanslv.stock.selector.crawler.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.KafkaUtil;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;

/**
 * 测试Spring整合Kafka
 * @author hanslv
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestSpringKafka {
	
	@Autowired
	private KafkaUtil<String , TabStockPriceInfo> kafkaUtil;
	
	
	/**
	 * 测试利用SpringKafka模块向Kafka发送消息
	 */
	@Test
	public void testSendMessage() {
		List<TabStockPriceInfo> messageList = new ArrayList<>();
		TabStockPriceInfo testInfo = new TabStockPriceInfo();
		testInfo.setStockPriceAmplitude("test3");
		messageList.add(testInfo);
		kafkaUtil.sendMessage("testSpringKafka", null, messageList);
		
		
		List<TabStockPriceInfo> messageList2 = new ArrayList<>();
		TabStockPriceInfo testInfo2 = new TabStockPriceInfo();
		testInfo2.setStockPriceAmplitude("test4");
		messageList2.add(testInfo2);
		kafkaUtil.sendMessage("testSpringKafka", null, messageList2);
		
		
		/*
		 * 防止多线程情况下JUnit提前结束主线程
		 */
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 测试利用SpringKafka模块接收Kafka发送消息
	 */
	public void testReceiveMessage() {
		
	}
}
