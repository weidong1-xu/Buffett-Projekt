package com.hanslv.stock.machine.learning.neural.network;

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

import com.hanslv.stock.machine.learning.repository.TabStockInfoRepository;
import com.hanslv.stock.machine.learning.repository.TabStockPriceInfoRepository;
import com.hanslv.stock.machine.learning.util.DataUtil;

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
		logger.info("正在计算股票：" + stockId);
		
		/*
		 * 获取训练数据和预测下一周信息的输入数据
		 */
		List<String> mainDataList = DataUtil.dl4jDataFormatterNew(stockPriceInfoMapper.getTrainAndTestDataDL4j(stockId , (trainDataSize + 1) * 5));
		
		/*
		 * 判断数据量是否符合标准
		 */
		if(mainDataList.size() < (trainDataSize + 1)) {
			logger.error("数据集小于预期");
			return;
		}
		
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
			String stockCode = stockInfoMapper.selectById(new Integer(stockId)).getStockCode();
			logger.info("找到符合要求股票：" + stockCode);
			logger.info("预计输出价格：");
			logger.info(result.getValue());
			logger.info("-----------------------------------");
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
		INDArray unNormalizerOutput = resultDataSet.getLabels();
		resultMap.put(doCheckData(unNormalizerOutput) , unNormalizerOutput);
		return resultMap;
	}
	/**
	 * 判断当前反标准化结果集是否符合标准
	 * @param unNormalizerOutput
	 * @return
	 */
	private boolean doCheckData(INDArray unNormalizerOutput) {
		Set<BigDecimal> setA = new HashSet<>();
		Set<BigDecimal> setB = new HashSet<>();
		for(int i = 0 ; i < 5 ; i ++) {
			for(int j = 0 ; j < 2 ; j++) {
				if(j == 0) setA.add(new BigDecimal(unNormalizerOutput.getDouble(i , j)).setScale(1 , BigDecimal.ROUND_HALF_UP));
				else setB.add(new BigDecimal(unNormalizerOutput.getDouble(i , j)).setScale(1 , BigDecimal.ROUND_HALF_UP));
			}
		}
		return (setA.size() > 1 || setB.size() > 1) ? false : true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 将当前股票信息标准化器序列化
	 * @param normalizer
	 * @param currentStockId
	 */
//	private void normalizerSerializer(NormalizerMinMaxScaler normalizer , String currentStockId) {
//		NormalizerSerializer serializer = NormalizerSerializer.getDefault();
//		File normalizerFile = new File(NeuralNetworkConstants.DL4J_NORMALIZER_SAVE_PATH + File.separator + currentStockId + NeuralNetworkConstants.DL4J_NORMALIZER_SAVE_SUFFIX);
//		/*
//		 * 删除历史文件
//		 */
//		if(normalizerFile.exists()) normalizerFile.delete();
//		
//		try {
//			/*
//			 * 将标准化器序列化到文件中
//			 */
//			serializer.write(normalizer , normalizerFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	/*
//	 * 保存算法模型
//	 */
//	Evaluation evaluation = lstmNetwork.evaluate(iteratorList.get(1));
//	
//	/*
//	 * 当精度大于下限时保存算法模型
//	 */
//	if(evaluation.accuracy() >= NeuralNetworkConstants.DL4J_ACCURACY_LIMIT) {
//		File lstmNetworkFile = new File(NeuralNetworkConstants.DL4J_NN_SAVE_PATH + File.separator + stockId + NeuralNetworkConstants.DL4J_NN_SAVE_SUFFIX);
//		/*
//		 * 删除历史文件
//		 */
//		if(lstmNetworkFile.exists()) lstmNetworkFile.delete();
//		try {
//			lstmNetwork.save(lstmNetworkFile);
//			logger.info("保存了股票：" + stockId + "的算法模型");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	}
}
