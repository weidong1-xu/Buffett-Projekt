package com.hanslv.stock.machine.learning.neural.network;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.encog.Encog;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.machine.learning.constants.NeuralNetworkConstants;
import com.hanslv.stock.machine.learning.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.machine.learning.util.DataUtil;

/**
 * 时间-价格神经网络训练器
 * @author hanslv
 *
 */
@Component
public class DatePriceNNTrainer {
	Logger logger = Logger.getLogger(DatePriceNNTrainer.class);
	
	/*
	 * 时间-价格神经网络模型
	 */
	@Autowired
	private DatePriceNN datePriceNN;
	
	@Autowired
	private TabStockPriceInfoRepository stockPriceInfoMapper;
	
	/**
	 * 训练模型
	 * 
	 * @param stockId 股票ID
	 * @param limit 精度
	 * @return
	 */
	public void trainNN(Integer stockId , double limit) {
		long start = System.currentTimeMillis();
		
		String algorithmFilePath = NeuralNetworkConstants.ALGORITHM_BASE_DIR + stockId + "_" + NeuralNetworkConstants.ALGORITHM_FILENAME_SUFFIX;
		
		logger.info("正在计算：stockId = " + stockId);
		
		/*
		 * 获取股票数据
		 */
		List<String> mainDataList = DataUtil.transPriceInfoToString(stockPriceInfoMapper.getTrainData(stockId , NeuralNetworkConstants.TRAIN_SIZE));
		
		
		/*
		 * 数据已经全部用完，没有找到合适模型
		 */
		if(mainDataList.size() < NeuralNetworkConstants.TRAIN_SIZE) {
			logger.error("数据样本集小于预期：stockId = " + stockId);
			return;
		}
	
		/*
		 * 算法训练数据
		 * 
		 * 2019-11-03更改，只标准化输出数据
		 */
//		Map<Map<String , NormalizedField> , MLDataSet> analyzedResult = DataUtil.dataAnalyze(mainDataList , NeuralNetworkConstants.TRAIN_DATA_TITLE.split(",") , 2 , 1 , 0);
//		Entry<Map<String , NormalizedField> , MLDataSet> analyzedResultEntry = analyzedResult.entrySet().iterator().next();
//		MLDataSet trainDataSet = analyzedResultEntry.getValue();
		
		MLDataSet trainDataSet = DataUtil.dataAnalyze(mainDataList , NeuralNetworkConstants.TRAIN_DATA_TITLE.split(",") , 2 , 1 , 0);
		
		/*
		 * 训练算法
		 */
		BasicNetwork algorithmModel = datePriceNN.train(trainDataSet , limit);
		
		long end = System.currentTimeMillis();
		
		/*
		 * 收敛失败
		 */
		if(algorithmModel == null) {
			Encog.getInstance().shutdown();
			logger.error("！！！！收敛失败，准备重新执行：stockId = " + stockId + ",耗时：" + (end - start)/1000 + "秒");
			
			/*
			 * 休眠1分钟，避免机器过热
			 */
			try {
				logger.info("稍等，我降降温");
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {}
			
			/*
			 * 重新执行
			 */
			trainNN(stockId , limit);
			return;
		}
			
		/*
		 * 预测成功保存算法到文件
		 */
		DataUtil.saveAlgorithm(algorithmFilePath , algorithmModel);
		logger.info("预测完成：stockId = " + stockId + ",耗时：" + (end - start)/1000 + "秒");
		Encog.getInstance().shutdown();
		
		
		/*
		 * 休眠1分钟，避免机器过热
		 */
		try {
			logger.info("稍等，我降降温");
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {}
	}
}
