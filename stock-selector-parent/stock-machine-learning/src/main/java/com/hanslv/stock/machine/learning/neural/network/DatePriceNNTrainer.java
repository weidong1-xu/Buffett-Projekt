package com.hanslv.stock.machine.learning.neural.network;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.Encog;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.arrayutil.NormalizedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.machine.learning.constants.NeuralNetWorkConstants;
import com.hanslv.stock.machine.learning.util.DataUtil;

/**
 * 时间-价格神经网络训练器
 * @author hanslv
 *
 */
@Component
public class DatePriceNNTrainer {
	
	/*
	 * 时间-价格神经网络模型
	 */
	@Autowired
	private DatePriceNN datePriceNN;
	
	/**
	 * 训练模型
	 * 
	 * @param stockId 股票ID
	 * @param startDate 起始时间
	 * @param limit 精度
	 * @return
	 */
	public void trainNN(String stockId , String startDate , double limit , String titles) {
		String algorithmFilePath = NeuralNetWorkConstants.ALGORITHM_BASE_DIR + stockId + "_" + NeuralNetWorkConstants.ALGORITHM_FILENAME_SUFFIX;
		
		/*
		 * 获取股票数据
		 */
//		List<String> mainDataList = DbUtil.getDataAndVolumeMap(stockId, startDate, trainDataSize + checkDataSize);
		List<String> mainDataList = null;
			
		/*
		 * 数据已经全部用完，没有找到合适模型
		 */
		if(mainDataList.size() < NeuralNetWorkConstants.TRAIN_SIZE) {
			System.err.println("数据样本集小于预期");
			return;
		}
	
		/*
		 * 算法训练数据
		 */
		Map<Map<String , NormalizedField> , MLDataSet> analyzedResult = DataUtil.dataAnalyze(mainDataList , titles.split(",") , 2 , 1 , 0);
		Entry<Map<String , NormalizedField> , MLDataSet> analyzedResultEntry = analyzedResult.entrySet().iterator().next();
		MLDataSet trainDataSet = analyzedResultEntry.getValue();
		
		/*
		 * 训练算法
		 */
		BasicNetwork algorithmModel = datePriceNN.train(trainDataSet , limit);
			
		/*
		 * 收敛失败
		 */
		if(algorithmModel == null) {
			Encog.getInstance().shutdown();
			/*
			 * 重新执行
			 */
			trainNN(stockId , startDate , limit , titles);
			return;
		}
			
		/*
		 * 预测成功保存算法到文件
		 */
		DataUtil.saveAlgorithm(algorithmFilePath , algorithmModel);
		System.out.println("预测完成！" + stockId);
		Encog.getInstance().shutdown();
	}
}
