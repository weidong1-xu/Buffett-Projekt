package com.hanslv.stock.machine.learning.neural.network;

import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
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
	public static MultiLayerNetwork build(int inputSize , int idealOutputSize) {
		/*
		 * 隐藏层
		 */
		FeedForwardLayer hideLayerA = new LSTM.Builder()
				.nIn(inputSize)
				.nOut(idealOutputSize * 100)
				.activation(Activation.TANH).build();
		FeedForwardLayer hideLayerB = new LSTM.Builder()
				.nIn(idealOutputSize * 100)
				.nOut(idealOutputSize * 200)
				.dropOut(0.2)
				.activation(Activation.TANH).build();
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nIn(idealOutputSize * 200)
				.nOut(idealOutputSize)
				.activation(Activation.TANH)
				.build();
		return DeepLearning4jNNFactory.buildRNN(1000000 , WeightInit.XAVIER , new Adam(0.1 , 0.9 , 0.9999 , 0.0000000001) , hideLayerA , hideLayerB , outputLayer);
	}
}
