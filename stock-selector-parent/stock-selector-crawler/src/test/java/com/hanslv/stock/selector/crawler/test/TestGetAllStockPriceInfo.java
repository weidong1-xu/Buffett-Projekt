package com.hanslv.stock.selector.crawler.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.crawler.StockPriceCrawler;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestGetAllStockPriceInfo {
	Logger logger = Logger.getLogger(TestGetAllStockPriceInfo.class);
	
	/**
	 * 测试获取一只股票的全部价格数据
	 */
	@Test
	public void testGetAStockPriceInfo() {
		ExecutorService service = Executors.newFixedThreadPool(1);
		try {
			service.execute(new StockPriceCrawler());
//			logger.info("共获取到了：" + futureTask.get().size() + "条数据");
		}finally {
			service.shutdown();
		}
	}
}
