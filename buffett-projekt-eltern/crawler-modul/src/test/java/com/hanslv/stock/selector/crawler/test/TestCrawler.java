package com.hanslv.stock.selector.crawler.test;

import org.jboss.logging.Logger;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.crawler.constants.CrawlerConstants;
import com.hanslv.crawler.starter.CrawlerServiceStarter;
import com.hanslv.crawler.util.CrawlerUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestCrawler {
	Logger logger = Logger.getLogger(TestCrawler.class);
	
	/**
	 * 测试获取股票信息页面HTML
	 */
	@Test
	public void testGetInfoHTML() {
//		Document documentResult = CrawlerUtil.getInstance().getHttpResponse(
//				CrawlerConstants.stockInfoTargetUrl , CrawlerConstants.stockEncoding);
//		logger.info(documentResult);
	}
	
	/**
	 * 测试获取股票价格
	 */
	@Test
	public void testGetPriceHTML() {
		Document documentResult = CrawlerUtil.getInstance().getHttpResponse(
				CrawlerConstants.stockPriceTargetUrlPrefix + "600505" + CrawlerConstants.stockPriceTargetShangzhengUrlSuffix , CrawlerConstants.stockEncoding);
		logger.info(documentResult);
	}
}
