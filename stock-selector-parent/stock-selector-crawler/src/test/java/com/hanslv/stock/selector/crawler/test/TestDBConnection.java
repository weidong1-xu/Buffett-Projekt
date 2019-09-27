package com.hanslv.stock.selector.crawler.test;

import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.commons.util.MyBatisUtil;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestDBConnection {
	Logger logger = Logger.getLogger(TestDBConnection.class);
	
	@Test
	public void testDBConnection() {
		logger.info(MyBatisUtil.getInstance().getConnection().getMapper(TabStockInfoRepository.class).getCount());
	}
}
