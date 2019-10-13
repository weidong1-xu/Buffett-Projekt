package com.hanslv.stock.selector.crawler.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.hanslv.stock.selector"})
@MapperScan("com.hanslv.stock.selector")
public class CrawlerServiceStarter {
	public static void main(String[] args) {
		new SpringApplication(CrawlerServiceStarter.class).run(args);
	}
}
