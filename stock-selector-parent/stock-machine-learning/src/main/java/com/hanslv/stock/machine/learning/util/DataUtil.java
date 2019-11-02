package com.hanslv.stock.machine.learning.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.encog.util.csv.CSVFormat;

import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;
/**
 * 数据处理类
 * -----------------------------------------------
 * 1、获取标准化的数据						public static MLDataSet dataAnalyze(List<String> objectStringList , String[] fieldNames , int targetColumnLength , double normalizedH , double normalizedL)
 * 2、将算法保存到文件						public static void saveAlgorithm(String filePath , BasicNetwork trainedNetwork)
 * 3、从文件加载算法							public static BasicNetwork loadAlgorithm(String filePath)
 * 4、将股票价格信息List转换为字符串List		public static List<String> transPriceInfoToString(List<TabStockPriceInfo> priceInfoList)
 * -----------------------------------------------
 * @author hanslv
 *
 */
public class DataUtil {
	/**
	 * 1、获取标准化的数据
	 * @param objectStringList 非标准化数据集合，需要将idealOutput字段放在后边，只可传入数字格式的字符串
	 * @param fieldNames 字段名称数组
	 * @param normalizedH 标准化后的最大值
	 * @param normalizedL 标准化后的最小值
	 * @param targetColumnLength idealOutput字段数量
	 * @return
	 */
	public static MLDataSet dataAnalyze(List<String> objectStringList , String[] fieldNames , int targetColumnLength , double normalizedH , double normalizedL) {
		/*
		 * 标准化后的输入样本数组和预测输出样本数组
		 */
		double[][] inputArray = new double[objectStringList.size()][];
		double[][] idealOutputArray = new double[objectStringList.size()][];
		
		/*
		 * 存放每个字段的NormalizedField对象，key=字段名称  value=对应的NormalizedField对象
		 */
		Map<String , NormalizedField> normalizedFieldMap = new HashMap<>();
		for(String fieldName : fieldNames) {
			NormalizedField currentNormalizedField = new NormalizedField(NormalizationAction.Normalize , fieldName , Integer.MIN_VALUE , Integer.MAX_VALUE , normalizedH , normalizedL);
			normalizedFieldMap.put(fieldName , currentNormalizedField);
		}
		
		/*
		 * 遍历数据List
		 */
		for(int i = 0 ; i < objectStringList.size() ; i++) {
			String[] objectStringArray = objectStringList.get(i).split(",");
			
			/*
			 * 遍历每条数据字符串中的每个字段，
			 * 更新当前NormalizedField的最大值和最小值
			 */
			for(int j = 0 ; j < objectStringArray.length ; j++) {
				/*
				 * 当前字段分析器
				 */
				NormalizedField currentNormalizedField = normalizedFieldMap.get(fieldNames[j]);
				Double currentVal = new Double(objectStringArray[j].trim());
				if(currentNormalizedField.getActualHigh() < currentVal) currentNormalizedField.setActualHigh(currentVal);
				if(currentNormalizedField.getActualLow() > currentVal) currentNormalizedField.setActualLow(currentVal);
			}
		}
		
		
		for(int i = 0 ; i < objectStringList.size() ; i++) {
			String[] objectStringArray = objectStringList.get(i).split(",");
			
			/*
			 * 本条数据输入样本数据数组
			 */
			double[] currentInputDataArray = new double[fieldNames.length - targetColumnLength];
			
			/*
			 * 本条数据预测输出样本数据数组
			 */
			double[] currentIdealOutputArray = new double[targetColumnLength];
			
			/*
			 * 获取标准化后的训练数据
			 */
			for(int j = 0 ; j < objectStringArray.length ; j++) {
				NormalizedField currentNormalizedField = normalizedFieldMap.get(fieldNames[j]);
				
				/*
				 * 判断当前数据是输入还是预测输出
				 * 本条数据列下标小于全部数据列数量-输出数据列数量
				 * 
				 * 2019-11-03更改，不标准化输入数据
				 */
//				if(j < (fieldNames.length - targetColumnLength)) currentInputDataArray[j] = currentNormalizedField.normalize(new Double(objectStringArray[j].trim()));
				if(j < (fieldNames.length - targetColumnLength)) currentInputDataArray[j] = new Double(objectStringArray[j].trim());
				else currentIdealOutputArray[j - fieldNames.length + targetColumnLength] = currentNormalizedField.normalize(new Double(objectStringArray[j].trim()));
				
			}
			
			/*
			 * 将本条数据添加到汇总数组中
			 */
			inputArray[i] = currentInputDataArray;
			idealOutputArray[i] = currentIdealOutputArray;
		}
		
		
		/*
		 * 2019-11-03更改，不标准化输入数据，因此只输出标准化后的数据源即可
		 */
//		Map<Map<String , NormalizedField> , MLDataSet> resultMap = new HashMap<>();
//		resultMap.put(normalizedFieldMap , new BasicMLDataSet(inputArray , idealOutputArray));
		
		return new BasicMLDataSet(inputArray , idealOutputArray);
	}
	
	
	
	
	
