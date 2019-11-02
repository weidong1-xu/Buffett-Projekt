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
 * -------------------------------------------------
 * @author hanslv
 *
 */
public abstract class NeuralNetWorkConstants {
	public static final int MAX_EPOCH = 1000 * 6;//训练最大迭代纪元
	public static String ALGORITHM_FILENAME_SUFFIX = "date_price.eg";//算法文件名称后缀
	public static String ALGORITHM_BASE_DIR = "D:" + File.separator + "data" + File.separator + "mine" + File.separator + "test-dateVolumeNN" + File.separator;//算法文件文件夹位置
	public static final int TRAIN_SIZE = 70;//训练数据长度
	public static final double TRAIN_ERROR_LIMIT = 0.000005;//模型训练精度
	public static final String TRAIN_DATA_TITLE = "";//训练模型样本表头
}
