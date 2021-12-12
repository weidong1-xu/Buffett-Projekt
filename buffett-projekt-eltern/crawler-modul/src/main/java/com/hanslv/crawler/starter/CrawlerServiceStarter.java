package com.hanslv.crawler.starter;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.hanslv.allgemein.dto.TabStockPriceInfo;
import com.hanslv.crawler.constants.CrawlerConstants;

@SpringBootApplication
@ComponentScan({"com.hanslv"})
@MapperScan("com.hanslv")
public class CrawlerServiceStarter {
    public static void main(String[] args) {
        new SpringApplication(CrawlerServiceStarter.class).run(args);
    }

    /**
     * 注入股票价格信息阻塞队列
     *
     * @return
     */
    @Bean("stockPriceInfoBlockingQueue")
    public BlockingQueue<List<TabStockPriceInfo>> getStockPriceInfoBlockingQueue() {
        return new ArrayBlockingQueue<>(CrawlerConstants.PRICE_INFO_MESSAGE_QUEUE_SIZE);
    }
}
