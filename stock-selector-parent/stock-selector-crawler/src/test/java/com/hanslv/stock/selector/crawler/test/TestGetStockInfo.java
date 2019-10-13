package com.hanslv.stock.selector.crawler.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.crawler.constants.CrawlerConstants;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.starter.CrawlerServiceStarter;
import com.hanslv.stock.selector.crawler.util.CrawlerUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestGetStockInfo {
	Logger logger = Logger.getLogger(TestGetStockInfo.class);
	
	@Autowired
	private TabStockInfoRepository stockInfoMapper;
	
	/**
	 * 获取上证股票信息
	 */
	@Test
	public void getShangzhengStockInfos() {
		
		/**
		 * 用于存储股票基本信息
		 */
		List<TabStockInfo> stockInfoList = new LinkedList<>();
		
		/**
		 * 清空历史数据
		 */
		stockInfoMapper.deleteAll();
		logger.info("清空历史数据完成........");
		
		/**
		 * 获取对应的HTML Document对象
		 */
		Document htmlDocument = CrawlerUtil.getInstance().getHttpResponse(CrawlerConstants.stockInfoTargetUrl , CrawlerConstants.stockEncoding);
		
		/**
		 * 使用CSS选择器获取获取全部符合要求的节点
		 */
		Elements targetElements = htmlDocument.select(CrawlerConstants.shangzhengStockInfoCssSelector);
		
		/**
		 * 遍历节点中的信息
		 */
		Iterator<Element> targetElementsIterator = targetElements.iterator();
		while(targetElementsIterator.hasNext()) {
			TabStockInfo stockInfo = new TabStockInfo();
			/**
			 * 获取股票代码
			 */
			stockInfo.setStockCode(targetElementsIterator.next().text());
			
			
			/**
			 * 获取股票名称
			 */
			stockInfo.setStockName(targetElementsIterator.next().text());
			
			
			logger.info("获取到股票信息：" + stockInfo);
			stockInfoList.add(stockInfo);
		}
		
		
		/**
		 * 执行落库
		 */
		stockInfoMapper.insertList(stockInfoList);
		logger.info("插入完成！共插入：" + stockInfoList.size() + "只股票");
	}
}
