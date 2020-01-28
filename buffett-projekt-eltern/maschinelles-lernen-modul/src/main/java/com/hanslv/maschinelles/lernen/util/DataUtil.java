package com.hanslv.maschinelles.lernen.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.allgemein.dto.TabStockPriceInfo;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.repository.TabStockPriceInfoRepository;

/**
 * 数据处理类
 * -----------------------------------------------
 * 1、获取训练源数据											public DataSetIterator[] getSourceData(Integer stockId , String date) throws IOException, InterruptedException
 * 2、数据标准化												public DataNormalization normalize(DataSetIterator dataSetIterators[])
 * 3、将日期向前或后推进limit个数据长度						public String changeDate(Integer stockId , String currentDate , int count , boolean forwardOrBackward)
 * -----------------------------------------------
 * @author hanslv
 *
 */
@Component
public class DataUtil {
	@Autowired
	private TabStockPriceInfoRepository priceInfoMapper;
	
	/**
	 * 1、获取训练源数据
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public DataSetIterator[] getSourceData(Integer stockId , String date){
		DataSetIterator trainDataIterator = null;
		DataSetIterator testDataIterator = null;
		try {
			String trainEndDate = changeDate(stockId , date , NeuralNetworkConstants.singleTimeLength , true);//将日期向前
			boolean resultA = createDatas(stockId , trainEndDate , NeuralNetworkConstants.trainDataSize , NeuralNetworkConstants.trainDataFilePathPrefix , false);//创建训练数据集文件
			boolean resultB = createDatas(stockId , date , NeuralNetworkConstants.forcastDataSize , NeuralNetworkConstants.forcastDataFilePathPrefix , true);//创建测试数据集文件
			
			if(!resultA || !resultB) return null;
			
			/*
			 * 从CSV文件读取数据
			 */
			SequenceRecordReader trainDataReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
			trainDataReader.initialize(new NumberedFileInputSplit(NeuralNetworkConstants.trainDataFilePathPrefix + "%d" + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH , 0, NeuralNetworkConstants.trainDataSize - 1));
			SequenceRecordReader testDataReader = new CSVSequenceRecordReader(0 , ",");//指定跳过的行数和分隔符
			testDataReader.initialize(new NumberedFileInputSplit(NeuralNetworkConstants.forcastDataFilePathPrefix + "%d" + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH , 0, NeuralNetworkConstants.forcastDataSize - 1));
			//定义单时间步长数据量、是否为回归模型
			trainDataIterator = new SequenceRecordReaderDataSetIterator(trainDataReader , NeuralNetworkConstants.batchSize , 2 , NeuralNetworkConstants.inputSize , true);
			testDataIterator = new SequenceRecordReaderDataSetIterator(testDataReader , NeuralNetworkConstants.batchSize , 2 , NeuralNetworkConstants.inputSize , true);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return new DataSetIterator[] {trainDataIterator , testDataIterator};
	}
	
	/**
	 * 2、数据标准化
	 * @param trainDataSet
	 * @return
	 */
	public DataNormalization normalize(DataSetIterator dataSetIterators[]) {
		DataNormalization normalizer = new NormalizerMinMaxScaler(-1 , 1);
		normalizer.fitLabel(true);
		normalizer.fit(dataSetIterators[0]);
		normalizer.fit(dataSetIterators[1]);
		dataSetIterators[0].setPreProcessor(normalizer);
		dataSetIterators[1].setPreProcessor(normalizer);
		return normalizer;
	}
	
	
	/**
	 * 3、将日期向前或后推进limit个数据长度
	 * @param stockId
	 * @param currentDate
	 * @param limit
	 * @param forwardOrBackward true-日期向前移动，false-日期向后移动
	 * @return
	 */
	public String changeDate(Integer stockId , String currentDate , int count , boolean forwardOrBackward) {
		String resultDate = "";
		if(forwardOrBackward) {
			for(TabStockPriceInfo priceInfo : priceInfoMapper.changeDateForward(stockId , currentDate , count)) resultDate = priceInfo.getStockPriceDate();
		}else {
			for(TabStockPriceInfo priceInfo : priceInfoMapper.changeDateBackward(stockId , currentDate , count)) resultDate = priceInfo.getStockPriceDate();
		}
		return resultDate;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	/**
	 * 生成数据文件
	 * @param inputStart
	 * @param stepLong
	 * @param filePrefix
	 * @throws IOException
	 */
	private boolean createDatas(Integer stockId , String date , int stepLong , String filePrefix , boolean isForcast) throws IOException {
		/*
		 * 将数据集合写入到文件
		 */
		List<String> sourceDataList = getParsedData(stockId , date , stepLong , NeuralNetworkConstants.singleTimeLength , isForcast);
		if(sourceDataList.size() != stepLong - 1) return false;
		for(int i = 0 ; i < stepLong - 1 ; i++) {
			/*
			 * 创建当前时间步长文件
			 */
			File dataFile = new File(filePrefix + i + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH);
			if(dataFile.exists()) dataFile.delete();
			dataFile.createNewFile();
			
			/*
			 * 将指定数量的数据写入到单个文件
			 */
			try(RandomAccessFile randomAccessFile = new RandomAccessFile(dataFile , "rw");
					FileChannel dataFileChannel = randomAccessFile.getChannel()){
				for(int j = 0 ; j < NeuralNetworkConstants.batchSize ; j++) {
					String data = sourceDataList.get(i * NeuralNetworkConstants.batchSize + j) + System.lineSeparator();
					ByteBuffer dataBuffer = ByteBuffer.wrap(data.getBytes());
					dataFileChannel.write(dataBuffer);
				}
			}catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	
	
	

	/**
	 * 获取格式化后的股票信息数据
	 * @param stockId
	 * @param date
	 * @param stepLong
	 * @param singleTimeLength
	 * @param isForcast 当前是否为测试数据
	 * @return
	 */
	private List<String> getParsedData(Integer stockId , String date , int stepLong , int singleTimeLength , boolean isForcast){
		/*
		 * 获取标准数据
		 */
		List<TabStockPriceInfo> rawStockPriceInfoList = priceInfoMapper.getStockPriceInfoList(stockId , stepLong * singleTimeLength , date);
		Collections.reverse(rawStockPriceInfoList);//将数据翻转成顺序
		BigDecimal max = null;//最大值
		BigDecimal min = null;//最小值
		BigDecimal startPrice = null;//开盘价
		BigDecimal endPrice = null;//收盘价
		BigDecimal turnoverRate = BigDecimal.ZERO;//换手率
		int counterA = 0;
		List<String> bufferList = new ArrayList<>();
		for(TabStockPriceInfo rawData : rawStockPriceInfoList) {
			BigDecimal currentMax = rawData.getStockPriceHighestPrice();//最大值
			BigDecimal currentMin = rawData.getStockPriceLowestPrice();//最小值
			BigDecimal currentStartPrice = rawData.getStockPriceStartPrice();//开盘价
			BigDecimal currentEndPrice = rawData.getStockPriceEndPrice();//收盘价
			BigDecimal currentTurnoverRate = rawData.getStockPriceTurnoverRate();//换手率
			counterA++;
			
			if(max == null || max.compareTo(currentMax) < 0) max = currentMax;
			if(min == null || min.compareTo(currentMin) > 0) min = currentMin;
			if(counterA == 1) startPrice = currentStartPrice;
			if(counterA == singleTimeLength) endPrice = currentEndPrice;
			turnoverRate = turnoverRate.add(currentTurnoverRate);
			if(counterA == singleTimeLength) {
				String sourceData = 
						max + "," +
						min + "," + 
						startPrice + "," + 
						endPrice + "," + 
						turnoverRate;
				bufferList.add(sourceData);
				max = null;
				min = null;
				startPrice = null;
				endPrice = null;
				turnoverRate = BigDecimal.ZERO;
				counterA = 0;
			}
		}
		
		/*
		 * 拼接实际结果
		 */
		List<String> sourceDataList = new ArrayList<>();
		for(int i = 0 ; i < bufferList.size() ; i++) {
			String[] nextValArray = null;
			if(i + 1 < bufferList.size()) nextValArray = bufferList.get(i + 1).split(",");
			else {
				/*
				 * 如果当前是预测数据，则使用当前数据补位
				 */
				if(isForcast) nextValArray = bufferList.get(i).split(",");
			}
			if(nextValArray != null) {
				String nextMax = nextValArray[0];
				String nextMin = nextValArray[1];
				String sourceData = bufferList.get(i) + "," + nextMax + "," + nextMin;
				sourceDataList.add(sourceData);
			}
		}
		return sourceDataList;
	}
}
