package com.hanslv.stock.machine.learning.neural.network.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.machine.learning.constants.NeuralNetworkConstants;
import com.hanslv.stock.machine.learning.neural.network.DatePriceNNTrainer;
import com.hanslv.stock.machine.learning.repository.TabStockInfoRepository;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;

/**
 * 股票神经网络训练
 * 
 * -----------------------------------------
 * 1、训练全部股票日期-价格模型									public void trainAllStockNN()
 * 2、训练指定股票的日期-价格模型									public void trainAllStockNN(Integer stockId)
 * -----------------------------------------
 * @author hanslv
 *
 */
@Service
public class NeuralNetworkService {
	
	/*
	 * 时间-价格神经网络
	 */
	@Autowired
	private DatePriceNNTrainer datePriceNNTrainer;
	
	@Autowired
	private TabStockInfoRepository tabStockInfoMapper;
	
	
	
	/**
	 * 1、训练全部股票日期-价格模型
	 */
	public void trainAllStockNN() {
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
	public void trainAllStockNN(Integer stockId) {
		datePriceNNTrainer.trainNN(stockId , NeuralNetworkConstants.TRAIN_ERROR_LIMIT);
	}
	
	
	
	/**
	 * 3、预测全部股票
	 */
	public void calculateAllStock() {
		
	}
	
	/**
	 * 4、预测指定股票
	 * @param stockId
	 */
	public void calculateAllStock(Integer stockId) {
		
	}
}
