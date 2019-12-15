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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.allgemein.dto.TabStockPriceInfo;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.repository.TabStockPriceInfoRepository;

/**
 * 数据处理类
 * -----------------------------------------------
 * 1、将获取到的数据标准化并转换为DataSetIterator				public static List<DataSetIterator> dl4jDataNormalizer(List<String> rawDataList , List<String> testDataList , int idealOutputSize)
 * 2、2、根据股票ID获取预测股票矩形面积List					public List<String> dl4jDataFormatter(Integer stockId , int stepLong , String endDate)
 * -----------------------------------------------
 * @author hanslv
 *
 */
@Component
public class DataUtil {
	static int dayCounter;//天数计数器
	static BigDecimal maxBuffer;//最大值缓存
	static BigDecimal minBuffer;//最小值缓存
	
	@Autowired
	private TabStockPriceInfoRepository priceInfoMapper;
	
	
	/**
	 * 1、将获取到的数据标准化并转换为DataSetIterator
	 * @param rawDataList
	 * @param idealOutputSize
	 * @return
	 */
	public List<DataSetIterator> dl4jDataNormalizer(List<String> rawDataList , List<String> testDataList , int idealOutputSize){
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
	 * 2、根据股票ID获取预测股票矩形面积List
	 * @param stockId
	 * @param stepLong = 训练步长+测试步长
	 * @param rectangleLong = 每个步长所包含的数据量
	 * @return
	 */
	public List<String> dl4jDataFormatter(Integer stockId , int stepLong , String endDate){
		List<String> resultList = new ArrayList<>();
		List<String> resultListBuffer = new ArrayList<>();
		
		/**
		 * 获取5天内的最大值、最小值
		 */
		for(String maxAndLowStr : getRectangleMaxAndLow(stockId , stepLong , endDate)) {
			String[] maxAndLowArray = maxAndLowStr.split(",");
			BigDecimal[] maxAndMinArray = {new BigDecimal(maxAndLowArray[0]) , new BigDecimal(maxAndLowArray[1])};
			resultListBuffer.add(doGetRectangleArea(maxAndMinArray));
		}
		
		/*
		 * 将后一天结果拼接到前一天
		 */
		for(int i = 0 ; i < resultListBuffer.size() ; i++) {
			if(i == 0) resultList.add(resultListBuffer.get(i) + "," + resultListBuffer.get(i));//包含当前日信息，并以任意值补位
			if((i + 1) < resultListBuffer.size())
				resultList.add(resultListBuffer.get(i + 1) + "," + resultListBuffer.get(i));
		}
		
		Collections.reverse(resultList);
		return resultList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 将List<String>数据转换为List<DataSet>数据
	 * @param rawDataList
	 * @param idealOutputSize
	 * @return
	 */
	private List<DataSet> dl4jDataParser(List<String> rawDataList , int idealOutputSize){
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
	
	
	
	/**
	 * 根据所给最高价与最低价差值百分比、矩形长度获取矩形面积
	 * @param data
	 * @return
	 */
	private static String doGetRectangleArea(BigDecimal[] maxAndMinArray) {
		BigDecimal rectangleWidth = maxAndMinArray[0].subtract(maxAndMinArray[1]).divide(maxAndMinArray[1] , 2 , BigDecimal.ROUND_HALF_UP);//矩形宽度=(最大值-最小值)/最小值
		return rectangleWidth.multiply(new BigDecimal(NeuralNetworkConstants.batchUnitLength * NeuralNetworkConstants.singleBatchSize)).setScale(2 , BigDecimal.ROUND_HALF_UP).toString();
	}
	
	
	
	/**
	 * 获取最高价、最低价
	 * @param stockId
	 * @param stepLong
	 * @param endDate
	 * @param batchSize
	 * @param rectangleLong
	 * @return
	 */
	private List<String> getRectangleMaxAndLow(Integer stockId , int stepLong , String endDate){
		List<String> resultList = new ArrayList<>();
		String endDateCopy = endDate;
		/*
		 * 获取每个矩形中包含的价格信息
		 */
		for(int i = 0 ; i < stepLong ; i++) {
			List<TabStockPriceInfo> priceInfoList = priceInfoMapper.getTrainAndTestDataDL4j(stockId , NeuralNetworkConstants.batchUnitLength * NeuralNetworkConstants.singleBatchSize , endDateCopy);
			for(TabStockPriceInfo priceInfo : priceInfoList) {
				/*
				 * 获取当前矩形价格的最高、最低
				 */
				dayCounter++;
				BigDecimal currentMax = priceInfo.getStockPriceHighestPrice();
				BigDecimal currentMin = priceInfo.getStockPriceLowestPrice();
				if(maxBuffer == null || currentMax.compareTo(maxBuffer) > 0) maxBuffer = currentMax;
				if(minBuffer == null || currentMin.compareTo(minBuffer) < 0) minBuffer = currentMin;
				if(dayCounter ==  NeuralNetworkConstants.batchUnitLength * NeuralNetworkConstants.singleBatchSize) {
					/*
					 * 获取结果
					 */
					resultList.add(maxBuffer + "," + minBuffer);
					/*
					 * 复位计数器、buffer
					 */
					dayCounter = 0;
					maxBuffer = null;
					minBuffer = null;
				}
			}
			/*
			 * 将日期前移5个日期单位
			 */
			endDateCopy = changeDate(stockId , endDateCopy , NeuralNetworkConstants.batchUnitLength , true);
		}
		return resultList;
	}
	
	
	/**
	 * 将日期向前或后推进limit个数据长度
	 * @param stockId
	 * @param currentDate
	 * @param limit
	 * @param forwardOrBackward true-日期向前移动，false-日期向后移动
	 * @return
	 */
	private String changeDate(Integer stockId , String currentDate , int count , boolean forwardOrBackward) {
		String resultDate = "";
		if(forwardOrBackward) {
			for(TabStockPriceInfo priceInfo : priceInfoMapper.changeDateForward(stockId , currentDate , count)) resultDate = priceInfo.getStockPriceDate();
		}else {
			for(TabStockPriceInfo priceInfo : priceInfoMapper.changeDateBackward(stockId , currentDate , count)) resultDate = priceInfo.getStockPriceDate();
		}
		return resultDate;
	}
	
}
