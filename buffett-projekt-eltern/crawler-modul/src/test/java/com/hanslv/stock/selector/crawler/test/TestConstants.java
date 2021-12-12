package com.hanslv.stock.selector.crawler.test;

import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.crawler.constants.CrawlerConstants;
import com.hanslv.crawler.starter.CrawlerServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerServiceStarter.class)
public class TestConstants {
    Logger logger = Logger.getLogger(TestConstants.class);

    /**
     * 测试常量类
     */
    @Test
    public void testConstants() {
        logger.info(CrawlerConstants.stockInfoTargetUrl);
    }
}
