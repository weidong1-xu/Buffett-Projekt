package com.hanslv.stock.selector.crawler.util;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.crawler.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.crawler.repository.TabStockPriceInfoRepository;

/**
 * 将爬取回的股票价格信息存入数据库
 * 
 * ------------------------------------------
 * 1、向线程池提交一个任务并从消息队列中接收股票价格信息并写入数据库								public void savePriceInfoToDB()
 * ------------------------------------------
 * @author admin
 *
 */
@Component
public class StockPriceInfoSaver {
	Logger logger = Logger.getLogger(StockPriceInfoSaver.class);
	
	private ExecutorService threadPool;
	
	@Autowired
	@Qualifier("stockPriceInfoBlockingQueue")
	private BlockingQueue<List<TabStockPriceInfo>> priceInfoBlockingQueue;
	
	@Autowired
	private TabStockPriceInfoRepository stockPriceInfoMapper;
	@Autowired
	private TabStockInfoRepository stockInfoMapper;
	
	@Autowired
	private DbTabSelectLogicUtil dbTabSelector;
	
	/*
	 * 线程状态Map
	 */
	private Map<String , Thread> threadStateMap;
	
	
	/**
	 * 1、向线程池提交一个任务并从消息队列中接收股票价格信息并写入数据库
	 */
	public void savePriceInfoToDB() {
		init();
		
		threadPool.execute(() -> {
			/*
			 * 将当前线程状态放入Map
			 */
			threadStateMap.put(Thread.currentThread().getName() , Thread.currentThread());
			
			/*
			 * 当结束标识为true时循环
			 */
			while(true) {
				List<TabStockPriceInfo> currentPriceInfoList = null;
				try {
					currentPriceInfoList = priceInfoBlockingQueue.take();
				} catch (InterruptedException e) {
					logger.info(Thread.currentThread() + "停止等待队列中数据");
					break;
				}
				
				/*
				 * 判断是否为结束标识，结束时会在消息队列中写入一个空List
				 */
				if(currentPriceInfoList.size() != 0) {
					for(TabStockPriceInfo currentPriceInfo : currentPriceInfoList) {
						String tableName = dbTabSelector.tableSelector4PriceInfo(currentPriceInfo, stockInfoMapper);
						/*
						 * 判断当前信息是否存在（已爬取过）
						 */
						if(stockPriceInfoMapper.selectOne(tableName, currentPriceInfo) == null) {
							/*
							 * 将价格信息落库
							 */
							stockPriceInfoMapper.insertOne(tableName, currentPriceInfo);
							logger.info("插入了一条数据：" + currentPriceInfo);
						}else logger.error("当前数据存在，已经跳过：" + currentPriceInfo);
					}
				}else {
					/*
					 * 中断所有正在等待的线程
					 */
					for(Thread thread : threadStateMap.values()) {
						if(State.WAITING.equals(thread.getState())) thread.interrupt();
					}
					
					/*
					 * 通知关闭当前线程池
					 */
					threadPool.shutdown();
					
					break;
				}
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 初始化
	 */
	private void init() {
		if(threadPool == null || threadPool.isTerminated()) {
			threadPool = Executors.newFixedThreadPool(CommonsOtherConstants.BASIC_THREAD_POOL_SIZE);
			threadStateMap = new HashMap<>();
		}
	}
}
