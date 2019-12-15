package com.hanslv.maschinelles.lernen.neural.network;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jboss.logging.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.allgemein.dto.TabResult;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.repository.TabResultRepository;
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
	@Autowired
	private TabResultRepository resultMapper;
	
	/**
	 * 训练LSTM股票模型
	 * @param priceInfoList
	 * @param idInPlanTest
	 */
	public void train(Integer stockId , String endDate) {
		/*
		 * 数据准备
		 */
		int stepLong = NeuralNetworkConstants.trainDataSize + NeuralNetworkConstants.testDataSize;//总步长=训练数据步长+测试数据步长
		List<String> allDataList = dataUtil.dl4jDataFormatter(stockId , stepLong , endDate);
		if(allDataList.size() != stepLong) return;
		
		/*
		 * 拆分训练数据、测试数据
		 */
		List<String> trainDataList = new ArrayList<>();
		for(int i = 0 ; i < NeuralNetworkConstants.trainDataSize ; i++) trainDataList.add(allDataList.get(i));
		List<String> testDataList = new ArrayList<>();
		for(int i = NeuralNetworkConstants.trainDataSize ; i < allDataList.size() ; i++) testDataList.add(allDataList.get(i));
		
		/*
		 * 数据标准化
		 */
		List<DataSetIterator> iteratorList = dataUtil.dl4jDataNormalizer(trainDataList , testDataList , NeuralNetworkConstants.idealOutputSize);
		
		/*
		 * 获取LSTM
		 */
		MultiLayerNetwork lstmNetwork = DeepLearning4jStockNNBuilder.build();
		
		/*
		 * 训练模型
		 */
		for(int i = 0 ; i < NeuralNetworkConstants.epoch ; i++) {
			lstmNetwork.fit(iteratorList.get(0));
			lstmNetwork.rnnClearPreviousState();
		}
		
		/*
		 * 判断预测结果
		 */
		BigDecimal forecastResult = docheck(testDataList , lstmNetwork , iteratorList.get(1));
		/*
		 * 计算成功存入结果表
		 */
		if(forecastResult != null) {
			TabResult resultObj = new TabResult();
			resultObj.setStockId(stockId);
			resultObj.setStockId(stockId);
			resultObj.setSuggestRate(forecastResult.toString());
			resultMapper.insert(resultObj);
		}
	}
	
	/**
	 * 判断预测结果是否准确
	 * @return
	 */
	private BigDecimal docheck(List<String> testDataList , MultiLayerNetwork lstmNetwork , DataSetIterator forecastData) {
		/*
		 * 获取当前标准化器
		 */
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) forecastData.getPreProcessor();
		
		int trueCounter = 0;
		int testTotal = forecastData.numExamples() - 1;
		
		/*
		 * 首先执行训练步长-1次预测，并判断结果是否准确
		 */
		for(int i = 0 ; i < testTotal ; i++) {
			double result = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			double checkData = Double.parseDouble(testDataList.get(i).split(",")[1]);
			if(Math.abs(result - checkData) <= NeuralNetworkConstants.errorLimit) trueCounter++;
		}
		
		/*
		 * 前几次预测都准确则预测当前时间的价格
		 */
		if(trueCounter == testTotal) {
			double forecastResult = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			
			return new BigDecimal(forecastResult).divide(new BigDecimal(NeuralNetworkConstants.batchUnitLength * NeuralNetworkConstants.singleBatchSize) , 2 , BigDecimal.ROUND_HALF_UP);
		}
		return null;
	}

	/**
	 * 执行预测
	 * @param lstmNetwork
	 * @param input
	 * @param normalizerStandardize
	 * @return
	 */
	private double doForecast(MultiLayerNetwork lstmNetwork , DataSet input , NormalizerMinMaxScaler normalizerStandardize) {
		INDArray output = lstmNetwork.rnnTimeStep(input.getFeatures());
		DataSet resultDataSet = new DataSet(input.getFeatures() , output);
		/*
		 * 反标准化结果并判断是否符合标准
		 */
		normalizerStandardize.revert(resultDataSet);
		INDArray resultOutput = resultDataSet.getLabels();
		return resultOutput.getDouble(0);
	}
}
