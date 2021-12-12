package com.hanslv.maschinelles.lernen.neural.network;

import java.util.HashMap;
import java.util.Map;

import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;

import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;

/**
 * 使用DeepLearning4j构建股票价格神经网络
 *
 * @author hanslv
 */
@SuppressWarnings("unchecked")
public class DeepLearning4jStockNNBuilder {
    @SuppressWarnings("rawtypes")
    private static Map paramMap = new HashMap();

    static {
        paramMap.put(DeepLearning4jNNFactory.SEED_KEY_NAME, NeuralNetworkConstants.seed);
        paramMap.put(DeepLearning4jNNFactory.BIAS_INIT_KEY_NAME, NeuralNetworkConstants.biasInit);
        IUpdater updater = new Adam(NeuralNetworkConstants.nnAdamLearningRate);
        paramMap.put(DeepLearning4jNNFactory.UPDATER_KEY_NAME, updater);
    }

    /**
     * 构建神经网络
     *
     * @return
     */
    public static MultiLayerNetwork build() {
        /*
         * 隐藏层
         */
        FeedForwardLayer hideLayerA = new GravesLSTM.Builder()
                .nIn(NeuralNetworkConstants.inputSize)
                .nOut(NeuralNetworkConstants.nnFirstOutRight)
                .activation(NeuralNetworkConstants.activationA)
                .build();
        FeedForwardLayer hideLayerB = new GravesLSTM.Builder()
                .nOut(NeuralNetworkConstants.nnSecondOutRight)
                .activation(NeuralNetworkConstants.activationB)
                .build();
        FeedForwardLayer hideLayerC = new GravesLSTM.Builder()
                .nOut(NeuralNetworkConstants.nnThirdOutRight)
                .activation(NeuralNetworkConstants.activationC)
                .build();
        /*
         * 输出层
         */
        FeedForwardLayer outputLayer = new RnnOutputLayer.Builder(NeuralNetworkConstants.lossFunction)
                .nOut(NeuralNetworkConstants.idealOutputSize)
                .activation(NeuralNetworkConstants.outputActivation)
                .build();
        return DeepLearning4jNNFactory.buildRNN(
                paramMap,
                hideLayerA,
                hideLayerB,
                hideLayerC,
                outputLayer);
    }
}
