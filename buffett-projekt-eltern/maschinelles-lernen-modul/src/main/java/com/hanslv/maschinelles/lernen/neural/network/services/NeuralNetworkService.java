package com.hanslv.maschinelles.lernen.neural.network.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.allgemein.dto.TabStockInfo;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.neural.network.DeepLearning4jStockNNTrainer;
import com.hanslv.maschinelles.lernen.repository.TabStockInfoRepository;

/**
 * 股票神经网络训练
 * 
 * -----------------------------------------
 * 1、dl4j从指定ID开始训练全部股票日期-价格模型					public void dl4jTrainStockNN(Integer stockId)
 * -----------------------------------------
 * @author hanslv
 *
 */
@Service
public class NeuralNetworkService {
	Logger logger = Logger.getLogger(NeuralNetworkService.class);
	
	@Autowired
	private DeepLearning4jStockNNTrainer dl4jStockNNTrainer;
	
	@Autowired
	private TabStockInfoRepository tabStockInfoMapper;
	
	/**
	 * 1、dl4j从指定ID开始训练全部股票日期-价格模型
	 * @param stockId
	 */
	public void dl4jTrainStockNN(Integer stockId) {
		/*
		 * 获取全部股票基本信息
		 */
		List<TabStockInfo> stockInfoList = tabStockInfoMapper.selectAllStockInfo();
		
		/*
		 * 遍历全部股票信息并运行算法
		 */
		for(TabStockInfo stockInfo : stockInfoList) {
			if(stockInfo.getStockId().compareTo(stockId) < 0) continue;
			dl4jStockNNTrainer.train(stockInfo.getStockId() + "" , NeuralNetworkConstants.DL4J_TRAIN_SIZE , 3 , 2 , NeuralNetworkConstants.DL4J_MAX_EPOCH);
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
