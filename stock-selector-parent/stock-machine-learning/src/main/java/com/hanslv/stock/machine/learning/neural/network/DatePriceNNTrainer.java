package com.hanslv.stock.machine.learning.neural.network;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.arrayutil.NormalizedField;

import com.hanslv.stock.machine.learning.constants.NeuralNetWorkConstants;
import com.hanslv.stock.machine.learning.util.DataUtil;

/**
 * 时间-价格神经网络训练器
 * @author hanslv
 *
 */
public class DatePriceNNTrainer {
	/**
	 * 训练模型
	 * 
	 * @param stockId 股票ID
	 * @param startDate 起始时间
	 * @param checkDataSize 校验数据大小
	 * @param limit 精度
	 * @return
	 */
	public static boolean trainNN(String stockId , String startDate , int checkDataSize , double limit , double checkLimit , String titles) {
		String algorithmFilePath = NeuralNetWorkConstants.ALGORITHM_BASE_DIR + stockId + "_" + NeuralNetWorkConstants.ALGORITHM_FILENAME_SUFFIX;
		
		/*
		 * 初始训练时间为365天，每次迭代增加50天直到当前ID的全部数据用完或找出最佳模型
		 */
		loop1:for(int trainDataSize = 70; trainDataSize < Integer.MAX_VALUE ; trainDataSize = trainDataSize + 50) {
			
			
			/*
			 * 获取股票数据
			 */
//			List<String> mainDataList = DbUtil.getDataAndVolumeMap(stockId, startDate, trainDataSize + checkDataSize);
			List<String> mainDataList = null;
			
			/*
			 * 数据已经全部用完，没有找到合适模型
			 */
			if(mainDataList.size() < trainDataSize + checkDataSize) {
				System.err.println("数据样本集小于预期");
				return false;
			}
	
			/*
			 * 算法训练数据
			 */
			Map<Map<String , NormalizedField> , MLDataSet> analyzedResult = DataUtil.dataAnalyze(mainDataList , titles.split(",") , 2 , 1 , 0);
			Entry<Map<String , NormalizedField> , MLDataSet> analyzedResultEntry = analyzedResult.entrySet().iterator().next();
			Map<String , NormalizedField> deNormalizedMap = analyzedResultEntry.getKey();
			MLDataSet mainData = analyzedResultEntry.getValue();
			MLDataSet trainData = new BasicMLDataSet();
			MLDataSet checkData = new BasicMLDataSet();
			
			/*
			 * 将样本拆分为训练数据和对比数据
			 */
			for(int i = 0 ; i < mainData.size() ; i++) {
				if(i < trainDataSize) trainData.add(mainData.get(i));
				else checkData.add(mainData.get(i));
			}
			
			/*
			 * 训练算法
			 */
			BasicNetwork algorithmModel = DatePriceNN.train(trainData , limit);
			
			/*
			 * 收敛失败
			 */
			if(algorithmModel == null) {
				Encog.getInstance().shutdown();
				continue;
			}
			
			/*
			 * 判断结果是否符合预期
			 */
			for(MLDataPair checkDataPair : checkData) {
				BasicMLData checkInput = new BasicMLData(checkDataPair.getInput());
				BasicMLData checkOutput = new BasicMLData(checkDataPair.getIdeal());
				
				/*
				 * 执行算法
				 */
				BasicMLData output = new BasicMLData(algorithmModel.compute(checkInput));
				
//				NormalizedField startPricedeNormalizer = deNormalizedMap.get("stockPricestart");
				NormalizedField endPriceNormalizer = deNormalizedMap.get("stockPriceend");
				System.out.println("--------------------------");
				System.out.println("实际：" + endPriceNormalizer.deNormalize(checkOutput.getData(1)));
				System.out.println("预测：" + endPriceNormalizer.deNormalize(output.getData(1)));
				
//				/*
//				 * 预测失败，增加天数后重新计算
//				 */
//				if(SourceDataParser.check(checkOutput , output , checkLimit)) {
//					Encog.getInstance().shutdown();
//					System.err.println("-----预测失败");
//					continue loop1;
//				}
			}
			
			/*
			 * 预测成功保存算法到文件
			 */
//			SourceDataParser.saveAlgorithm(algorithmFilePath , algorithmModel);
//			System.out.println("预测成功！" + stockId);
//			Encog.getInstance().shutdown();
			return true;
		}
		return false;
	}
}
