package com.hanslv.stock.machine.learning.neural.network.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.machine.learning.constants.NeuralNetworkConstants;
import com.hanslv.stock.machine.learning.neural.network.DatePriceNNTrainer;
import com.hanslv.stock.machine.learning.neural.network.DatePriceProphet;
import com.hanslv.stock.machine.learning.repository.TabPriceDateMLResultFiveDaysRepository;
import com.hanslv.stock.machine.learning.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.commons.dto.TabPriceDateMLResultFiveDays;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;

/**
 * 股票神经网络训练
 * 
 * -----------------------------------------
 * 1、1、从指定ID开始训练全部股票日期-价格模型						public void trainStockNN(Integer stockId)
 * 2、训练指定股票的日期-价格模型									public void trainAStockNN(Integer stockId)
 * 3、预测全部股票												public void calculateStock()
 * 4、预测指定股票												public void calculateStock(Integer stockId)
 * 5、获取并将全部预测上涨的股票输出到控制台						public void getResult()
 * -----------------------------------------
 * @author hanslv
 *
 */
@Service
public class NeuralNetworkService {
	Logger logger = Logger.getLogger(NeuralNetworkService.class);
	
	/*
	 * 时间-价格神经网络
	 */
	@Autowired
	private DatePriceNNTrainer datePriceNNTrainer;
	
	/*
	 * 时间-价格预测
	 */
	@Autowired
	private DatePriceProphet prophet;
	
	@Autowired
	private TabStockInfoRepository tabStockInfoMapper;
	@Autowired
	private TabPriceDateMLResultFiveDaysRepository mlResultMapper;
	
	

	/**
	 * 1、从指定ID开始训练全部股票日期-价格模型
	 * @param stockId
	 */
	public void trainStockNN(Integer stockId) {
		/*
		 * 获取全部股票基本信息
		 */
		List<TabStockInfo> stockInfoList = tabStockInfoMapper.selectAllStockInfo();
		
		/*
		 * 遍历全部股票信息并运行算法，算法将会被储存到NeuralNetworkConstants.ALGORITHM_BASE_DIR文件夹
		 */
		for(TabStockInfo stockInfo : stockInfoList) {
			if(stockInfo.getStockId().compareTo(stockId) < 0) continue;
			datePriceNNTrainer.trainNN(stockInfo.getStockId() , NeuralNetworkConstants.TRAIN_ERROR_LIMIT);
		}
		
	}
	
	
	
	/**
	 * 2、训练指定股票的日期-价格模型
	 */
	public void trainAStockNN(Integer stockId) {
		datePriceNNTrainer.trainNN(stockId , NeuralNetworkConstants.TRAIN_ERROR_LIMIT);
	}
	
	
	
	/**
	 * 3、预测全部股票
	 */
	public void calculateStock() {
		/*
		 * 运算结果
		 */
		List<TabPriceDateMLResultFiveDays> mlResultList = new ArrayList<>();
		
		/*
		 * 获取全部股票基本信息
		 */
		List<TabStockInfo> stockInfoList = tabStockInfoMapper.selectAllStockInfo();
		
		/*
		 * 当前日期
		 */
		LocalDate currentDateLocalDate = LocalDate.now();
		
		/*
		 * 对每只股票进行预测
		 */
		for(TabStockInfo stockInfo : stockInfoList) {
			TabPriceDateMLResultFiveDays mlResult = prophet.saySomething(stockInfo.getStockId() , currentDateLocalDate.toString());
			if(mlResult != null) mlResultList.add(mlResult);
		}
		
		/*
		 * 将全部预测结果存入数据库
		 */
		mlResultMapper.insertList(mlResultList);
	}
	
	
	/**
	 * 4、预测指定股票
	 * @param stockId
	 */
	public void calculateStock(Integer stockId) {
		/*
		 * 当前日期
		 */
		LocalDate currentDateLocalDate = LocalDate.now();
		
		/*
		 * 进行预测
		 */
		TabPriceDateMLResultFiveDays mlResult = prophet.saySomething(stockId , currentDateLocalDate.toString());
		
		/*
		 * 输出结果到控制台
		 */
		logger.info(mlResult);
	}
	
	/**
	 * 5、获取并将全部预测上涨的股票输出到控制台
	 */
	public void getResult() {
		/*
		 * 最新全部算法结果List
		 */
		List<TabPriceDateMLResultFiveDays> currentAlgorithmResultList = mlResultMapper.selectCurrentList();
		
		/*
		 * 判断是否预测上涨
		 */
		for(TabPriceDateMLResultFiveDays currentAlgorithmResult : currentAlgorithmResultList) {
//			int flag = 0;
			double currentPirce = new Double(currentAlgorithmResult.getEndPriceCurrent());
//			double priceA = new Double(currentAlgorithmResult.getEndPriceA());
//			double priceB = new Double(currentAlgorithmResult.getEndPriceB());
//			double priceC = new Double(currentAlgorithmResult.getEndPriceC());
//			double priceD = new Double(currentAlgorithmResult.getEndPriceD());
			double priceE = new Double(currentAlgorithmResult.getEndPriceE());
			
			/*
			 * 重点关注股票
			 */
			if(currentPirce < 0.9 && priceE >= 0.95) {
				TabStockInfo currentStockInfo = tabStockInfoMapper.selectById(currentAlgorithmResult.getStockId());
				logger.info(currentAlgorithmResult.getEndPriceCurrent());
				logger.info(currentAlgorithmResult.getEndPriceA());
				logger.info(currentAlgorithmResult.getEndPriceB());
				logger.info(currentAlgorithmResult.getEndPriceC());
				logger.info(currentAlgorithmResult.getEndPriceD());
				logger.info(currentAlgorithmResult.getEndPriceE());
				logger.info("股票信息：" + currentStockInfo);
				logger.info("--------------------");
				continue;
			}
			
//			if(currentPirce < priceA) flag++;
//			if(currentPirce < priceB) flag++;
//			if(currentPirce < priceC) flag++;
//			if(currentPirce < priceD) flag++;
//			if(currentPirce < priceE) flag++;
			
//			if(flag >= NeuralNetworkConstants.ALGROITHM_RESULT_TRUE_FLAG) logger.info("可以看看这只股票：" + currentAlgorithmResult);
		}
	}
}
