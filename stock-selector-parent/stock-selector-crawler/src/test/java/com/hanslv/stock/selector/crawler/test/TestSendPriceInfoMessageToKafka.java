package com.hanslv.stock.selector.crawler.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.crawler.StockPriceCrawler;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;

/**
 * 向Kafka发送股票价格信息
 * @author hanslv
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerServiceStarter.class)
public class TestSendPriceInfoMessageToKafka {
	@Autowired
	private StockPriceCrawler priceCrawler;
	
	/**
	 * 发送信息
	 */
	@Test
	public void sendMessage() {
		priceCrawler.runCrawler();
		
		
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
