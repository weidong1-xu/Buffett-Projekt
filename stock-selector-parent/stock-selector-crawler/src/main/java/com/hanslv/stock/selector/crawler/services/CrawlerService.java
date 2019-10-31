package com.hanslv.stock.selector.crawler.services;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.crawler.StockInfoCrawler;
import com.hanslv.stock.selector.crawler.StockPriceCrawler;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.util.StockPriceInfoSaver;

/**
 * 爬虫模块Service
 * 
 * -----------------------------------------
 * 1、初始化股票基本信息表													public void runStockInfoCrawler()
 * 2、执行股票价格信息爬虫													public void runStockPriceCrawler()
 * -----------------------------------------
 * @author hanslv
 *
 */
@Service
public class CrawlerService {
	Logger logger = Logger.getLogger(CrawlerService.class);
	
	@Autowired
	private TabStockInfoRepository stockInfoMapper;
	
	@Autowired
	private StockInfoCrawler stockInfoCrawler;
	@Autowired
	private StockPriceInfoSaver priceInfoSaver;
	
	@Autowired
	private StockPriceCrawler stockPriceCrawler;
	
	/**
	 * 1、初始化股票基本信息表
	 */
	public void runStockInfoCrawler() {
		/*
		 * 爬取全部股票基本信息并存放到stockInfoList中
		 */
		List<TabStockInfo> stockInfoList = stockInfoCrawler.runCrawler();
		
		/*
		 * 判断爬取回结果是否为空
		 */
		if(stockInfoList.size() == 0) {
			logger.error("----------------爬取回结果为空，请检查爬虫是否正常！----------------");
			return;
		}
		
		/*
		 * 插入新数据
		 */
		stockInfoMapper.insertList(stockInfoList);
		
		/*
		 * 提交数据
		 */
		logger.info("初始化数据库基本信息完毕！");
	}
	
	
	
	/**
	 * 2、执行股票价格信息爬虫
	 */
	public void runStockPriceCrawler() {
		new Thread(() -> {
			stockPriceCrawler.runCrawler();
		}).start();
		
		/*
		 * 将股票价格信息存入数据库
		 */
		for(int i = 0 ; i < CommonsOtherConstants.BASIC_THREAD_POOL_SIZE ; i++) {
			priceInfoSaver.savePriceInfoToDB();
		}
	}
	
}
