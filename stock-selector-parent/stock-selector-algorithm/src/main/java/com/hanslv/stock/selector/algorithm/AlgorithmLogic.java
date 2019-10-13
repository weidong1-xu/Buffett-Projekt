package com.hanslv.stock.selector.algorithm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.selector.algorithm.constants.AlgorithmOtherConstants;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmInfoRepository;
import com.hanslv.stock.selector.algorithm.repository.TabAlgorithmResultRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.algorithm.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.selector.algorithm.util.DbTabSelectLogicUtil;
import com.hanslv.stock.selector.commons.constants.CommonsOtherConstants;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmInfo;
import com.hanslv.stock.selector.commons.dto.TabAlgorithmResult;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
import com.hanslv.stock.selector.commons.util.MyBatisUtil;

/**
 * 算法模板
 * 包含一个公用线程池、需要执行的算法队列、执行完毕算法计数器、上证指数StockID
 * 
 * 执行前准备：
 * 实例化公用线程池
 * 实例化需要执行的算法队列(泛型：TabAlgorithmInfo)
 * 实例化执行完毕算法计数器
 * 查询数据库中全部的算法ID、算法名称、算法类全名、算法时间区间并放入需要执行的算法队列中并将计数器
 * 
 * runAlgorithm()方法逻辑：
 * 提交N个任务到线程池：
 *  获取当前计数器的值
 * 	while(算法计数器!=0)循环：
 * 		从算法队列中取出一个TabAlgorithmInfo
 * 		数据库中比当前日期currentLastRunDate大的上证指数信息数量是否大于等于时间区间
 * 			是
 * 				用最大时间执行algorithmLogic()方法
 * 				将计算结果插入数据库
 * 				将当前算法TabAlgorithmInfo放入到需要执行的算法队列
 * 			否
 * 				执行完毕计数器-1
 * 				判断当前算法计数器是否为0
 * 					是
 * 						立即关闭线程池（shutDownNow）
 * 
 * 
 * 新创建的算法需要在数据库中初始化，包括（算法名称、算法类全名、算法时间区间）
 * 子类需要重写当前算法的算法逻辑algorithmLogic()
 * 
 * @author hanslv
 */
@Component
public class AlgorithmLogic {
	static Logger logger = Logger.getLogger(AlgorithmLogic.class);
	
	/*
	 * Redis操作Util
	 */
	@Autowired
//	private RedisUtil redisUtil;
	
	/*
	 * 公用线程池，调用runAlgorithm()方法向该线程池提交一个算法逻辑并运算
	 */
	private static ExecutorService publicThreadPool;
	
	/*
	 * 计数器
	 */
	private static AtomicInteger counter;
	
	/*
	 * 未完成算法队列
	 */
	private static BlockingQueue<TabAlgorithmInfo> incompleteBlockingQueue;
	
