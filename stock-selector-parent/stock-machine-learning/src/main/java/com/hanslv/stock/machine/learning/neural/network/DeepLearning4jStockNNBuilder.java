package com.hanslv.stock.machine.learning.neural.network;

import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.impl.ActivationTanH;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * 使用DeepLearning4j构建股票价格神经网络
 * @author hanslv
 *
 */
public class DeepLearning4jStockNNBuilder {
	/**
	 * 构建神经网络
	 * @return
	 */
	public static MultiLayerNetwork build() {
		/*
		 * 隐藏层
		 */
		FeedForwardLayer hideLayerA = new LSTM.Builder()
				.nIn(2)
				.nOut(4)
				.activation(new ActivationTanH()).build();
		FeedForwardLayer hideLayerB = new LSTM.Builder()
				.nIn(4)
				.nOut(4)
				.activation(new ActivationTanH()).build();
		
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nIn(4)
				.nOut(2)
				.activation(new ActivationTanH()).build();
		
		return DeepLearning4jNNFactory.buildRNN(100000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 1e-10) , hideLayerA , hideLayerB , outputLayer);
	}
}
