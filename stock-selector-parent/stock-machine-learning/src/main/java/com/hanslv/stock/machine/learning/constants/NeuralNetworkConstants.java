package com.hanslv.stock.machine.learning.constants;

import java.io.File;

/**
 * 神经网络常量
 * 
 * -------------------------------------------------
 * 1、训练最大迭代纪元								MAX_EPOCH
 * 2、算法文件名称后缀								ALGORITHM_FILENAME_SUFFIX
 * 3、算法文件文件夹位置								ALGORITHM_BASE_DIR
 * 4、训练数据长度									TRAIN_SIZE
 * 5、模型训练精度									TRAIN_ERROR_LIMIT
 * 6、训练模型样本表头								TRAIN_DATA_TITLE
 * 7、预测天数										RESULT_SIZE
 * 8、判断预测是否上涨阈值							ALGROITHM_RESULT_TRUE_FLAG
 * 9、dl4j训练数据长度								DL4J_TRAIN_SIZE
 * 10、dl4j验证数据长度								DL4J_TEST_SIZE
 * 11、dl4j标准化器存储根路径							DL4J_NORMALIZER_SAVE_PATH
 * 12、dl4j标准化器存储后缀							DL4J_NORMALIZER_SAVE_SUFFIX
 * 13、dl4j算法存储根路径							DL4J_NN_SAVE_PATH
 * 14、dl4j算法存储后缀								DL4J_NN_SAVE_SUFFIX
 * 15、dl4j训练纪元									DL4J_MAX_EPOCH
 * 16、dl4j精确度下限								DL4J_ACCURACY_LIMIT
 * -------------------------------------------------
 * @author hanslv
 *
 */
public abstract class NeuralNetworkConstants {
	
	public static String ALGORITHM_FILENAME_SUFFIX = "date_price.eg";//算法文件名称后缀
//	public static String ALGORITHM_BASE_DIR = "D:" + File.separator + "data" + File.separator + "mine" + File.separator + "test-dateVolumeNN" + File.separator;//算法文件文件夹位置
	public static String ALGORITHM_BASE_DIR = "E:" + File.separator + "Java" + File.separator + "eclipse" + File.separator + "stock-selector" + File.separator + "algorithm" + File.separator;
	
	public static final int MAX_EPOCH = 500 * 8;//训练最大迭代纪元
	public static final double TRAIN_ERROR_LIMIT = 0.0000005;//模型训练精度
	public static final int TRAIN_SIZE = 40;//训练数据长度
	public static final int RESULT_SIZE = 5;//预测天数
	public static final String TRAIN_DATA_TITLE = "month,day,stockEndPrice";//训练模型样本表头
	
	public static final int ALGROITHM_RESULT_TRUE_FLAG = 3;//判断预测是否上涨阈值
	
	
	public static final int DL4J_TRAIN_SIZE = 89;//dl4j训练数据长度
	public static final int DL4J_TEST_SIZE = 5;//dl4j验证数据长度
	public static final int DL4J_MAX_EPOCH = 50;//dl4j训练纪元
	public static final double DL4J_ACCURACY_LIMIT = 0.8;//dl4j精确度下限
	public static final String DL4J_NORMALIZER_SAVE_PATH = "E:\\Java\\eclipse\\stock-selector\\algorithm\\dl4j-normalizer";//dl4j标准化器存储根路径
	public static final String DL4J_NORMALIZER_SAVE_SUFFIX = ".eg";//dl4j标准化器存储后缀
	public static final String DL4J_NN_SAVE_PATH = "E:\\Java\\eclipse\\stock-selector\\algorithm\\dl4j-nn";//dl4j算法存储根路径
	public static final String DL4J_NN_SAVE_SUFFIX = ".eg";//dl4j算法存储后缀
	
	
//	public static final String DL4J_NORMALIZER_SAVE_PATH = "";//dl4j标准化器存储根路径
//	public static final String DL4J_NORMALIZER_SAVE_SUFFIX = "";//dl4j标准化器存储后缀
//	public static final String DL4J_NN_SAVE_PATH = "";//dl4j算法存储根路径
//	public static final String DL4J_NN_SAVE_SUFFIX = "";//dl4j算法存储后缀
}
