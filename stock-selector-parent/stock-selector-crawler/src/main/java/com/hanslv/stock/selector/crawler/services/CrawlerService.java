package com.hanslv.stock.selector.crawler.services;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.crawler.StockInfoCrawler;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;

/**
 * 爬虫模块Service
 * 
 * -----------------------------------------
 * 1、初始化股票基本信息表													public void initStockInfo()
 * -----------------------------------------
 * @author hanslv
 *
 */
@Service
public class CrawlerService {
	Logger logger = Logger.getLogger(CrawlerService.class);
	
	@Autowired
	private TabStockInfoRepository stockInfoMapper;
	
	/**
	 * 1、初始化股票基本信息表，
	 * 通过爬虫爬取回全部股票基本信息后，先清空原有库中全部股票基本信息，之后再将新的信息存入数据库
	 */
	public void initStockInfo() {
		/*
		 * 爬取全部股票基本信息并存放到stockInfoList中
		 */
		StockInfoCrawler crawler = new StockInfoCrawler();
		List<TabStockInfo> stockInfoList = crawler.runCrawler();
		
		/*
		 * 判断爬取回结果是否为空
		 */
		if(stockInfoList.size() == 0) {
			logger.error("----------------爬取回结果为空，请检查爬虫是否正常！----------------");
			return;
		}
		
		/*
		 * 首先清空原有数据
		 */
		stockInfoMapper.deleteAll();			
		/*
		 * 插入新数据
		 */
		stockInfoMapper.insertList(stockInfoList);
		/*
		 * 提交数据
		 */
		logger.info("初始化数据库基本信息完毕！");
	}
}
