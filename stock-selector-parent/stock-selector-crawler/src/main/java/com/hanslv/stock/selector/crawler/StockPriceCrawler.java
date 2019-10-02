package com.hanslv.stock.selector.crawler;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;
import com.hanslv.stock.selector.crawler.constants.CrawlerConstants;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.util.CrawlerUtil;
import com.hanslv.stock.selector.crawler.util.CrawlerMessageTransUtil;

/**
 * 股票价格爬虫，实现Callable接口
 * @author hanslv
 *
 */
public class StockPriceCrawler implements Runnable{
	static Logger logger = Logger.getLogger(StockPriceCrawler.class);
	/*
	 * 全部股票基本信息List
	 */
	private static List<TabStockInfo> stockInfoList;
	
	/*
	 * 原子计数器，每个线程通过这个数字判断从stockInfoList获取下一个股票基本信息的index
	 */
	private static AtomicInteger listIndexCounter;
	
	static {
		listIndexCounter = new AtomicInteger();
		
		/*
		 * 初始化全部股票基本信息List
		 */
		try {
			stockInfoList = Collections.synchronizedList(
					MyBatisUtil
					.getInstance()
					.getConnection()
					.getMapper(TabStockInfoRepository.class)
					.selectAll());
			logger.info("共获取到了：" + stockInfoList.size() + "条股票基本信息");
			/*
			 * 当获取到的股票基本信息数量为0时，考虑是否没有初始化股票基本信息表
			 */
			if(stockInfoList.size() == 0)
				throw  new Exception("股票基本信息获取失败");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			MyBatisUtil.getInstance().closeConnection();
		}
	}
	
	
	/**
	 * 从全部股票基本信息List中获取一个股票信息，
	 * 执行爬取逻辑并向消息队列中写入一个包含当前股票N天(在Properties文件中指定)价格信息的List
	 */
	@Override
	public void run() {
		/*
		 * 从股票信息List中获取记录，
		 * 当前下标小于List集合长度时继续执行
		 */
		int currentIndex = 0;//获取当前股票信息List下标
		while((currentIndex = listIndexCounter.getAndIncrement()) < stockInfoList.size()) {
			TabStockInfo stockInfo = stockInfoList.get(currentIndex);
			
			/*
			 * 获取页面信息并存入List
			 */
			JSONObject bodyTextJsonObject = getJsonObject(stockInfo);
			if(bodyTextJsonObject != null) {
				/*
				 * 向KafkaUtil的消息队列中写入一个List<TabStockPriceInfo>
				 */
				CrawlerMessageTransUtil
					.getInstance()
					.writeAMessageIntoPriceInfoMessageQueue(
							parseJsonObjectToList(bodyTextJsonObject , stockInfo.getStockId()));
			}else 
				logger.warn("当前股票已退市或不存在：" + stockInfo);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 通过股票信息爬取到对应的价格信息并转换成JSONObject
	 * @return
	 */
	private JSONObject getJsonObject(TabStockInfo stockInfo) {
		logger.info(Thread.currentThread() + " 正在爬取股票：" + stockInfo.getStockName() + "，" + stockInfo.getStockCode() + "的信息");
		
		String targetUrl = CrawlerConstants.stockPriceTargetUrlPrefix + stockInfo.getStockCode();//网址
		/**
		 * 判断是否为上证股票
		 */
		if(stockInfo.getStockCode().indexOf("6") != 0) 
			targetUrl += CrawlerConstants.stockPriceTargetShenzhengUrlSuffix;
		else
			targetUrl += CrawlerConstants.stockPriceTargetShangzhengUrlSuffix;
		
		
		/*
		 * 获取返回结果的字符串对象
		 */
		StringBuffer contextStringBuffer = new StringBuffer(
				CrawlerUtil
				.getInstance()
				.getHttpResponse(targetUrl, CrawlerConstants.stockEncoding)
				.select("body")
				.text());
		
		/*
		 * 排除股票退市或其他情况
		 */
		if(contextStringBuffer.length() != 0 && contextStringBuffer.indexOf("name") != -1) {
			/*
			 * 爬虫取回的数据中包含了多余的报文，
			 * 在此处除去多余部分
			 */
			int subIndex = contextStringBuffer.indexOf("name") - 2;
			String finalContextStringBuffer = contextStringBuffer.substring(subIndex , contextStringBuffer.length()-1);
			return JSONObject.parseObject(finalContextStringBuffer);
		}else 
			return null;
	}
	
	
	/**
	 * 将传入的JSONObject对象转换为TabStockPriceInfo的List
	 * @param stockPriceInfos
	 * @return
	 */
	private List<TabStockPriceInfo> parseJsonObjectToList(JSONObject stockPriceInfos , Integer stockCode){
		List<TabStockPriceInfo> priceInfoList = new LinkedList<>();
		
		/**
		 * 获取JSONObject中对应的JSONArray数组
		 */
		JSONArray resultArray = stockPriceInfos.getJSONArray("data");
		
		/**
		 * 遍历数组并将数据封装成TabStockPriceInfo对象
		 */
		Iterator<Object> arrayIterator = resultArray.iterator();
		while(arrayIterator.hasNext()) {
			TabStockPriceInfo currentPriceInfo = new TabStockPriceInfo();
			String[] result = String.valueOf(arrayIterator.next()).split(",");
			
			/**
			 * 跳过数据缺失的价格信息
			 */
			if(result.length < 9) {
				logger.error("--------------股票" + stockCode + "在今天："+ new Date() +" 获取到数据有缺失，请检查数据---------------------");
				continue;
			}
			
			
			/**
			 * 数据样本：2019-07-02,8.78,8.83,8.94,8.68,40466,35837123,2.96%,1.5
			 * 日期： 2019-07-02,
			 * 开盘价： 8.78,
			 * 收盘价： 8.83,
			 * 最高价： 8.94,
			 * 最低价： 8.68,
			 * 成交量： 40466,
			 * 成交额： 35837123,
			 * 振幅： 2.96%,
			 * 换手率： 1.5
			 */
			currentPriceInfo.setStockId(stockCode);
			currentPriceInfo.setStockPriceDate(result[0]);
			currentPriceInfo.setStockPriceStartPrice(new BigDecimal(result[1]));
			currentPriceInfo.setStockPriceEndPrice(new BigDecimal(result[2]));
			currentPriceInfo.setStockPriceHighestPrice(new BigDecimal(result[3]));
			currentPriceInfo.setStockPriceLowestPrice(new BigDecimal(result[4]));
			currentPriceInfo.setStockPriceVolume(new Integer(result[5]));
			String turnover = result[6].length() >= 5 ? result[6].substring(0, result[6].length() - 4) : "0";//万单位，当小于1万时为0
			currentPriceInfo.setStockPriceTurnover(new Integer(turnover));
			currentPriceInfo.setStockPriceAmplitude(result[7]);
			currentPriceInfo.setStockPriceTurnoverRate(new BigDecimal(result[8]));
			
			
			/**
			 * 加入List中
			 */
			priceInfoList.add(currentPriceInfo);
		}
		return priceInfoList;
	}



















}
