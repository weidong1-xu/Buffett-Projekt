package com.hanslv.maschinelles.lernen.constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.deeplearning4j.nn.conf.BackpropType;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

/**
 * 神经网络常量
 * 
 * @author hanslv
 *
 */
public abstract class NeuralNetworkConstants {
	public static int nnFirstOutRight;//第一次神经网络加权
	public static int nnSecondOutRight;//第二层神经网络加权
	public static int nnThirdOutRight;//第三层神经网络加权
	
	//Adam参数
	public static double nnAdamLearningRate;
	
	public static int epoch;//训练纪元
	public static int trainDataSize;//训练数据量
	public static int forcastDataSize;//预测数据数据量
	public static int inputSize;//输入神经元数量
	public static int idealOutputSize;//输出神经元数量
	public static int singleTimeLength;//单个数据所包含时间跨度
	public static int averageType;//均线类型
	public static int batchSize;//单时间步长中包含的数据量
	
	public static Activation lstmActivationA = Activation.SOFTSIGN;//lstm层激活函数
	public static Activation lstmActivationB = Activation.SOFTSIGN;//lstm层激活函数
	public static Activation lstmActivationC = Activation.SOFTSIGN;//lstm层激活函数
	public static Activation outputActivation = Activation.IDENTITY;//输出层激活函数
	public static LossFunction lossFunction = LossFunctions.LossFunction.MSE;//损失函数
	public static String seed;//随机权重种子
	public static String biasInit;//偏置向量初始化
	public static BackpropType backpropType = BackpropType.TruncatedBPTT;//反向传播类型
	public static String bpttForwardLength;//正向传播截断长度
	public static String bpttBackwardLength;//反向传播截断长度
	
	public static String trainDate;//训练日期
	
	private static final String PROP_PATH = "/machineLearning-config.properties";
	public static String trainDataFilePathPrefix;
	public static String forcastDataFilePathPrefix;
	public static final String DATA_FILE_SUFFIX_PATH = ".csv";
	public static int sleepSecondCount;//每只股票执行时间间隔
	
	static {
		try(InputStream inputStream = NeuralNetworkConstants.class.getResourceAsStream(PROP_PATH);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8")){
			Properties prop = new Properties();
			prop.load(inputStreamReader);
			
			nnFirstOutRight = Integer.parseInt(prop.getProperty("nnFirstOutRight"));
			nnSecondOutRight = Integer.parseInt(prop.getProperty("nnSecondOutRight"));
			nnThirdOutRight = Integer.parseInt(prop.getProperty("nnThirdOutRight"));
			
			nnAdamLearningRate = Double.parseDouble(prop.getProperty("nnAdamLearningRate"));
			
			epoch = Integer.parseInt(prop.getProperty("epoch"));
			trainDataSize = Integer.parseInt(prop.getProperty("trainDataSize"));
			forcastDataSize = Integer.parseInt(prop.getProperty("forcastDataSize"));
			inputSize = Integer.parseInt(prop.getProperty("inputSize"));
			idealOutputSize = Integer.parseInt(prop.getProperty("idealOutputSize"));
			singleTimeLength = Integer.parseInt(prop.getProperty("singleTimeLength"));
			batchSize = Integer.parseInt(prop.getProperty("batchSize"));
			averageType = Integer.parseInt(prop.getProperty("averageType"));
			
			seed = prop.getProperty("seed");
			biasInit = prop.getProperty("biasInit");
			bpttForwardLength = prop.getProperty("bpttForwardLength");
			bpttBackwardLength = prop.getProperty("bpttBackwardLength");
			
			trainDate = prop.getProperty("trainDate");
			
			trainDataFilePathPrefix = prop.getProperty("trainDataFilePathPrefix");
			forcastDataFilePathPrefix = prop.getProperty("forcastDataFilePathPrefix");
			sleepSecondCount = Integer.parseInt(prop.getProperty("sleepSecondCount"));
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
