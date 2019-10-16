package com.hanslv.stock.selector.algorithm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hanslv.stock.selector.algorithm.services.PriceInfoConsumerService;

@Controller
@RequestMapping("/algorithm-service")
public class RestfulController {
	@Autowired
	private PriceInfoConsumerService priceInfoService;
	
	@GetMapping("/price-consumer-run")
	public void runPriceInfoConsumer() {
		priceInfoService.runConsumer();
	}
	
	
}
