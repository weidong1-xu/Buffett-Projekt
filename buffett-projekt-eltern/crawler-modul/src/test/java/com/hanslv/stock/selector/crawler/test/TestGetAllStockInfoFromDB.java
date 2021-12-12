package com.hanslv.stock.selector.crawler.test;

import java.util.List;

import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.allgemein.dto.TabStockInfo;
import com.hanslv.crawler.repository.TabStockInfoRepository;
import com.hanslv.crawler.starter.CrawlerServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerServiceStarter.class)
public class TestGetAllStockInfoFromDB {
    Logger logger = Logger.getLogger(TestGetAllStockInfoFromDB.class);

    @Autowired
    private TabStockInfoRepository stockInfoMapper;

    /**
     * 从数据库中获取全部股票基本信息
     */
    @Test
    public void getAllStockInfoFromDB() {
        List<TabStockInfo> resultList = stockInfoMapper.selectAll();
        for (TabStockInfo stockInfo : resultList) {
            logger.info("获取到股票信息：" + stockInfo);
        }
    }
}
