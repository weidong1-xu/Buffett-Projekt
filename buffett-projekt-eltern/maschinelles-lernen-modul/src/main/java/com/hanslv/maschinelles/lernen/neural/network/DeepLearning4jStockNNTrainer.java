package com.hanslv.maschinelles.lernen.neural.network;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jboss.logging.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.repository.TabStockPriceInfoRepository;
import com.hanslv.maschinelles.lernen.util.DataUtil;

/**
 * 训练DeepLearning4j构建的股票神经网络
 * @author hanslv
 *
 */
@Component
public class DeepLearning4jStockNNTrainer {
	@Autowired
	private TabStockPriceInfoRepository stockPriceInfoMapper;
	
	Logger logger = Logger.getLogger(DeepLearning4jStockNNTrainer.class);
	
	/**
	 * 训练LSTM股票模型
	 * @param stockId 股票ID
	 * @param trainDataSize 训练数据长度
	 * @param inputSize 输入神经元数量
	 * @param idealOutputSize 输出神经元数量
	 * @param epoch 训练纪元
	 * @param trainEndDate 训练结束日期
	 */
	public Map<Boolean , double[]> train(Integer stockId , LocalDate trainEndDate , boolean isInPlanTest) {
		/*
		 * 2019-12-11日修改Bug，训练传入的数据源不准确
		 */
		String trainEndDateStr = trainEndDate.toString();
		if(isInPlanTest) trainEndDateStr = trainEndDate.plusDays(7).toString();
		
		/*
		 * 结果
		 */
		Map<Boolean , double[]> resultMap = new HashMap<>();
		resultMap.put(false , null);
		
		/*
		 * 获取计算需要的数据
		 */
		/*
		 * 2019-12-11日修改Bug，训练传入的数据源不准确
		 */
		List<String> mainDataList = null;
		if(isInPlanTest)
			mainDataList = DataUtil.dl4jDataFormatterNew(
				stockPriceInfoMapper.getTrainAndTestDataDL4j(
						stockId , (NeuralNetworkConstants.trainDataSize + NeuralNetworkConstants.testDataSize) * NeuralNetworkConstants.singleBatchSize + NeuralNetworkConstants.singleBatchSize , trainEndDateStr) , isInPlanTest);
		else
			mainDataList = DataUtil.dl4jDataFormatterNew(
					stockPriceInfoMapper.getTrainAndTestDataDL4j(
							stockId , (NeuralNetworkConstants.trainDataSize + NeuralNetworkConstants.testDataSize) * NeuralNetworkConstants.singleBatchSize , trainEndDateStr) , isInPlanTest);
		
		/*
		 * 判断数据量是否符合标准
		 */
		if(mainDataList.size() != (NeuralNetworkConstants.trainDataSize + NeuralNetworkConstants.testDataSize) * NeuralNetworkConstants.singleBatchSize) return resultMap;
		
		/*
		 * 拆分训练数据和测试数据
		 */
		List<String> trainDataList = mainDataList.subList(
				0 , NeuralNetworkConstants.trainDataSize * NeuralNetworkConstants.singleBatchSize);
		List<String> testDataList = mainDataList.subList(
				NeuralNetworkConstants.trainDataSize * NeuralNetworkConstants.singleBatchSize , mainDataList.size());
		
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
		
		/*
		 * 验证数据是否符合评判标准
		 */
		Map<Boolean , INDArray> forcastResultMap = checkData(lstmNetwork , iteratorList.get(1));
		Entry<Boolean , INDArray> result = forcastResultMap.entrySet().iterator().next();
		
		/*
		 * 获取符合要求的股票
		 */
		if(result.getKey()) {
			/*
			 * 将预测结果数据与当前价格比较
			 */
			Double currentPrice = new Double(trainDataList.get(trainDataList.size() - 1).split(",")[2]);
			double[] forcastResult = getMaxAndLow(result.getValue());

			/*
			 * 判断当前价格是否不符合预测
			 * 预测最高价小于等于当前价格，预测最低价大于等于当前价格
			 */
			if(new Double(forcastResult[0]).compareTo(currentPrice) <= 0 || new Double(forcastResult[1]).compareTo(currentPrice) >= 0) return resultMap;
			
			/*
			 * 是否为初步筛选
			 */
			if(isInPlanTest) getInPlanStocks(forcastResult , testDataList);
			resultMap.clear();
			resultMap.put(true , forcastResult);
			return resultMap;
		}
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
	 * 验证数据是否符合评判标准，
	 * 当获取到的多次最高价和最低价比较相近时则为符合标准
	 * @param lstmNetwork
	 * @param forecastData
	 * @return 返回是否符合标准，预测输出数据
	 */
	private Map<Boolean , INDArray> checkData(MultiLayerNetwork lstmNetwork , DataSetIterator forecastData) {
		/*
		 * 结果Map
		 */
		Map<Boolean , INDArray> resultMap = new HashMap<>();
		
		/*
		 * 获取当前标准化器
		 */
		NormalizerMinMaxScaler normalizerStandardize = (NormalizerMinMaxScaler) forecastData.getPreProcessor();
		
		/*
		 * 执行预测
		 */
		DataSet input = forecastData.next();
		INDArray output = lstmNetwork.rnnTimeStep(input.getFeatures());
		DataSet resultDataSet = new DataSet(input.getFeatures() , output);
		
		/*
		 * 反标准化结果并判断是否符合标准
		 */
		normalizerStandardize.revert(resultDataSet);
		INDArray resultOutput = resultDataSet.getLabels();
		resultMap.put(doCheckData(resultOutput) , resultOutput);
		return resultMap;
	}
	/**
	 * 判断当前反标准化结果集是否符合标准
	 * @param unNormalizerOutput
	 * @return
	 */
	private boolean doCheckData(INDArray resultOutput) {
		Set<BigDecimal> setA = new HashSet<>();
		Set<BigDecimal> setB = new HashSet<>();
		for(int i = 0 ; i < NeuralNetworkConstants.singleBatchSize ; i ++) {
			for(int j = 0 ; j < NeuralNetworkConstants.idealOutputSize ; j++) {
				if(j == 0) setA.add(new BigDecimal(resultOutput.getDouble(i , j)).setScale(1 , BigDecimal.ROUND_HALF_UP));
				else setB.add(new BigDecimal(resultOutput.getDouble(i , j)).setScale(1 , BigDecimal.ROUND_HALF_UP));
			}
		}
		return (setA.size() > 1 || setB.size() > 1) ? false : true;
	}
	
	/**
	 * 获取当前INDArray的最大值最小值
	 * @param output
	 * @return
	 */
	private double[] getMaxAndLow(INDArray output) {
		double[] maxAndLow = new double[2];
		double testMaxBuffer = 0;
		for(int i = 0 ; i < NeuralNetworkConstants.singleBatchSize ; i++) {
			if(testMaxBuffer != 0) {
				if(output.getDouble(i , 0) > testMaxBuffer) testMaxBuffer = output.getDouble(i , 0);
			}else testMaxBuffer = output.getDouble(i , 0);
		}
		double testMinBuffer = 0;
		for(int i = 0 ; i < NeuralNetworkConstants.singleBatchSize ; i++) {
			if(testMinBuffer != 0) {
				if(output.getDouble(i , 1) < testMinBuffer) testMinBuffer = output.getDouble(i , 1);
			}else testMinBuffer = output.getDouble(i , 1);
		}
		maxAndLow[0] = testMaxBuffer;
		maxAndLow[1] = testMinBuffer;
		return maxAndLow;
	}
}
