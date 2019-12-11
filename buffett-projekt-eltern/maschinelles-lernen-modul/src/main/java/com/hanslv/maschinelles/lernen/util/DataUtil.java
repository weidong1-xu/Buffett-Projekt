package com.hanslv.maschinelles.lernen.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

import com.hanslv.allgemein.dto.TabStockPriceInfo;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;

/**
 * 数据处理类
 * -----------------------------------------------
 * 1、将获取到的数据标准化并转换为DataSetIterator				public static List<DataSetIterator> dl4jDataNormalizer(List<String> rawDataList , List<String> testDataList , int idealOutputSize)
 * 2、将数据转换为List<String>								public static List<String> dl4jDataFormatter(List<TabStockPriceInfo> priceInfoList)
 * -----------------------------------------------
 * @author hanslv
 *
 */
public class DataUtil {
	
	/**
	 * 1、将获取到的数据标准化并转换为DataSetIterator
	 * @param rawDataList
	 * @param idealOutputSize
	 * @return
	 */
	public static List<DataSetIterator> dl4jDataNormalizer(List<String> rawDataList , List<String> testDataList , int idealOutputSize){
		List<DataSetIterator> iteratorList = new ArrayList<>();
		
		/*
		 * 数据标准化器
		 */
		NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(-1 , 1);
		
		/*
		 * 实例化数据集合迭代器
		 */
		DataSetIterator trainDataIterator = new ListDataSetIterator<>(dl4jDataParser(rawDataList, idealOutputSize) , rawDataList.size());
		DataSetIterator testDataIterator = new ListDataSetIterator<>(dl4jDataParser(testDataList , idealOutputSize) , testDataList.size());
		
		/*
		 * 初始化数据标准化器
		 */
		normalizer.fitLabel(true);//指定是否标准化idealOutput
		normalizer.fit(trainDataIterator);
		
		/*
		 * 给训练数据集合配置标准化器
		 */
		trainDataIterator.setPreProcessor(normalizer);
		testDataIterator.setPreProcessor(normalizer);
		
		iteratorList.add(trainDataIterator);
		iteratorList.add(testDataIterator);
		
		return iteratorList;
	}
	
	/**
	 * 2、将数据转换为List<String>
	 * @param priceInfoList
	 * @param isInPlanTest
	 * @return
	 */
	public static List<String> dl4jDataFormatterNew(List<TabStockPriceInfo> priceInfoList , boolean isInPlanTest){
		/*
		 * 结果集合
		 */
		List<String> resultList = new ArrayList<>();
		
		/*
		 * 获取将5天内的最高价和最低价
		 */
		int counter = 0;
		BigDecimal highestBuffer = null;
		BigDecimal lowestBuffer = null;
		
		/*
		 * 记录每5天的最高价和最低价
		 */
		List<String> highAndLowList = new ArrayList<>();
		
		for(TabStockPriceInfo priceInfo : priceInfoList) {
			/*
			 * 当前最高价
			 */
			BigDecimal currentHighest = priceInfo.getStockPriceHighestPrice();
			
			/*
			 * 当前最低价
			 */
			BigDecimal currentLowest = priceInfo.getStockPriceLowestPrice();
			
			/*
			 * 初始化或比对5天内最高价
			 */
			if(highestBuffer == null) highestBuffer = currentHighest;
			else if(highestBuffer.compareTo(currentHighest) < 0) highestBuffer = currentHighest;
			/*
			 * 初始化或比对5天内最低价
			 */
			if(lowestBuffer == null) lowestBuffer = currentLowest;
			else if(lowestBuffer.compareTo(currentLowest) > 0) lowestBuffer = currentLowest;
			
			
			/*
			 * 为5天则添加到resultList中
			 */
			if(++counter == 5) {
				String result = highestBuffer + "," + lowestBuffer;
				highAndLowList.add(result);
				
				/*
				 * 复原Buffers
				 */
				highestBuffer = null;
				lowestBuffer = null;
				counter = 0;
			}
		}
		
		
		/*
		 * 匹配5日内的信息和后5日的最高价、最低价
		 */
		/*
		 * 2019-12-11修改Bug，传入的训练数据源不准确
		 */
		Collections.reverse(priceInfoList);
		Collections.reverse(highAndLowList);
		
		for(int i = 0 ; i < priceInfoList.size() ; i++) {
			TabStockPriceInfo priceInfo = priceInfoList.get(i);
			String inputData = priceInfo.getStockPriceVolume() + "," + priceInfo.getStockPriceStartPrice() + "," + priceInfo.getStockPriceEndPrice();
			
			int highAndLowIndex = (i / NeuralNetworkConstants.singleBatchSize) + 1;
			String idealOutput = "";
			if(highAndLowIndex < highAndLowList.size()) idealOutput = highAndLowList.get(highAndLowIndex);
			
			String result = "";
			if(isInPlanTest) {
				if(!"".equals(idealOutput)) {
					result = inputData + "," + idealOutput;
					resultList.add(result);
				}
			}else {
				/*
				 * 若idealOutput为空则用其他值补位
				 */
				result = ("".equals(idealOutput)) ? inputData + "," + highAndLowList.get(0) : inputData + "," + idealOutput;
				resultList.add(result);
			}
		}
		return resultList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 将List<String>数据转换为List<DataSet>数据
	 * @param rawDataList
	 * @param idealOutputSize
	 * @return
	 */
	private static List<DataSet> dl4jDataParser(List<String> rawDataList , int idealOutputSize){
		/*
		 * 将数据封装成DataSet集合
		 */
		List<DataSet> dataSetList = new ArrayList<>();
		
		/*
		 * 遍历raw数据集
		 */
		for(String rawData : rawDataList) {
			
			/*
			 * 未标准化数据字符串数组
			 */
			String[] rawDataArray = rawData.split(",");
			
			/*
			 * 输入数据的double数组、期望输出数据数组
			 */
			double[] inputDataDoubleArray = new double[rawDataArray.length - idealOutputSize];
			double[] idealOutputDataDoubleArray = new double[idealOutputSize];


			
			/*
			 * 根据下标将数据插入到对应的double数组中
			 */
			for(int i = 0 ; i < rawDataArray.length ; i++) {
				if(i < rawDataArray.length - idealOutputSize) inputDataDoubleArray[i] = new Double(rawDataArray[i]);
				else idealOutputDataDoubleArray[i - rawDataArray.length + idealOutputSize] = new Double(rawDataArray[i]);
			}
			
			/*
			 * 输入数据向量数组、期望输出数据向量数组
			 * dimension 0 = number of examples in minibatch(训练批次长度)
			 * dimension 1 = size of each vector (i.e., number of characters)(训练数据列数)
			 * dimension 2 = length of each time series/example(训练数据行数)
			 * Why 'f' order here? See http://deeplearning4j.org/usingrnns.html#data section "Alternative: Implementing a custom DataSetIterator"
			 */
			INDArray inputScalerArray = Nd4j.create(inputDataDoubleArray);
			INDArray idealOutputScalerArray = Nd4j.create(idealOutputDataDoubleArray);
			
			/*
			 * 实例化DataSet对象并存入List
			 */
			dataSetList.add(new DataSet(inputScalerArray , idealOutputScalerArray));
		}
		return dataSetList;
	}
	
}
