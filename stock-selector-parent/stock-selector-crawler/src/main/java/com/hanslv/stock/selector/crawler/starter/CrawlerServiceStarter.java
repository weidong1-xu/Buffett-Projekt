package com.hanslv.stock.selector.crawler.starter;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.crawler.constants.CrawlerKafkaConstants;

@SpringBootApplication
@ComponentScan({"com.hanslv.stock.selector"})
@MapperScan("com.hanslv.stock.selector")
public class CrawlerServiceStarter {
	public static void main(String[] args) {
		new SpringApplication(CrawlerServiceStarter.class).run(args);
	}
	
	/**
	 * 注入股票价格信息阻塞队列
	 * @return
	 */
	@Bean("stockPriceInfoBlockingQueue")
	public BlockingQueue<List<TabStockPriceInfo>> getStockPriceInfoBlockingQueue(){
		return new ArrayBlockingQueue<>(CrawlerKafkaConstants.PRICE_INFO_MESSAGE_QUEUE_SIZE);
	}
}
