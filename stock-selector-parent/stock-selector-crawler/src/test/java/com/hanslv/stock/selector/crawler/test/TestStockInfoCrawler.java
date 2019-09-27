package com.hanslv.stock.selector.crawler.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.crawler.StockInfoCrawler;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestStockInfoCrawler {
	
	/**
	 * 测试从股票网站中获取全部股票基本信息
	 */
	@Test
	public void testGetList() {
		StockInfoCrawler crawler = new StockInfoCrawler();
		crawler.runCrawler();
	}
}
