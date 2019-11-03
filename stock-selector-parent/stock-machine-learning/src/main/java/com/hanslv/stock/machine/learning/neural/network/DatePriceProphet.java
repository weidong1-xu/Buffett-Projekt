package com.hanslv.stock.machine.learning.neural.network;

import java.io.File;
import java.time.LocalDate;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.springframework.stereotype.Component;

import com.hanslv.stock.machine.learning.constants.NeuralNetworkConstants;
import com.hanslv.stock.machine.learning.util.DataUtil;
import com.hanslv.stock.selector.commons.dto.TabPriceDateMLResultFiveDays;

/**
 * 股票日期-价格预言家
 * 
 * ------------------------------------------
 * 1、预言一只股票								public TabPriceDateMLResultFiveDays saySomething(Integer stockId , String currentDate)
 * ------------------------------------------
 * @author hanslv
 *
 */
@Component
public class DatePriceProphet {
	/**
	 * 1、预言一只股票
	 * @param stockId
	 * @param currentDate
	 * @return
	 */
	public TabPriceDateMLResultFiveDays saySomething(Integer stockId , String currentDate){
		TabPriceDateMLResultFiveDays result = new TabPriceDateMLResultFiveDays();
		result.setStockId(stockId);
		result.setRunDate(currentDate);
		
		/*
		 * 算法文件地址
		 */
		String algorithmFilePath =  NeuralNetworkConstants.ALGORITHM_BASE_DIR + stockId + "_" + NeuralNetworkConstants.ALGORITHM_FILENAME_SUFFIX;
		
		/*
		 * 判断算法是否存在
		 */
		if(!new File(algorithmFilePath).exists()) return null;
		
		/*
		 * 读取当前算法
		 */
		BasicNetwork algorithm = DataUtil.loadAlgorithm(algorithmFilePath);
		
		/*
		 * 当前日期
		 */
		LocalDate currentLocalDate = LocalDate.parse(currentDate);
		
		/*
		 * 根据结果天数设定执行计算
		 */
		for(long i = 0 ; i <= NeuralNetworkConstants.RESULT_SIZE ; i++) {
			String[] currentDateArray = currentLocalDate.plusDays(i).toString().split("-");
			double[] inputData = {new Double(currentDateArray[1]) , new Double(currentDateArray[2])};
			
			/*
			 * 执行运算
			 */
			MLData currentResultMLData = algorithm.compute(new BasicMLData(inputData));
			
			/*
			 * 结果中的收盘价
			 */
			double currentResult = currentResultMLData.getData()[1];
			
			if(result.getEndPriceCurrent() == null) result.setEndPriceCurrent(String.valueOf(currentResult));
			else if(result.getEndPriceA() == null) result.setEndPriceA(String.valueOf(currentResult));
			else if(result.getEndPriceB() == null) result.setEndPriceB(String.valueOf(currentResult));
			else if(result.getEndPriceC() == null) result.setEndPriceC(String.valueOf(currentResult));
			else if(result.getEndPriceD() == null) result.setEndPriceD(String.valueOf(currentResult));
			else result.setEndPriceE(String.valueOf(currentResult));
		}
		
		return result;
	}
}
