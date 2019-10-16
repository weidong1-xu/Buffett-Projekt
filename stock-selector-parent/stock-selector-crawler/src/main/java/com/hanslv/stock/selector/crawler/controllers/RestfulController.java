package com.hanslv.stock.selector.crawler.controllers;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanslv.stock.selector.crawler.services.CrawlerService;
/**
 * -----------------------------------------------
 * 1、执行股票基本信息爬虫										public void getStockInfo()
 * 2、执行股票价格信息爬虫										public void getStockPrice()
 * -----------------------------------------------
 * @author hanslv
 *
 */
@RestController
@RequestMapping("/crawler-service")
public class RestfulController {
	Logger logger = Logger.getLogger(RestfulController.class);
	
	/**
	 * 股票爬虫Service
	 */
	@Autowired
	private CrawlerService crawlerService;
	
	/**
	 * 1、执行股票基本信息爬虫
	 */
	@GetMapping("/stock-info")
	public void getStockInfo() {
		crawlerService.runStockInfoCrawler();
	}
	
	
	/**
	 * 2、执行股票价格信息爬虫
	 */
	@GetMapping("/stock-price")
	public void getStockPrice() {
		crawlerService.runStockPriceCrawler();
	}
}
