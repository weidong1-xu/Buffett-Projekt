package com.hanslv.maschinelles.lernen.neural.network;

import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;

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
				.nIn(NeuralNetworkConstants.inputSize)
				.nOut(NeuralNetworkConstants.idealOutputSize * NeuralNetworkConstants.nnFirstOutRight)
				.activation(Activation.TANH)
				.build();
		FeedForwardLayer hideLayerB = new LSTM.Builder()
				.nIn(NeuralNetworkConstants.idealOutputSize * NeuralNetworkConstants.nnFirstOutRight)
				.nOut(NeuralNetworkConstants.idealOutputSize * NeuralNetworkConstants.nnSecondOutRight)
				.activation(Activation.TANH)
				.build();
		/*
		 * 输出层
		 */
		FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
				.nIn(NeuralNetworkConstants.idealOutputSize * NeuralNetworkConstants.nnSecondOutRight)
				.nOut(NeuralNetworkConstants.idealOutputSize)
				.activation(Activation.TANH)
				.dropOut(NeuralNetworkConstants.nnDropout)
				.build();
		return DeepLearning4jNNFactory.buildRNN(
				1000000 , 
				WeightInit.XAVIER , 
				new Adam(NeuralNetworkConstants.nnAdamLearningRate , NeuralNetworkConstants.nnAdamBeta1 , NeuralNetworkConstants.nnAdamBeta2 , NeuralNetworkConstants.nnAdamEpsilon) , 
				hideLayerA , 
				hideLayerB , 
				outputLayer);
	}
}
