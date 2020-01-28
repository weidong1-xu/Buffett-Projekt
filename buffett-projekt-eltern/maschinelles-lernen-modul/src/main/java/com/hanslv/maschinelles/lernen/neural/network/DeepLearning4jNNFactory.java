package com.hanslv.maschinelles.lernen.neural.network;

import java.util.Map;
import java.util.Optional;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Sgd;

/**
 * 使用DeepLearning4j构建神经网络
 * @author hanslv
 *
 */
public class DeepLearning4jNNFactory {
	public static final String SEED_KEY_NAME = "WEIGHT_SEED";
	public static final String OPT_ALGO_KEY_NAME = "OPT_ALGO";
	public static final String BIAS_INIT_KEY_NAME = "BIAS_INIT";
	public static final String WEIGH_INIT_KEY_NAME = "WEIGHT_INIT";
	public static final String UPDATER_KEY_NAME = "UPDATER";
	public static final String GRADIENT_NORMALIZATION_KEY_NAME = "GRADIENT_NORMALIZATION";
	public static final String GRADIENT_NORMALIZATION_THRESHOLD_KEY_NAME = "GRADIENT_NORMALIZATION_THRESHOLD";
	public static final String PRETRAIN_KEY_NAME = "PRETRAIN";
	public static final String BACKPROP_KEY_NAME = "BACKPROP";
	public static final String BACKPROP_TYPE_KEY_NAME = "BACKPROP_TYPE";
	public static final String BPTT_FORWARD_LENGTH_KEY_NAME = "BPTT_FORWARD_LENGTH";
	public static final String BPTT_BACKWARD_LENGTH_KEY_NAME = "BPTT_BACKWARD_LENGTH";
	
	/**
	 * 搭建神经网络
	 * @param weightSeed 随机权重种子
	 * @param weightInit 初始化方式
	 * @param updater 梯度更新方式
	 * @param layers 各层神经网络，需要按顺序
	 * @return
	 */
	public static MultiLayerNetwork buildRNN(@SuppressWarnings("rawtypes") Map paramMap , FeedForwardLayer ... layers) {
		Optional<Object> weightSeed = Optional.ofNullable(paramMap.get(SEED_KEY_NAME));
		Optional<Object> optAlgo = Optional.ofNullable(paramMap.get(OPT_ALGO_KEY_NAME));
		Optional<Object> biasInit = Optional.ofNullable(paramMap.get(BIAS_INIT_KEY_NAME));
		Optional<Object> weightInit = Optional.ofNullable(paramMap.get(WEIGH_INIT_KEY_NAME));
		Optional<Object> updater = Optional.ofNullable(paramMap.get(UPDATER_KEY_NAME));
		Optional<Object> gradientNormalization = Optional.ofNullable(paramMap.get(GRADIENT_NORMALIZATION_KEY_NAME));
		Optional<Object> gradientNormalizationThreshold = Optional.ofNullable(paramMap.get(GRADIENT_NORMALIZATION_THRESHOLD_KEY_NAME));
		Optional<Object> pretrain = Optional.ofNullable(paramMap.get(PRETRAIN_KEY_NAME));
		Optional<Object> backprop = Optional.ofNullable(paramMap.get(BACKPROP_KEY_NAME));
		Optional<Object> backpropType = Optional.ofNullable(paramMap.get(BACKPROP_TYPE_KEY_NAME));
		Optional<Object> bpttForLength = Optional.ofNullable(paramMap.get(BPTT_FORWARD_LENGTH_KEY_NAME));
		Optional<Object> bpttBackLength = Optional.ofNullable(paramMap.get(BPTT_BACKWARD_LENGTH_KEY_NAME));
		
		/*
		 * 初始化神经网络配置
		 */
        ListBuilder configBuilder = new NeuralNetConfiguration.Builder()
        		/*
        		 * 随机权重种子
        		 */
                .seed(Long.parseLong(String.valueOf(weightSeed.orElse(System.currentTimeMillis() + ""))))
                /*
                 * 梯度下降方式
                 */
                .optimizationAlgo((OptimizationAlgorithm)optAlgo.orElse(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT))
                /*
                 * 初始化偏置向量
                 */
                .biasInit(Double.parseDouble(String.valueOf(biasInit.orElse("0.0"))))
                /*
                 * 神经网络初始化方式(当前选择XAVIER：使每层输出的方差尽量相等)
                 * XAVIER能够很好的使用TANH激活函数，但是当使用ReLU时建议将初始化方式替换为WeightInit.RELU
                 */
                .weightInit((WeightInit)weightInit.orElse(WeightInit.XAVIER))
                /*
                 * 梯度更新方式，将会影响神经网络中各个层，是每层神经网络梯度更新的默认方式
                 * Nesterov：根据动量理论进行优化，防止梯度大幅震荡，避免错过最小值，通过参数未来的大致方向对参数进行预测
                 * Adagrad：适合稀疏数据，根据参数对稀疏数据进行大步更新，对频繁参数进行小幅更新，缺点是学习速率总是在衰减
                 * AdaDelta：解决Adagrad学习速率衰减的问题
                 * Adam：学习速率为自适应的，自使用时刻估计法，收敛效果较好，如果希望加快收敛速度或网络为复杂解构则使用该方法|β1设为0.9，β2设为0.9999，ϵ设为10-8
                 * 如果输入数据集比较稀疏，SGD、Nesterov和动量项等方法可能效果不好
                 */
                .updater((IUpdater)updater.orElse(new Sgd()))
                /*
                 * 设置梯度规范化器，防止梯度消失
                 * GradientNormalization.ClipElementWiseAbsoluteValue
                 */
                .gradientNormalization((GradientNormalization)gradientNormalization.orElse(GradientNormalization.None))
                /*
                 * 设置梯队规范化器阈值
                 * 0.5
                 */
                .gradientNormalizationThreshold(Double.parseDouble(String.valueOf(gradientNormalizationThreshold.orElse("1.0"))))
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
        MultiLayerConfiguration layerConfig = configBuilder        		
        		/*
        		 * 是否预训练
        		 */
        		.pretrain(Boolean.parseBoolean(String.valueOf(pretrain.orElse("false"))))
        		/*
        		 * 是否反向传播
        		 */
        		.backprop(Boolean.parseBoolean(String.valueOf(backprop.orElse("true"))))
        		/*
        		 * 设置反向传播类型为截断式传播TruncatedBPTT
        		 */
        		.backpropType((BackpropType)backpropType.orElse(BackpropType.Standard))
        		/*
        		 * 设置向前传播截断长度50
        		 */
        		.tBPTTForwardLength(Integer.parseInt(String.valueOf(bpttForLength.orElse("20"))))
        		/*
        		 * 设置向后传播截断长度50
        		 */
        		.tBPTTBackwardLength(Integer.parseInt(String.valueOf(bpttBackLength.orElse("20"))))
        		/*
        		 * 构建配置对象
        		 */
        		.build();
        
        /*
         * 实例化神经网络
         */
        MultiLayerNetwork network = new MultiLayerNetwork(layerConfig);
        
        /*
         * 初始化神经网络
         */
        network.init();
        
        return network;
	}
}
