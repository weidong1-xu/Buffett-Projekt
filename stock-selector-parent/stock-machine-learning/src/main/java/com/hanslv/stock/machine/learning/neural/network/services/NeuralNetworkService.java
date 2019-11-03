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
 * 1、训练全部股票日期-价格模型									public void trainStockNN()
 * 2、训练指定股票的日期-价格模型									public void trainStockNN(Integer stockId)
 * 3、预测全部股票												public void calculateStock()
 * 4、预测指定股票												public void calculateStock(Integer stockId)
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
	 * 1、训练全部股票日期-价格模型
	 */
	public void trainStockNN() {
		/*
		 * 获取全部股票基本信息
		 */
		List<TabStockInfo> stockInfoList = tabStockInfoMapper.selectAllStockInfo();
		
		/*
		 * 遍历全部股票信息并运行算法，算法将会被储存到NeuralNetworkConstants.ALGORITHM_BASE_DIR文件夹
		 */
		for(TabStockInfo stockInfo : stockInfoList) datePriceNNTrainer.trainNN(stockInfo.getStockId() , NeuralNetworkConstants.TRAIN_ERROR_LIMIT);
		
	}
	
	
	
	/**
	 * 2、训练指定股票的日期-价格模型
	 */
	public void trainStockNN(Integer stockId) {
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
			mlResultList.add(mlResult);
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
}
