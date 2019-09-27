package com.hanslv.stock.selector.crawler.controllers;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanslv.stock.selector.crawler.services.CrawlerService;

@RestController
@RequestMapping("/crawler")
public class RestfulController {
	Logger logger = Logger.getLogger(RestfulController.class);
	
	/**
	 * 股票爬虫Service
	 */
	@Autowired
	private CrawlerService crawlerService;
	
	
	@GetMapping("/initStockInfo")
	public void initStockInfo() {
		crawlerService.initStockInfo();
	}
}
