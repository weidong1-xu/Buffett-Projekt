package com.hanslv.stock.selector.crawler.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.crawler.services.CrawlerService;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestInitStockInfo {
	
	@Autowired
	private CrawlerService service;
	
	/**
	 * 测试从股票网站中获取全部股票基本信息
	 */
	@Test
	public void testGetList() {
		service.initStockInfo();
	}
}