	/*
	 * 上证指数StockID
	 */
	private static String shangzhengStockId;
	
	
	/*
	 * Mappers
	 */
	@Autowired
	TabAlgorithmInfoRepository algorithmInfoMapper;
	@Autowired
	TabStockPriceInfoRepository priceInfoMapper;
	@Autowired
	TabStockInfoRepository stockInfoMapper;
	@Autowired
	TabAlgorithmResultRepository algorithmResultMapper;
	
	
	/**
	 * 运行全部算法
	 */
	public void runAlgorithm() {
		/*
		 * 初始化
		 */
		initAlgorithms();
		
		/*
		 * 提交N个任务
		 */
		for(int i = 0 ; i < AlgorithmOtherConstants.ALGORITHM_THREAD_POOL_SIZE ; i++) {
			publicThreadPool.execute(() -> {
				/*
				 * 获取当前计数器，while(算法计数器!=0)循环
				 * 
				 * 保持线程池最后被关闭的思路：
				 * counter>0，则说明当前队列中仍然存在算法信息，或仍然有算法正在被其他线程计算
				 * 
				 * 当有线程正在计算算法时counter不可能为0。
				 * 因为有线程正在计算说明当前存在未完成计算的算法，并且该算法对象是唯一的，只有在执行完当前算法时才可能对counter进行操作
				 * 
				 * 若当前存在未完成算法（counter不为0）但是该算法对象被其他线程持有，当前算法可能进入while循环，并且在从阻塞队列中取出算法信息时阻塞，
				 * 但最终会因为其他线程执行完毕后插入新的算法信息而继续，或因其他线程判断当前算法已经执行完毕并且将counter-1得0而关闭线程池结束
				 */
				while(getCounter() > 0) {
					TabAlgorithmInfo currentAlgorithmInfo = takeFromIncomplateBlockingQueue();
					
					/*
					 * 数据库中比当前日期currentLastRunDate大的上证指数信息数量是否大于等于时间区间
					 */
					if(checkLastRunDate(currentAlgorithmInfo.getAlgorithmDayCount() , currentAlgorithmInfo.getUpdateDate())) {
						/**
						 * 利用反射执行当前算法的algorithmLogic()方法
						 */
						try {
							AlgorithmLogic currentAlgorithm = (AlgorithmLogic) Class.forName(currentAlgorithmInfo.getAlgorithmClassName()).newInstance();
							String currentLastRunDate = currentAlgorithm.algorithmLogic(currentAlgorithmInfo.getUpdateDate());
							
							/*
							 * 设置当前对象的最后执行时间
							 */
							currentAlgorithmInfo.setUpdateDate(currentLastRunDate);
							
							/*
							 * 将当前算法放回阻塞队列
							 */
							putIncomplateAlgorithmInfo(currentAlgorithmInfo);
								
						} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					}else {
						/*
						 * 执行完毕计数器-1
						 */
						decrCounter();
						
						/*
						 * 判断当前算法计数器是否为0，是则关闭线程池
						 */
						if(getCounter() == 0) shutdownThreadPool();
						
						logger.info(Thread.currentThread() + " -------------------------算法 " + currentAlgorithmInfo.getAlgorithmName() + "的计算完成-------------------------");
						}
					}
				MyBatisUtil.getInstance().closeConnection();
			});
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 需要子类实现的算法逻辑
	 * 获取股票价格信息数据可以通过getPriceInfos()方法获取
	 * 因为使用反射调用，因此子类获取的实例不可以交由Spring管理
	 * 执行后需要调用updateAlgorithmUpdateDate()方法更新当前算法的最后执行时间TabAlgorithmInfo.updateDate
	 */
	String algorithmLogic(String lastRunDate) {return null;}
	
	
	/**
	 * 更新当前算法的最后执行日期
	 * @param currentAlgorithmClassName
	 * @param currentPriceInfoList 当前用于算法计算的股票价格信息List，用于计算当前算法的最后更新日期
	 */
	String updateAlgorithmUpdateDate(String currentAlgorithmClassName , List<TabStockPriceInfo> currentPriceInfoList) {
		TabAlgorithmInfoRepository mapper = MyBatisUtil.getInstance().getConnection().getMapper(TabAlgorithmInfoRepository.class);
		/*
		 * 将最大值加1
		 */
		LocalDate currentLastRunDate = LocalDate.parse(getMaxStockPriceInfoDateFromList(currentPriceInfoList)).plusDays(1);
		
		/*
		 * 更新当前算法的最后执行日期
		 */
		mapper.updateAlgorithmInfoUpdateDate(currentAlgorithmClassName , currentLastRunDate.toString());
		MyBatisUtil.getInstance().getConnection().commit();
		return currentLastRunDate.toString();
	}
	
	/**
	 * 获取指定日期之后指定数量指定股票ID的价格信息
	 * @param stockId
	 * @param startDate
	 * @param count
	 * @return
	 */
	List<TabStockPriceInfo> getPriceInfos(String stockId , String lastRunDate , String count){
		List<TabStockPriceInfo> resultPriceInfoList = new ArrayList<>();
		TabStockPriceInfoRepository mapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockPriceInfoRepository.class);
		
		/*
		 * 当获取到的lastRunDate为null
		 */
		if(lastRunDate == null) return mapper.selectPriceInfoByStockIdAndLimit(stockId , count);
		
		/*
		 * 尝试从缓存中获取
		 */
//		boolean notFoundInRedis = false;
//		for(int i = 0 ; i <= Integer.parseInt(count) ; i++) {
//			LocalDate currentPriceDate = LocalDate.parse(lastRunDate).plusDays(i);
//			String key = stockId + "," + currentPriceDate.toString();
//			TabStockPriceInfo priceInfoFromRedis = (TabStockPriceInfo) redisUtil.get(key);
//			if(priceInfoFromRedis == null) {
//				logger.info("没有在Redis中找到数据");
//				resultPriceInfoList = null;
//				notFoundInRedis = true;
//			}
//			else resultPriceInfoList.add(priceInfoFromRedis);
//		}
		
		
		/*
		 * 当有数据没有从Redis中获取到
		 */
//		if(notFoundInRedis) {
			/*
			 * 从数据库中查询比最后运行日期大的股票价格信息
			 */
			TabStockPriceInfo queryParam = new TabStockPriceInfo();
			queryParam.setStockId(Integer.parseInt(stockId));
			queryParam.setStockPriceDate(lastRunDate);
			
			resultPriceInfoList = mapper.selectByStockIdAndAfterDateLimit(queryParam , Integer.parseInt(count) + 1 + "");
			
			
			/*
			 * 将从数据库获取到的股票价格信息放入Redis中
			 */
//			for(TabStockPriceInfo priceInfo : resultPriceInfoList) {
//				String key = priceInfo.getStockId() + "," + priceInfo.getStockPriceDate();
//				/*
//				 * 设置超时时间并设置随机值
//				 */
//				Random random = new Random();
//				int randomExpire = random.ints(CommonsRedisConstants.PRICE_INFO_EXPIRE_RANDOM_START , CommonsRedisConstants.PRICE_INFO_EXPIRE_RANDOM_END).findFirst().getAsInt();
//				redisUtil.setWithExpire(key, priceInfo, CommonsRedisConstants.PRICE_INFO_EXPIRE + randomExpire, TimeUnit.SECONDS);
//			}
//		}
		return resultPriceInfoList;
	}
	
	/**
	 * 获取当前股票价格信息List中最大的日期
	 * @param priceInfoList
	 * @return
	 */
	String getMaxStockPriceInfoDateFromList(List<TabStockPriceInfo> priceInfoList) {
		String maxPriceDate = priceInfoList.get(0).getStockPriceDate();
		String maxPriceDateWithOutSperator = priceInfoList.get(0).getStockPriceDate().replaceAll("-", "");
		for(TabStockPriceInfo stockPriceInfo : priceInfoList) {
			String currentPriceDateWithOutSeparator = stockPriceInfo.getStockPriceDate().replaceAll("-", "");
			if(Integer.parseInt(maxPriceDateWithOutSperator) - Integer.parseInt(currentPriceDateWithOutSeparator) < 0) { 
				maxPriceDateWithOutSperator = currentPriceDateWithOutSeparator;
				maxPriceDate = stockPriceInfo.getStockPriceDate();
			}
		}
		return maxPriceDate;
	}
	
	/**
	 * 插入一条算法记录
	 * @param runDate
	 * @param stockId
	 */
	void insertAlgorithmResult(Integer algorithmId , List<TabStockPriceInfo> priceInfoList , Integer stockId) {
		TabAlgorithmResultRepository mapper = MyBatisUtil.getInstance().getConnection().getMapper(TabAlgorithmResultRepository.class);
		DbTabSelectLogicUtil tableSelector = new DbTabSelectLogicUtil();
		TabAlgorithmResult result = new TabAlgorithmResult();
		result.setRunDate(getMaxStockPriceInfoDateFromList(priceInfoList));
		result.setStockId(stockId);
		result.setAlgorithmId(algorithmId);
		mapper.insertResult(tableSelector.tableSelector4AlgorithmResult(result) , result);
		MyBatisUtil.getInstance().commitConnection();
	}
	
	/**
	 * 获取全部股票信息
	 * @return
	 */
	List<TabStockInfo> getAllStockInfo(){
		TabStockInfoRepository mapper = MyBatisUtil.getInstance().getConnection().getMapper(TabStockInfoRepository.class);
		return mapper.getAllStockInfo();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 初始化
	 */
	private void initAlgorithms() {
		publicThreadPool = Executors.newFixedThreadPool(AlgorithmOtherConstants.ALGORITHM_THREAD_POOL_SIZE);
		counter = new AtomicInteger();
		incompleteBlockingQueue = new ArrayBlockingQueue<>(CommonsOtherConstants.BASIC_BLOCKING_QUEUE_SIZE);
		
		/*
		 * 获取全部算法信息LIst
		 */
		List<TabAlgorithmInfo> algorithmInfoList = algorithmInfoMapper.getAllAlgorithmInfo();
		
		/*
		 * 将算法信息放入阻塞队列并初始化计数器
		 */
		for(TabAlgorithmInfo algorithmInfo : algorithmInfoList) {
			putIncomplateAlgorithmInfo(algorithmInfo);
			addCounter();
		}
		
		/*
		 * 上证指数StockId
		 */
		shangzhengStockId = String.valueOf(stockInfoMapper.getStockInfoByCode(CommonsOtherConstants.SHANGZHENG_ZHISHU_STOCK_CODE).getStockId());
	}
	
	
	/**
	 * 关闭公用线程池
	 */
	private void shutdownThreadPool() {
		if(!publicThreadPool.isShutdown()) {
			logger.info("-----------------------------------全部算法计算完成--------------------------------------");
			publicThreadPool.shutdown();
		}
	}
	
	
	/**
	 * 从未完成队列中取出算法信息
	 * @return
	 */
	private TabAlgorithmInfo takeFromIncomplateBlockingQueue() {
		TabAlgorithmInfo incomplateAlgorithmInfo = null;
		try {
			incomplateAlgorithmInfo = incompleteBlockingQueue.take();
		} catch (InterruptedException e) {
			logger.warn("线程池关闭，停止等待从incompleteBlockingQueue中获取算法信息........");
		}
		return incomplateAlgorithmInfo;
	}
	
	/**
	 * 向队列中放入未完成算法信息
	 * @param incomplateAlgorithmInfo
	 */
	private void putIncomplateAlgorithmInfo(TabAlgorithmInfo incomplateAlgorithmInfo) {
		try {
			incompleteBlockingQueue.put(incomplateAlgorithmInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	
	/**
	 * 计数器减1
	 */
	private int decrCounter() {
		return counter.decrementAndGet();
	}
	
	/**
	 * 计数器获取
	 * @return
	 */
	private int getCounter() {
		return counter.get();
	}
	
	/**
	 * 计数器加1
	 */
	private int addCounter() {
		return counter.incrementAndGet();
	}
	
	
	/**
	 * 数据库中比当前日期currentLastRunDate大的上证指数信息数量是否大于等于时间区间
	 * @param algorithmDayCount
	 * @param currentLastRunDate
	 * @return true需要继续执行
	 */
	private boolean checkLastRunDate(String algorithmDayCount , String currentLastRunDate) {
		/*
		 * 排除currentLastRunDate为空
		 */
		if(currentLastRunDate == null) return true;
		
		
		int currentShangzhengDayCount = Integer.parseInt(priceInfoMapper.selectPriceInfoCountByStockIdAndAfterDate(shangzhengStockId , currentLastRunDate));
		int currentDayCountInt = Integer.parseInt(algorithmDayCount);
		return currentShangzhengDayCount >= currentDayCountInt ? true : false;
	}
}
