package com.hanslv.stock.machine.learning.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.hanslv.stock"})
@MapperScan("com.hanslv.stock")
public class StockMachineLearningStarter {
	public static void main(String[] args) {
		new SpringApplication(StockMachineLearningStarter.class).run(args);
	}
}