	/**
	 * 2、将算法保存到文件
	 * @param filePath
	 * @param trainedNetwork
	 */
	public static void saveAlgorithm(String filePath , BasicNetwork trainedNetwork) {
		File algorithmFile = new File(filePath);
		
		/*
		 * 先删除当前存在的算法文件
		 */
		if(algorithmFile.exists()) algorithmFile.delete();
		
		EncogDirectoryPersistence.saveObject(algorithmFile , trainedNetwork);
	}
	
	
	/**
	 * 3、从文件加载算法
	 * @param filePath
	 * @return
	 */
	public static BasicNetwork loadAlgorithm(String filePath) {
		return (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(filePath));
	}
	
	/**
	 * 4、将股票价格信息List转换为字符串List
	 * @param priceInfoList
	 * @return
	 */
	public static List<String> transPriceInfoToString(List<TabStockPriceInfo> priceInfoList){
		List<String> resultList = new ArrayList<>();
		for(TabStockPriceInfo priceInfo : priceInfoList) {
			String[] priceDateArray = priceInfo.getStockPriceDate().split("-");
			String result = priceDateArray[1] + "," + priceDateArray[2] + "," + priceInfo.getStockPriceStartPrice() + "," + priceInfo.getStockPriceEndPrice();
			resultList.add(result);
		}
		return resultList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 神经网络返回单个数据时判断是否符合预期
	 * @param idealOutput
	 * @param realOutput
	 * @param limit
	 * @return
	 */
	@Deprecated
	public static boolean check(BasicMLData idealOutput , BasicMLData realOutput , double limit) {
		double idealOutputDouble = idealOutput.getData(0);
		double realOutputDouble = realOutput.getData(0);
		double checkFlag = 0;
		
		if(idealOutputDouble >=0 && realOutputDouble >= 0) {
			/*
			 * 二者都大于等于0
			 */
			if(idealOutputDouble > realOutputDouble) checkFlag = idealOutputDouble - realOutputDouble;
			else if(idealOutputDouble < realOutputDouble) checkFlag = realOutputDouble - idealOutputDouble;
			else return true;
			if(checkFlag / idealOutputDouble + realOutputDouble < limit) return true;
		}else if(idealOutputDouble >= 0 && realOutputDouble < 0) {
			/*
			 * 预计输出大于等于0，实际输出小于0
			 */
			checkFlag = idealOutputDouble - realOutputDouble;
			if(checkFlag / idealOutputDouble - realOutputDouble < limit) return true;
		}else if(idealOutputDouble < 0 && realOutputDouble >= 0) {
			/*
			 * 预计输出小于0，实际输出大于等于0
			 */
			checkFlag = realOutputDouble - idealOutputDouble;
			if(checkFlag / realOutputDouble - idealOutputDouble < limit) return true;
		}else {
			/*
			 * 二者都小于0
			 */
			if(idealOutputDouble > realOutputDouble) checkFlag = idealOutputDouble + realOutputDouble;
			else if(idealOutputDouble < realOutputDouble) checkFlag = realOutputDouble + idealOutputDouble;
			else return true;
			if(checkFlag / -(idealOutputDouble - realOutputDouble) < limit) return true;
		}
		
		return false;
	}
	
	/**
	 *
	 * @param filePath 标准化文件路径，会同时生成raw文件，在使用前需要先创建MLConstants.RAW_DATA_FILE_PATH对应文件夹
	 * @param objectStringList
	 * @param inputSize
	 * @param idealOutputSize
	 * @param headers
	 * @return
	 */
	@Deprecated
	public static CSVNeuralDataSet dataAnalyze(String filePath , List<String> objectStringList , int inputSize , int idealOutputSize , boolean headers){
		String dataFilePath = parseRawData(filePath , objectStringList);
		return new CSVNeuralDataSet(dataFilePath , inputSize , idealOutputSize , headers);
	}
	
	
	
	/**
	 * 将数据实体写入到CSV文件中
	 * 
	 * 在调用前需要先在系统中创建MLConstants.RAW_DATA_FILE_PATH对应文件夹
	 * 
	 * @param filePath 文件名称，创建后的文件名称会添加4raw后缀
	 * @param objectStringList 非标准化输入数据List，数据需要符合CSV文件格式(以,分隔每个属性)
	 * @return 返回创建后的文件全路径
	 */
	private static String writeRawToCSV(String filePath , List<String> objectStringList) {
		/*
		 * 当前非标准化神经元输入文件全路径
		 */
//		String rawDataFilePath = MLConstants.RAW_DATA_FILE_PATH + filePath + MLConstants.RAW_DATA_FILE_NAME_SUFFIX + MLConstants.DATA_FILE_TYPE;
		String rawDataFilePath = "";
		
		/*
		 * 文件对象
		 */
		File rawDataFile = new File(rawDataFilePath);
		
		/*
		 * 创建文件
		 */
		if(!rawDataFile.exists()) {
			try {
				rawDataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("创建新文件失败");
				return null;
			}
		}else {
			System.err.println("当前文件已存在！");
			return null;
		}
		
		try(FileOutputStream fileOutputStream = new FileOutputStream(rawDataFilePath);
				OutputStreamWriter outputStreamRader = new OutputStreamWriter(fileOutputStream , "UTF-8");
				BufferedWriter bufferedReader = new BufferedWriter(outputStreamRader)){
			for(String objectString : objectStringList) bufferedReader.write(objectString + System.lineSeparator());
		}catch(IOException e) {
			e.printStackTrace();
			System.err.println("向新文件写入数据失败");
			return null;
		}
		
		System.out.println("创建了新数据文件：" + rawDataFilePath);
		return rawDataFilePath;
	}
	
	
	/**
	 * 将传入的Java对象数据格式化为(-1,1)区间的数据，并保存在对应的目录中
	 * 
	 * 在使用前需要创建RAW_DATA_FILE_PATH、DATA_FILE_PATH两个目录
	 * 
	 * @param filePath 目标文件名称
	 * @param objectStringList 非标准化数据List，第一列包含表头
	 */
	private static String parseRawData(String filePath , List<String> objectStringList) {
		/*
		 * 非标准化数据文件
		 */
		File rawDataFile = new File(writeRawToCSV(filePath , objectStringList));
		
		/*
		 * 标准化数据文件路径
		 */
//		String dataFilePath = MLConstants.DATA_FILE_PATH + filePath + MLConstants.DATA_FILE_TYPE;
		String dataFilePath = "";
		try {
			/*
			 * 实例化Encog Analyst Script脚本运行器
			 */
			EncogAnalyst analyst = new EncogAnalyst();
			
			/*
			 * 实例化CSV分析器
			 */
			AnalystWizard wizard = new AnalystWizard(analyst);
			
			/*
			 * 执行Encog Analyst Script，分析非标准化数据文件
			 */
			wizard.wizard(rawDataFile , true , AnalystFileFormat.DECPNT_COMMA);
			
			/*
			 * 对当前CSV文件进行规范化处理
			 */
			final AnalystNormalizeCSV normalizer = new AnalystNormalizeCSV();
			normalizer.analyze(rawDataFile , true , CSVFormat.ENGLISH , analyst);
			normalizer.setProduceOutputHeaders(true);
			normalizer.normalize(new File(dataFilePath));
		}finally {
			/*
			 * 删除非标准化数据文件
			 */
			rawDataFile.delete();
		}
		
		return dataFilePath;
	}
}
