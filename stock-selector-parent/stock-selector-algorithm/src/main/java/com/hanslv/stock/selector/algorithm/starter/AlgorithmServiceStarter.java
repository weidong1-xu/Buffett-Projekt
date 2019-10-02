package com.hanslv.stock.selector.algorithm.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 算法模块启动类
 * @author hanslv
 *
 */
@SpringBootApplication
public class AlgorithmServiceStarter {
	public static void main(String[] args) {
		new SpringApplication(AlgorithmServiceStarter.class).run(args);
	}
}
