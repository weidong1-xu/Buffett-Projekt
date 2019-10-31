package com.hanslv.stock.selector.algorithm.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 算法模块启动类
 * @author hanslv
 *
 */
@SpringBootApplication
@ComponentScan({"com.hanslv.stock.selector"})
@MapperScan("com.hanslv.stock.selector")
@Deprecated
public class AlgorithmServiceStarter {
	public static void main(String[] args) {
		new SpringApplication(AlgorithmServiceStarter.class).run(args);
	}
}
