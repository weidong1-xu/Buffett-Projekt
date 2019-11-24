package com.hanslv.stock.machine.learning.neural.network;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jboss.logging.Logger;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.stock.machine.learning.constants.NeuralNetworkConstants;
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
	
	Logger logger = Logger.getLogger(DeepLearning4jStockNNTrainer.class);
	
	
	/**
	 * 训练LSTM股票模型
	 * @param stockId 股票ID
	 * @param trainDataSize 训练数据长度
	 * @param testDataSize 测试数据长度
	 * @param idealOutputSize 输出神经元数量
	 * @param epoch 训练纪元
	 */
	public void train(String stockId , int trainDataSize , int testDataSize , int idealOutputSize , int epoch) {
		logger.info("正在计算股票：" + stockId);
		
		/*
		 * 获取训练数据和测试数据
		 */
		List<String> mainDataList = DataUtil.dl4jDataFormatter(stockPriceInfoMapper.getTrainAndTestDataDL4j(stockId , trainDataSize + testDataSize + 1));
		
		/*
		 * 判断数据量是否符合标准
		 */
		if(mainDataList.size() < (trainDataSize + testDataSize)) {
			logger.error("数据集小于预期");
			return;
		}
		
		List<String> trainDataList = mainDataList.subList(0 , trainDataSize);
		List<String> testDataList = mainDataList.subList(trainDataSize , mainDataList.size());
		
		/*
		 * 标准化训练数据集、测试数据集
		 */
		List<DataSetIterator> iteratorList = DataUtil.dl4jDataNormalizer(trainDataList , testDataList, idealOutputSize);
		
		/*
		 * 获取标准化器并将其持久化
		 */
		NormalizerStandardize normalizerStandardize = (NormalizerStandardize) iteratorList.get(1).getPreProcessor();
		NormalizerSerializer serializer = NormalizerSerializer.getDefault();
		File normalizerFile = new File(NeuralNetworkConstants.DL4J_NORMALIZER_SAVE_PATH + File.separator + stockId + NeuralNetworkConstants.DL4J_NORMALIZER_SAVE_SUFFIX);
		/*
		 * 删除历史文件
		 */
		if(normalizerFile.exists()) normalizerFile.delete();
		
		try {
			/*
			 * 执行序列化
			 */
			serializer.write(normalizerStandardize , normalizerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * 获取神经网络模型
		 */
		MultiLayerNetwork lstmNetwork = DeepLearning4jStockNNBuilder.build();
		
		/*
		 * 训练模型epoch次
		 */
		for(int i = 0 ; i < epoch ; i++) {
			lstmNetwork.fit(iteratorList.get(0));
			lstmNetwork.rnnClearPreviousState();
		}
		
		/*
		 * 重置训练数据用于模型评估
		 */
		iteratorList.get(1).reset();
		
		Evaluation evaluation = lstmNetwork.evaluate(iteratorList.get(1));
		
		/*
		 * 当精度大于下限时保存算法模型
		 */
		if(evaluation.accuracy() >= NeuralNetworkConstants.DL4J_ACCURACY_LIMIT) {
			File lstmNetworkFile = new File(NeuralNetworkConstants.DL4J_NN_SAVE_PATH + File.separator + stockId + NeuralNetworkConstants.DL4J_NN_SAVE_SUFFIX);
			/*
			 * 删除历史文件
			 */
			if(lstmNetworkFile.exists()) lstmNetworkFile.delete();
			try {
				lstmNetwork.save(lstmNetworkFile);
				logger.info("保存了股票：" + stockId + "的算法模型");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
