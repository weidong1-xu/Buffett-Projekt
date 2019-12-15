package com.hanslv.maschinelles.lernen.neural.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jboss.logging.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.springframework.stereotype.Component;

import com.hanslv.allgemein.dto.TabStockPriceInfo;
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
	
	/**
	 * 训练LSTM股票模型
	 * @param priceInfoList
	 * @param idInPlanTest
	 */
	public Map<Boolean , double[]> train(List<TabStockPriceInfo> priceInfoList, boolean isInPlanTest) {
		/*
		 * 结果
		 */
		Map<Boolean , double[]> resultMap = new HashMap<>();
		resultMap.put(false , null);
		
		/*
		 * 获取计算需要的数据
		 */
		List<String> mainDataList = DataUtil.dl4jDataFormatterNew(priceInfoList , isInPlanTest);
		
		/*
		 * 拆分训练数据和测试数据
		 */
		List<String> trainDataList = new ArrayList<>();
		for(int i = 0 ; i < NeuralNetworkConstants.trainDataSize * NeuralNetworkConstants.singleBatchSize ; i++) trainDataList.add(mainDataList.get(i));
		
		List<String> testDataList = new ArrayList<>();
		for(int i = NeuralNetworkConstants.trainDataSize * NeuralNetworkConstants.singleBatchSize ; i < mainDataList.size() ; i++) testDataList.add(mainDataList.get(i));
		
		/*
		 * 标准化训练数据集、测试数据集
		 */
		List<DataSetIterator> iteratorList = DataUtil.dl4jDataNormalizer(trainDataList , testDataList, NeuralNetworkConstants.idealOutputSize);
		
		/*
		 * 获取神经网络模型
		 */
		MultiLayerNetwork lstmNetwork = DeepLearning4jStockNNBuilder.build();
		
		/*
		 * 训练模型epoch次
		 */
		for(int i = 0 ; i < NeuralNetworkConstants.epoch ; i++) {
			lstmNetwork.fit(iteratorList.get(0));
			lstmNetwork.rnnClearPreviousState();
		}
		
		double[] middleHighAndLow = getMiddleData(lstmNetwork , iteratorList.get(1));
		/*
		 * 将预测结果数据与当前价格比较
		 */
		Double currentPrice = new Double(trainDataList.get(trainDataList.size() - 1).split(",")[2]);
		if(new Double(middleHighAndLow[0]).compareTo(currentPrice) <= 0 || new Double(middleHighAndLow[1]).compareTo(currentPrice) >= 0) return resultMap;
		/*
		 * 是否为初步筛选
		 */
		if(isInPlanTest) getInPlanStocks(middleHighAndLow , testDataList);
		resultMap.clear();
		resultMap.put(true , middleHighAndLow);
		return resultMap;
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static enum Result{
		TRUE,FALSE,EXCLUDE
	}
	
	/**
	  * 比较实际是否包含于预测输出
	  * @param testOutput
	  * @param idealOutput
	  * @return
	  */
	private Result compareIdealOutputAndtest(double[] forcastOutput , List<String> idealOutput) {
		double[] idealMaxAndLow = new double[2];
		double idealMaxBuffer = 0;
		double idealMinBuffer = 0;
		for(String ideal : idealOutput) {
			String[] idealArray = ideal.split(",");
			String idealMaxStr = idealArray[idealArray.length - 2];
			String idealMinStr = idealArray[idealArray.length - 1];
			if(idealMaxBuffer != 0) {
				if(new Double(idealMaxStr).compareTo(idealMaxBuffer) > 0) idealMaxBuffer = new Double(idealMaxStr);
			}else idealMaxBuffer = new Double(idealMaxStr);
			   
			if(idealMinBuffer != 0) {
				if(new Double(idealMinStr).compareTo(idealMinBuffer) < 0) idealMinBuffer = new Double(idealMinStr);
			}else idealMinBuffer = new Double(idealMinStr);
		}
		idealMaxAndLow[0] = idealMaxBuffer;
		idealMaxAndLow[1] = idealMinBuffer;
		
		if(forcastOutput[0] * (1-NeuralNetworkConstants.errorLimit) <= forcastOutput[1]) return Result.EXCLUDE;
		if(forcastOutput[1] * (1+NeuralNetworkConstants.errorLimit) >= forcastOutput[0]) return Result.EXCLUDE;
		return (idealMaxAndLow[0] >= forcastOutput[0] * (1 - NeuralNetworkConstants.errorLimit) && idealMaxAndLow[1] <= forcastOutput[1] * (1 + NeuralNetworkConstants.errorLimit)) ? Result.TRUE : Result.FALSE;
	}
	
	/**
	 * 初步模拟测试
	 * @param forcastResult 模拟预测结果
	 * @param testDataList 真实数据
	 */
	private void getInPlanStocks(double[] forcastResult , List<String> testDataList) {
		NeuralNetworkConstants.inPlanMainCounter++;
		
		/*
		 * 比较真实最高价、最低价与预测结果是否相符
		 */
		Result checkResult = compareIdealOutputAndtest(forcastResult , testDataList);
		if(Result.TRUE == checkResult) NeuralNetworkConstants.inPlanGoalCounter++;//预测结果准确
		else if(Result.EXCLUDE == checkResult) NeuralNetworkConstants.inPlanMainCounter--;//预测结果应排除
	}
	/**
	 * 获取执行结果的中位数
	 * @param lstmNetwork
	 * @param forecastData
	 * @return
	 */
	private double[] getMiddleData(MultiLayerNetwork lstmNetwork , DataSetIterator forecastData) {
		/*
		 * 获取当前标准化器
		 */
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) forecastData.getPreProcessor();
		
		/*
		 * 执行预测
		 */
		DataSet input = forecastData.next();
//		forecastData.reset();
//		INDArray output = lstmNetwork.output(forecastData);
		INDArray output = lstmNetwork.rnnTimeStep(input.getFeatures());
		DataSet resultDataSet = new DataSet(input.getFeatures() , output);
		
		/*
		 * 反标准化结果并判断是否符合标准
		 */
		normalizerStandardize.revert(resultDataSet);
		INDArray resultOutput = resultDataSet.getLabels();
		
		return doGetMiddleData(resultOutput);
	}
	
	/**
	 * 获取中位数的方法
	 * @param resultOutput
	 * @return
	 */
	private double[] doGetMiddleData(INDArray resultOutput) {
		List<Double> highList = new ArrayList<>();
		List<Double> lowList = new ArrayList<>();
		for(int i = 0 ; i < NeuralNetworkConstants.singleBatchSize ; i ++) {
			for(int j = 0 ; j < NeuralNetworkConstants.idealOutputSize ; j++) {
				if(j == 0) highList.add(resultOutput.getDouble(i , j));
				else lowList.add(resultOutput.getDouble(i , j));
			}
		}
		
//		for(int i = 0 ; i < NeuralNetworkConstants.singleBatchSize ; i ++) {
//			highList.add(resultOutput.getDouble(i));
//			lowList.add(resultOutput.getDouble(i + NeuralNetworkConstants.singleBatchSize));
//		}
		Collections.sort(highList);
		Collections.sort(lowList);
		
		return new double[]{highList.get(NeuralNetworkConstants.singleBatchSize / 2) , lowList.get(NeuralNetworkConstants.singleBatchSize / 2)};
	}
}
