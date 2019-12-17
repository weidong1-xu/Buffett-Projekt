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
import com.hanslv.maschinelles.lernen.repository.TabStockPriceInfoRepository;
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
	@Autowired
	private TabStockPriceInfoRepository priceInfoMapper;
	
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
		List<String> allDataList = dataUtil.getRectangleArea(stockId , stepLong , endDate , NeuralNetworkConstants.batchUnitLength , NeuralNetworkConstants.singleBatchSize);
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
		 * 判断模型是否准确，并执行预测
		 */
		checkAndForecast(testDataList , lstmNetwork , iteratorList.get(1) , stockId , endDate);
	}
	
	/**
	 * 判断模型是否准确，并执行预测
	 * @return
	 */
	private void checkAndForecast(List<String> testDataList , MultiLayerNetwork lstmNetwork , DataSetIterator forecastData , Integer stockId , String endDate) {
		/*
		 * 2019-12-17修改，在执行预测前先判断当前日期均线斜率是否为正
		 */
		if(dataUtil.getAverageSlope(stockId , endDate , NeuralNetworkConstants.averageType).compareTo(BigDecimal.ZERO) < 0) return;
		
		/*
		 * 获取当前标准化器
		 */
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) forecastData.getPreProcessor();
		
		int trueCounter = 0;
		int testTotal = forecastData.numExamples() - 1;
		
		double checkDataBuffer = 0;
		
		/*
		 * 首先执行训练步长-1次预测，并判断结果是否准确
		 */
		for(int i = 0 ; i < testTotal ; i++) {
			double result = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			checkDataBuffer = Double.parseDouble(testDataList.get(i).split(",")[1]);
			if(Math.abs(result - checkDataBuffer) <= NeuralNetworkConstants.errorLimit) trueCounter++;
		}
		
		/*
		 * 前几次预测都准确则预测当前时间的价格
		 */
		if(trueCounter == testTotal) {
			double forecastResult = doForecast(lstmNetwork , forecastData.next(1) , normalizerStandardize);
			
			/*
			 * 2019-12-17修改，只保留预测矩形面积小于等于当前矩形面积的预测结果
			 */
			if(forecastResult <= checkDataBuffer) {
				/*
				 * 2019-12-17修改，筛选当前价格接近当前矩形最低价的结果
				 * 首先获取endDate之前的singleBatchSize * (batchSize - 1)个数据的最低价smallBatchLow
				 * 获取当前的价格currentStockPrice
				 * 判断(currentStockPrice-smallBatchLow)/currentStockPrice <= buyErrorLimit
				 */
				BigDecimal smallBatchLow = new BigDecimal(dataUtil.getRectangleMaxAndLow(stockId , 1 , endDate , NeuralNetworkConstants.batchUnitLength - 1 , NeuralNetworkConstants.singleBatchSize).get(0).split(",")[1]);
				BigDecimal currentStockPrice = priceInfoMapper.getTrainAndTestDataDL4j(stockId , 1 , endDate).get(0).getStockPriceEndPrice();
				if(currentStockPrice.subtract(smallBatchLow).divide(currentStockPrice , 3 , BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(NeuralNetworkConstants.buyErrorLimit)) <= 0) {
					/*
					 * 插入结果
					 */
					TabResult result = new TabResult();
					result.setStockId(stockId);
					result.setDate(endDate);
					result.setSuggestBuyPrice(currentStockPrice.toString());
					resultMapper.insert(result);
					logger.info("找到合适股票：" + stockId + "，日期：" + endDate + "，建议买入价格：" + currentStockPrice);
				}
			}
		}
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
