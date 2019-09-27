package com.hanslv.stock.selector.web.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebServiceStarter {
	public static void main(String[] args) {
		new SpringApplication(WebServiceStarter.class).run(args);
	}
}
