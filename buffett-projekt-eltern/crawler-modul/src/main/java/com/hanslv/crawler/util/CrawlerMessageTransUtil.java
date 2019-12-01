package com.hanslv.crawler.util;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hanslv.allgemein.dto.TabStockPriceInfo;

/**
 * 消息传输工具类
 * 包括股票价格信息消息队列以及与其他中间件模块交互的方法
 * ---------------------------------------
 * 1、向队列中写入股票价格信息List						public void writePriceInfoToQueue(List<TabStockPriceInfo> priceInfoList)
 * ---------------------------------------
 * @author hanslv
 *
 */
@Component
public class CrawlerMessageTransUtil {
	Logger logger = Logger.getLogger(CrawlerMessageTransUtil.class);
	
	@Autowired
	@Qualifier("stockPriceInfoBlockingQueue")
	private BlockingQueue<List<TabStockPriceInfo>> stockPriceInfoQueue;
	
	/**
	 * 1、向队列中写入股票价格信息List
	 * @param priceInfoList
	 */
	public void writePriceInfoToQueue(List<TabStockPriceInfo> priceInfoList) {
		/*
		 * 将股票价格信息写入消息队列
		 */
		try {
			stockPriceInfoQueue.put(priceInfoList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
