package com.hanslv.stock.selector.crawler.test;

import java.util.List;

import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestGetAllStockInfoFromDB {
	Logger logger = Logger.getLogger(TestGetAllStockInfoFromDB.class);
	
	/**
	 * 从数据库中获取全部股票基本信息
	 */
	@Test
	public void getAllStockInfoFromDB() {
		try {
			List<TabStockInfo> resultList = MyBatisUtil.getInstance().getConnection().getMapper(TabStockInfoRepository.class).selectAll();
			for(TabStockInfo stockInfo : resultList) {
				logger.info("获取到股票信息：" + stockInfo);
			}
		}finally {
			MyBatisUtil.getInstance().closeConnection();
		}
	}
}
