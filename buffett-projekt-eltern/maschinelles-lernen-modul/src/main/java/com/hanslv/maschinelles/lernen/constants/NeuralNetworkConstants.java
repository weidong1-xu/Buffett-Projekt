package com.hanslv.maschinelles.lernen.constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 神经网络常量
 * 
 * -------------------------------------------------
 * 
 * -------------------------------------------------
 * @author hanslv
 *
 */
public abstract class NeuralNetworkConstants {
	public static int nnFirstOutRight;//第一次神经网络加权
	public static int nnSecondOutRight;//第二层神经网络加权
	public static int nnThirdOutRight;//第三层神经网络加权
	public static double nnDropout;//神经网络失活比例
	
	//Adam参数
	public static double nnAdamLearningRate;
	public static double nnAdamBeta1;
	public static double nnAdamBeta2;
	public static double nnAdamEpsilon;
	
	public static int epoch;//训练纪元
	public static int trainDataSize;//训练数据量
	public static int testDataSize;//测试数据量
	public static int inputSize;//输入神经元数量
	public static int idealOutputSize;//输出神经元数量
	public static int singleBatchSize;//单批次数据量
	public static int batchUnitLength;//单批次中包含的数据单元数量（单批次数据量的个数）
	public static int averageType;//均线类型
	public static double errorLimit;//矩形误差容忍范围
	public static double buyErrorLimit;//买入亏损容忍百分比
	
	private static final String PROP_PATH = "/machineLearning-config.properties";
	
	static {
		try(InputStream inputStream = NeuralNetworkConstants.class.getResourceAsStream(PROP_PATH);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream , "UTF-8")){
			Properties prop = new Properties();
			prop.load(inputStreamReader);
			
			nnFirstOutRight = Integer.parseInt(prop.getProperty("nnFirstOutRight"));
			nnSecondOutRight = Integer.parseInt(prop.getProperty("nnSecondOutRight"));
			nnThirdOutRight = Integer.parseInt(prop.getProperty("nnThirdOutRight"));
			nnDropout = Double.parseDouble(prop.getProperty("nnDropout"));
			
			nnAdamLearningRate = Double.parseDouble(prop.getProperty("nnAdamLearningRate"));
			nnAdamBeta1 = Double.parseDouble(prop.getProperty("nnAdamBeta1"));
			nnAdamBeta2 = Double.parseDouble(prop.getProperty("nnAdamBeta2"));
			nnAdamEpsilon = Double.parseDouble(prop.getProperty("nnAdamEpsilon"));
			
			epoch = Integer.parseInt(prop.getProperty("epoch"));
			trainDataSize = Integer.parseInt(prop.getProperty("trainDataSize"));
			testDataSize = Integer.parseInt(prop.getProperty("testDataSize"));
			inputSize = Integer.parseInt(prop.getProperty("inputSize"));
			idealOutputSize = Integer.parseInt(prop.getProperty("idealOutputSize"));
			singleBatchSize = Integer.parseInt(prop.getProperty("singleBatchSize"));
			batchUnitLength = Integer.parseInt(prop.getProperty("batchUnitLength"));
			averageType = Integer.parseInt(prop.getProperty("averageType"));
			errorLimit = Double.parseDouble(prop.getProperty("errorLimit"));
			buyErrorLimit = Double.parseDouble("buyErrorLimit");
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
