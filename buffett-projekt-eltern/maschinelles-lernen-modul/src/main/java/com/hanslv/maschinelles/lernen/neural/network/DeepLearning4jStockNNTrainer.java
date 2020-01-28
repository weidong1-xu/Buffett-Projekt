package com.hanslv.maschinelles.lernen.neural.network;

import java.math.BigDecimal;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jboss.logging.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.allgemein.dto.TabResult;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.util.DataUtil;

/**
 * 训练DeepLearning4j构建的股票神经网络
 * @author hanslv
 *
 */
@Component
public class DeepLearning4jStockNNTrainer {
	Logger logger = Logger.getLogger(DeepLearning4jStockNNTrainer.class);
	
	@Autowired
	private DataUtil dataUtil;
	
	/**
	 * 训练LSTM股票模型
	 * @param priceInfoList
	 * @param idInPlanTest
	 */
	public TabResult train(Integer stockId , String endDate) {
		DataSetIterator[] dataSetIterators = dataUtil.getSourceData(stockId , endDate);
		
		if(dataSetIterators == null) return null;
		
		/*
		 * 数据归一化处理
		 */
		DataNormalization normalize = dataUtil.normalize(dataSetIterators);
		
		/*
		 * 获取LSTM神经网络
		 */
		MultiLayerNetwork lstmNetwork = DeepLearning4jStockNNBuilder.build();
		
		DataSetIterator trainDataSetIterator = dataSetIterators[0];
		DataSetIterator forcastDataSetIterator = dataSetIterators[1];
		
		/*
		 * 拟合模型，并获取最佳结果
		 */
		Evaluation eval = new Evaluation(NeuralNetworkConstants.idealOutputSize);
		TabResult result = new TabResult();
		result.setStockId(stockId);
		result.setDate(endDate);
		for(int i = 0 ; i < NeuralNetworkConstants.epoch ; i++) {
			while(trainDataSetIterator.hasNext()) {
				DataSet trainDataSet = trainDataSetIterator.next();
				lstmNetwork.fit(trainDataSet);
			}
			
			/*
			 * 记录当前迭代纪元信息
			 */
			checkForcast(lstmNetwork , forcastDataSetIterator , eval , normalize , result);
			
			/*
			 * 将Iterator复位，准备进入下一纪元
			 */
			trainDataSetIterator.reset();
			forcastDataSetIterator.reset();
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 对当前纪元进行评估并记录最佳结果
	 * @param lstmNetwork
	 * @param forcastDataSetIterator
	 * @param eval
	 * @param normalize
	 * @param result
	 */
	private void checkForcast(MultiLayerNetwork lstmNetwork , DataSetIterator forcastDataSetIterator , Evaluation eval , DataNormalization normalize , TabResult result) {
		/*
		 * 使用部分数据对当前模型进行评估
		 */
		int counter = 0;
		while(forcastDataSetIterator.hasNext()) {
			counter++;
			DataSet testDataSet = forcastDataSetIterator.next();
			INDArray testInput = testDataSet.getFeatures();
			INDArray testOutput = testDataSet.getLabels();
			INDArray checkOutput = lstmNetwork.output(testInput , false);
			eval.evalTimeSeries(testOutput , checkOutput);
			if(counter == NeuralNetworkConstants.forcastDataSize - 1) break;
		}

		/*
		 * 使用当前时间步长数据预测下一步长结果
		 */
		DataSet testDataSet = forcastDataSetIterator.next();
		INDArray testInput = testDataSet.getFeatures();
		INDArray checkOutput = lstmNetwork.output(testInput , false);
		DataSet checkDataSet = new DataSet(testInput , checkOutput);
		normalize.revert(checkDataSet);
		INDArray revertedForcastResult = checkDataSet.getLabels();
		double[] forcastResultArray = revertedForcastResult.data().asDouble();
		
		/*
		 * 当前评估结果
		 */
		BigDecimal currentAccuracy = result.getAccuracy();
		BigDecimal accuracy = new BigDecimal(String.valueOf(eval.accuracy())).setScale(4 , BigDecimal.ROUND_HALF_UP);
		BigDecimal currentPrecision = result.getAccuracy();
		BigDecimal precision = new BigDecimal(String.valueOf(eval.precision())).setScale(4 , BigDecimal.ROUND_HALF_UP);
		BigDecimal currentRecall = result.getAccuracy();
		BigDecimal recall = new BigDecimal(String.valueOf(eval.recall())).setScale(4 , BigDecimal.ROUND_HALF_UP);
		BigDecimal currentF1 = result.getAccuracy();
		BigDecimal f1 = new BigDecimal(String.valueOf(eval.f1())).setScale(4 , BigDecimal.ROUND_HALF_UP);
		
		if(
				accuracy.compareTo(currentAccuracy) >= 0
				&&
				precision.compareTo(currentPrecision) >= 0
				&&
				recall.compareTo(currentRecall) >= 0
				&&
				f1.compareTo(currentF1) >= 0
			) {
			result.setAccuracy(accuracy);
			result.setPrecisions(precision);
			result.setRecall(recall);
			result.setF1(f1);
			result.setForcastMax(new BigDecimal(String.valueOf(forcastResultArray[0])).setScale(2 , BigDecimal.ROUND_HALF_UP));
			result.setForcastMin(new BigDecimal(String.valueOf(forcastResultArray[1])).setScale(2 , BigDecimal.ROUND_HALF_UP));
		}
	}
}
