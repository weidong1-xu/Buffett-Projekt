package com.hanslv.stock.selector.crawler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.crawler.constants.CrawlerConstants;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.util.CrawlerUtil;

/**
 * 股票基本信息爬虫
 * @author harrylu
 *
 */
@Component
public class StockInfoCrawler{
	Logger logger = Logger.getLogger(StockInfoCrawler.class);
	
	@Autowired
	private TabStockInfoRepository stockInfoMapper;
	
	/**
	 * 执行爬虫操作
	 * 会将获取到的全部股票信息List与当前存在的股票信息取差集
	 */
	public List<TabStockInfo> runCrawler() {
		/**
		 * 获取对应的HTML Document对象
		 */
		Document htmlDocument = CrawlerUtil.getInstance().getHttpResponse(CrawlerConstants.stockInfoTargetUrl , CrawlerConstants.stockEncoding);
		
		/*
		 * 使用CSS选择器获取获取全部符合要求的上证股票节点
		 */
		Elements shangzhengTargetElements = htmlDocument.select(CrawlerConstants.shangzhengStockInfoCssSelector);
		
		/*
		 * 使用CSS选择器获取获取全部符合要求的深证股票节点
		 */
		Elements shenzhengTargetElements = htmlDocument.select(CrawlerConstants.shenzhengStockInfoCssSelector);
		
		
		/*
		 * 全部股票List
		 */
		List<TabStockInfo> stockInfoList = stockInfoCrawlerLogic(shangzhengTargetElements);
		stockInfoList.addAll(stockInfoCrawlerLogic(shenzhengTargetElements));
		
		/*
		 * 加入指数信息
		 */
		TabStockInfo shangzhengStockInfo = new TabStockInfo();
		shangzhengStockInfo.setStockCode(CommonsOtherConstants.SHANGZHENG_ZHISHU_STOCK_CODE);
		shangzhengStockInfo.setStockName(CommonsOtherConstants.SHANGZHENG_ZHISHU_STOCK_NAME);
		TabStockInfo shenzhengStockInfo = new TabStockInfo();
		shenzhengStockInfo.setStockCode(CommonsOtherConstants.SHENZHENG_ZHISHU_STOCK_CODE);
		shenzhengStockInfo.setStockName(CommonsOtherConstants.SHENZHENG_ZHISHU_STOCK_NAME);
		TabStockInfo chuangyeStockInfo = new TabStockInfo();
		chuangyeStockInfo.setStockCode(CommonsOtherConstants.CHUANGYEBAN_ZHISHU_STOCK_CODE);
		chuangyeStockInfo.setStockName(CommonsOtherConstants.CHUANGYEBAN_ZHISHU_STOCK_NAME);
		
		stockInfoList.add(shangzhengStockInfo);
		stockInfoList.add(shenzhengStockInfo);
		stockInfoList.add(chuangyeStockInfo);
		
		/*
		 * 首先获取当前存在的股票信息
		 */
		List<TabStockInfo> existStockInfoList = stockInfoMapper.selectAll();
		for(TabStockInfo existStockInfo : existStockInfoList) existStockInfo.setStockId(null);
		
		/*
		 * 当前存在股票信息List和获取到的全部股票信息List取差集
		 */
		stockInfoList.removeAll(existStockInfoList);
		
		logger.info("------------------------共获取到：" + stockInfoList.size() + "只股票基本信息------------------------");
		return stockInfoList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 股票信息爬虫执行逻辑，将全部股票信息转换为List
	 * @param targetElements
	 * @return
	 */
	private List<TabStockInfo> stockInfoCrawlerLogic(Elements targetElements){
		List<TabStockInfo> stockInfoList = new LinkedList<>();
		Iterator<Element> targetElementsIterator = targetElements.iterator();
		while(targetElementsIterator.hasNext()) {
			TabStockInfo stockInfo = new TabStockInfo();
			
			/**
			 * 获取当前股票代码
			 */
			Element currentStockCodeElement = targetElementsIterator.next();
			stockInfo.setStockCode(CrawlerUtil.getInstance().getTextFromElement(currentStockCodeElement));
			
			/**
			 * 获取当前股票名称
			 */
			Element currentStockNameElement = targetElementsIterator.next();
			stockInfo.setStockName(CrawlerUtil.getInstance().getTextFromElement(currentStockNameElement));
			
//			logger.info(Thread.currentThread() + " 获取到股票信息：" + stockInfo.getStockCode() + "，" + stockInfo.getStockName());
			stockInfoList.add(stockInfo);
		}
		return stockInfoList;
	}
}
