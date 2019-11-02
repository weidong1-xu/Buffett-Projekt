package com.hanslv.stock.machine.learning.neural.network.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.machine.learning.constants.NeuralNetWorkConstants;
import com.hanslv.stock.machine.learning.neural.network.DatePriceNNTrainer;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;

@Service
public class NeuralNetworkService {
	
	/*
	 * 时间-价格神经网络
	 */
	@Autowired
	private DatePriceNNTrainer datePriceNNTrainer;
	
	/**
	 * 训练全部股票日期-价格模型
	 */
	public void trainAllStockNN() {
		/*
		 * 获取全部股票基本信息
		 */
		List<TabStockInfo> stockInfoList = null;
		
		/*
		 * 遍历全部股票信息并运行算法
		 */
		for(TabStockInfo stockInfo : stockInfoList) {
			datePriceNNTrainer.trainNN(stockInfo.getStockId() , startDate, NeuralNetWorkConstants.TRAIN_ERROR_LIMIT , titles);
		}
		
	}
	
	/**
	 * 预测指定股票
	 */
	public void calculateAllStock(Integer stockId) {
		
	}
}
