package com.hanslv.stock.machine.learning.constants;

import java.io.File;

/**
 * 神经网络常量
 * 
 * -------------------------------------------------
 * 1、训练最大迭代纪元								MAX_EPOCH
 * -------------------------------------------------
 * @author hanslv
 *
 */
public abstract class NeuralNetWorkConstants {
	public static final int MAX_EPOCH = 1000 * 6;//训练最大迭代纪元
	public static String ALGORITHM_FILENAME_SUFFIX = "date_price.eg";
	public static String ALGORITHM_BASE_DIR = "D:" + File.separator + "data" + File.separator + "mine" + File.separator + "test-dateVolumeNN" + File.separator;
}
