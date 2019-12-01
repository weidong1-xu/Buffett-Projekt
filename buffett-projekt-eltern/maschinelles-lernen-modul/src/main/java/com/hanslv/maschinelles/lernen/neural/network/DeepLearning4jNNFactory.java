package com.hanslv.maschinelles.lernen.neural.network;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.learning.config.IUpdater;

/**
 * 使用DeepLearning4j构建神经网络
 * @author hanslv
 *
 */
public class DeepLearning4jNNFactory {
	/**
	 * 搭建神经网络
	 * @param weightSeed 随机权重种子
	 * @param weightInit 初始化方式
	 * @param updater 梯度更新方式
	 * @param layers 各层神经网络，需要按顺序
	 * @return
	 */
	public static MultiLayerNetwork buildRNN(long weightSeed , WeightInit weightInit , IUpdater updater , FeedForwardLayer ... layers) {
		/*
		 * 初始化神经网络配置
		 */
        ListBuilder configBuilder = new NeuralNetConfiguration.Builder()
        		/*
        		 * 随机权重种子
        		 */
                .seed(weightSeed)
                /*
                 * 神经网络初始化方式(当前选择XAVIER：使每层输出的方差尽量相等)
                 * XAVIER能够很好的使用TANH激活函数，但是当使用ReLU时建议将初始化方式替换为WeightInit.RELU
                 */
//                .weightInit(WeightInit.XAVIER)
                .weightInit(weightInit)
                /*
                 * 梯度更新方式，将会影响神经网络中各个层，是每层神经网络梯度更新的默认方式
                 * Nesterov：根据动量理论进行优化，防止梯度大幅震荡，避免错过最小值，通过参数未来的大致方向对参数进行预测
                 * Adagrad：适合稀疏数据，根据参数对稀疏数据进行大步更新，对频繁参数进行小幅更新，缺点是学习速率总是在衰减
                 * AdaDelta：解决Adagrad学习速率衰减的问题
                 * Adam：学习速率为自适应的，自使用时刻估计法，收敛效果较好，如果希望加快收敛速度或网络为复杂解构则使用该方法|β1设为0.9，β2设为0.9999，ϵ设为10-8
                 * 如果输入数据集比较稀疏，SGD、Nesterov和动量项等方法可能效果不好
                 */
//                .updater(new Nesterovs(9, 0.9))
                .updater(updater)
                /*
                 * 设置梯度规范化器，防止梯度消失
                 */
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                /*
                 * 设置梯队规范化器阈值
                 */
                .gradientNormalizationThreshold(0.5)
                /*
                 * 根据上方的配置构建ListBuilder
                 */
                .list();
        /*
         * 添加各层
         */
        
        for(FeedForwardLayer layer : layers) configBuilder.layer(layer);

        /*
         * 初始化神经网络结构        
         */
        MultiLayerConfiguration layerConfig = configBuilder.build();
        
        /*
         * 实例化神经网络
         */
        MultiLayerNetwork network = new MultiLayerNetwork(layerConfig);
        
        /*
         * 初始化神经网络
         */
        network.init();
        
        /*
         * 设置监听器
         */
//        network.setListeners(new ScoreIterationListener(1));
        
        return network;
	}
}
