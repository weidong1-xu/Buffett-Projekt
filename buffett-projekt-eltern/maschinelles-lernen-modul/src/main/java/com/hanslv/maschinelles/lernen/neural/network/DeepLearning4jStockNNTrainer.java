package com.hanslv.maschinelles.lernen.neural.network;

import java.math.BigDecimal;
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

import com.hanslv.maschinelles.lernen.repository.TabStockInfoRepository;
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
	
	@Autowired
	private TabStockInfoRepository stockInfoMapper;
	
	Logger logger = Logger.getLogger(DeepLearning4jStockNNTrainer.class);
	
	
	/**
	 * 训练LSTM股票模型
	 * @param stockId 股票ID
	 * @param trainDataSize 训练数据长度
	 * @param inputSize 输入神经元数量
	 * @param idealOutputSize 输出神经元数量
	 * @param epoch 训练纪元
	 */
	public void train(String stockId , int trainDataSize , int inputSize ,  int idealOutputSize , int epoch) {
		/*
		 * 获取训练数据和预测下一周信息的输入数据
		 */
		List<String> mainDataList = DataUtil.dl4jDataFormatterNew(stockPriceInfoMapper.getTrainAndTestDataDL4j(stockId , (trainDataSize + 1) * 5));
		
		/*
		 * 判断数据量是否符合标准
		 */
		if(mainDataList.size() < (trainDataSize + 1)) return;
		
		List<String> trainDataList = mainDataList.subList(0 , trainDataSize * 5);
		List<String> forecastDataList = mainDataList.subList(trainDataSize * 5 , mainDataList.size());
		
		/*
		 * 标准化训练数据集、测试数据集
		 */
		List<DataSetIterator> iteratorList = DataUtil.dl4jDataNormalizer(trainDataList , forecastDataList, idealOutputSize);
		
		/*
		 * 获取神经网络模型
		 */
		MultiLayerNetwork lstmNetwork = DeepLearning4jStockNNBuilder.build(inputSize , idealOutputSize);
		
		/*
		 * 训练模型epoch次
		 */
		for(int i = 0 ; i < epoch ; i++) {
			lstmNetwork.fit(iteratorList.get(0));
			lstmNetwork.rnnClearPreviousState();
		}
		
		Map<Boolean , INDArray> resultMap = checkData(lstmNetwork , iteratorList.get(1));
		Entry<Boolean , INDArray> result = resultMap.entrySet().iterator().next();
		
		/*
		 * 获取符合要求的股票
		 */
		if(result.getKey()) {
			double[] resultMaxAndLow = getMaxAndLow(result.getValue());
			
			BigDecimal diff = new BigDecimal(resultMaxAndLow[0] - resultMaxAndLow[1]);//最高价最低价差异
			
			if(diff.divide(new BigDecimal(resultMaxAndLow[1]) , 2 , BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(0.03)) < 0) return;
			
			String stockCode = stockInfoMapper.selectById(new Integer(stockId)).getStockCode();
			System.out.println("找到符合要求股票：" + stockCode);
			double[] reuslt = getMaxAndLow(result.getValue());
			System.out.println(reuslt[0] + "," + reuslt[1]);
			System.out.println("-----------------------------------------------");
		}
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 预测未来数据并判断是否符合标准
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
		for(int i = 0 ; i < 5 ; i ++) {
			for(int j = 0 ; j < 2 ; j++) {
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
		for(int i = 0 ; i < 5 ; i++) {
			if(testMaxBuffer != 0) {
				if(output.getDouble(i , 0) > testMaxBuffer) testMaxBuffer = output.getDouble(i , 0);
			}else testMaxBuffer = output.getDouble(i , 0);
		}
		double testMinBuffer = 0;
		for(int i = 0 ; i < 5 ; i++) {
			if(testMinBuffer != 0) {
				if(output.getDouble(i , 1) < testMinBuffer) testMinBuffer = output.getDouble(i , 1);
			}else testMinBuffer = output.getDouble(i , 1);
		}
		maxAndLow[0] = testMaxBuffer;
		maxAndLow[1] = testMinBuffer;
		return maxAndLow;
	}
	
}
