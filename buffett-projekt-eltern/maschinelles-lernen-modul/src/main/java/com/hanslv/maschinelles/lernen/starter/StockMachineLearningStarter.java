package com.hanslv.maschinelles.lernen.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.hanslv"})
@MapperScan("com.hanslv")
public class StockMachineLearningStarter {
	public static void main(String[] args) {
		new SpringApplication(StockMachineLearningStarter.class).run(args);
	}
}
