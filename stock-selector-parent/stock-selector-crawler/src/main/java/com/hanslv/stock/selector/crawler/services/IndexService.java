package com.hanslv.stock.selector.crawler.services;

import java.time.LocalDate;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.selector.commons.dto.TabStockIndexMacd;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.crawler.repository.TabStockIndexMacdRepository;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.crawler.util.DbTabSelectLogicUtil;
import com.hanslv.stock.selector.crawler.util.StockIndexUtil;

/**
 * 股票指标Service
 * 
 * --------------------------------------------------
 * 1、计算全部股票的MACD信息										public void calculateAllStockTillToday()
 * 2、计算指定股票ID的MACD信息									public void calculateGivenStockTillToday(Integer stockId)
 * --------------------------------------------------
 * @author hanslv
 *
 */
@Service
public class IndexService {
	Logger logger = Logger.getLogger(IndexService.class);
	
	/*
	 * Mapper
	 */
	@Autowired
	private TabStockInfoRepository stockInfoMapper;
	@Autowired
	private TabStockPriceInfoRepository stockPriceMapper;
	@Autowired
	private TabStockIndexMacdRepository stockMacdMapper;
	
	/*
	 * 分表选择器
	 */
	@Autowired
	private DbTabSelectLogicUtil tableSelector;
	
	/**
	 * 1、计算全部股票的MACD信息
	 */
	public void calculateAllStockTillToday() {
		/*
		 * 获取全部股票信息
		 */
		List<TabStockInfo> stockInfoList = stockInfoMapper.selectAll();
		
		/*
		 * 当前日期
		 */
		LocalDate currentDate = LocalDate.now();
		
		/*
		 * 计算每只股票的MACD指标
		 */
		for(TabStockInfo stockInfo : stockInfoList) {
			/*
			 * 获取当前股票从上市到今天的全部股票价格信息
			 */
			List<TabStockPriceInfo> priceInfoList = stockPriceMapper.selectPriceInfoByStockIdAndBeforeDate(stockInfo.getStockId() , currentDate.toString());
			
			/*
			 * 计算当前股票的MACD信息
			 */
			List<TabStockIndexMacd> macdInfoList = StockIndexUtil.macdIndexCalculation(priceInfoList);
			
			/*
			 * 遍历插入
			 */
			for(TabStockIndexMacd macdInfo : macdInfoList) {
				/*
				 * 判断不存在当前信息再插入
				 */
				if(stockMacdMapper.selectOne(macdInfo.getStockId() , macdInfo.getDate()) == null) {
					stockMacdMapper.insertOne(tableSelector.tableSelector4IndexMacd(macdInfo) , macdInfo);
					logger.info("插入了一条MACD信息" + macdInfo);
				}else logger.error("当前信息已存在，跳过当前信息" + macdInfo);
			}
		}
	}
	
	
	/**
	 * 2、计算指定股票ID的MACD信息
	 * @param stockId
	 */
	public void calculateGivenStockTillToday(Integer stockId) {
		/*
		 * 当前日期
		 */
		LocalDate currentDate = LocalDate.now();
		
		/*
		 * 获取当前股票从上市到今天的全部股票价格信息
		 */
		List<TabStockPriceInfo> priceInfoList = stockPriceMapper.selectPriceInfoByStockIdAndBeforeDate(stockId , currentDate.toString());
		
		/*
		 * 计算当前股票的MACD信息
		 */
		List<TabStockIndexMacd> macdInfoList = StockIndexUtil.macdIndexCalculation(priceInfoList);
		
		/*
		 * 遍历插入
		 */
		for(TabStockIndexMacd macdInfo : macdInfoList) {
			/*
			 * 判断不存在当前信息再插入
			 */
			if(stockMacdMapper.selectOne(macdInfo.getStockId() , macdInfo.getDate()) == null) {
				stockMacdMapper.insertOne(tableSelector.tableSelector4IndexMacd(macdInfo) , macdInfo);
				logger.info("插入了一条MACD信息" + macdInfo);
			}else logger.error("当前信息已存在，跳过当前信息" + macdInfo);
		}
	}
}
