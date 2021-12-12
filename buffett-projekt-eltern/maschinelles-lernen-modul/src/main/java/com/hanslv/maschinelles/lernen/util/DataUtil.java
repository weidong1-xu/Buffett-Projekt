package com.hanslv.maschinelles.lernen.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
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
 *
 * @author hanslv
 */
@Component
public class DataUtil {
    @Autowired
    private TabStockPriceInfoRepository priceInfoMapper;

    /**
     * 1、获取训练源数据
     *
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public DataSetIterator[] getSourceData(Integer stockId, String date) throws IOException, InterruptedException {
        boolean createFileResult = createDatas(stockId, date);
        /*
         * 判空
         */
        if (!createFileResult) return null;

        /*
         * 从CSV文件读取数据
         */
        SequenceRecordReader trainDataLabelReader = new CSVSequenceRecordReader(0, ",");//指定跳过的行数和分隔符
        trainDataLabelReader.initialize(new NumberedFileInputSplit(NeuralNetworkConstants.trainDataLabelFilePathPrefix + "%d" + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH, 0, NeuralNetworkConstants.trainDataSize - 1));
        SequenceRecordReader trainDataFeaturesReader = new CSVSequenceRecordReader(0, ",");//指定跳过的行数和分隔符
        trainDataFeaturesReader.initialize(new NumberedFileInputSplit(NeuralNetworkConstants.trainDataFeaturesFilePathPrefix + "%d" + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH, 0, NeuralNetworkConstants.trainDataSize - 1));

        SequenceRecordReader testDataLabelReader = new CSVSequenceRecordReader(0, ",");//指定跳过的行数和分隔符
        testDataLabelReader.initialize(new NumberedFileInputSplit(NeuralNetworkConstants.forcastDataLabelFilePathPrefix + "%d" + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH, 0, NeuralNetworkConstants.forcastDataSize - 2));
        SequenceRecordReader testDataFeaturesReader = new CSVSequenceRecordReader(0, ",");//指定跳过的行数和分隔符
        testDataFeaturesReader.initialize(new NumberedFileInputSplit(NeuralNetworkConstants.forcastDataFeaturesFilePathPrefix + "%d" + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH, 0, NeuralNetworkConstants.forcastDataSize - 2));
        //定义单时间步长数据量、是否为回归模型
        DataSetIterator trainDataIterator = new SequenceRecordReaderDataSetIterator(trainDataFeaturesReader, trainDataLabelReader, NeuralNetworkConstants.batchSize, -1, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_START);
        DataSetIterator testDataIterator = new SequenceRecordReaderDataSetIterator(testDataFeaturesReader, testDataLabelReader, NeuralNetworkConstants.batchSize, -1, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_START);

        return new DataSetIterator[]{trainDataIterator, testDataIterator};
    }

    /**
     * 2、数据标准化
     *
     * @param trainDataSet
     * @return
     */
    public void normalize(DataSetIterator dataSetIterators[]) {
        DataNormalization normalizer = new NormalizerMinMaxScaler(-1, 1);
        normalizer.fitLabel(true);
        normalizer.fit(dataSetIterators[0]);
        normalizer.fit(dataSetIterators[1]);
        dataSetIterators[0].setPreProcessor(normalizer);
        dataSetIterators[1].setPreProcessor(normalizer);
    }


    /**
     * 3、将日期向前或后推进limit个数据长度
     *
     * @param stockId
     * @param currentDate
     * @param limit
     * @param forwardOrBackward true-日期向前移动，false-日期向后移动
     * @return
     */
    public String changeDate(Integer stockId, String currentDate, int count, boolean forwardOrBackward) {
        String resultDate = "";
        if (forwardOrBackward) {
            for (TabStockPriceInfo priceInfo : priceInfoMapper.changeDateForward(stockId, currentDate, count))
                resultDate = priceInfo.getStockPriceDate();
        } else {
            for (TabStockPriceInfo priceInfo : priceInfoMapper.changeDateBackward(stockId, currentDate, count))
                resultDate = priceInfo.getStockPriceDate();
        }
        return resultDate;
    }


    /**
     * 生成数据文件
     *
     * @param inputStart
     * @param stepLong
     * @param filePrefix
     * @throws IOException
     */
    private boolean createDatas(Integer stockId, String date) throws IOException {
        /*
         * 获取全部数据
         * 注：不包括date当周数据
         */
        List<String> sourceDataList = getSourceData(stockId, date, NeuralNetworkConstants.trainDataSize + NeuralNetworkConstants.forcastDataSize, NeuralNetworkConstants.singleTimeLength);
        if (sourceDataList.size() != NeuralNetworkConstants.trainDataSize + NeuralNetworkConstants.forcastDataSize - 1)
            return false;

        /*
         * 拆分为训练数据、测试数据
         */
        List<String> trainDataList = new ArrayList<>();
        for (int i = 0; i < NeuralNetworkConstants.trainDataSize; i++) trainDataList.add(sourceDataList.get(i));
        List<String> testDataList = new ArrayList<>();
        for (int i = NeuralNetworkConstants.trainDataSize; i < sourceDataList.size(); i++)
            testDataList.add(sourceDataList.get(i));

        try {
            writeToFile(NeuralNetworkConstants.trainDataLabelFilePathPrefix, NeuralNetworkConstants.trainDataFeaturesFilePathPrefix, trainDataList);
            writeToFile(NeuralNetworkConstants.forcastDataLabelFilePathPrefix, NeuralNetworkConstants.forcastDataFeaturesFilePathPrefix, testDataList);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 将数据写入对应文件
     *
     * @param labelFilePathPrefix
     * @param featuresFilePathPrefix
     * @param dataList
     * @throws IOException
     */
    private void writeToFile(String labelFilePathPrefix, String featuresFilePathPrefix, List<String> dataList) throws IOException {
        int stepLong = dataList.size();//当前步长
        /*
         * 将数据写入到文件中
         */
        for (int i = 0; i < stepLong; i++) {
            /*
             * 创建新文件
             */
            File labelDataFile = new File(labelFilePathPrefix + i + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH);
            if (labelDataFile.exists()) labelDataFile.delete();
            labelDataFile.createNewFile();
            File featuresDataFile = new File(featuresFilePathPrefix + i + NeuralNetworkConstants.DATA_FILE_SUFFIX_PATH);
            if (featuresDataFile.exists()) featuresDataFile.delete();
            featuresDataFile.createNewFile();

            String[] currentData = dataList.get(i).split(",");//当前条数据
            StringBuffer labelData = new StringBuffer();//输出数据
            StringBuffer featuresData = new StringBuffer();//输入数据
            for (int j = 0; j < currentData.length; j++) {
                /*
                 * 输入数据
                 */
                if (j < NeuralNetworkConstants.inputSize) {
                    if (j == NeuralNetworkConstants.inputSize - 1) featuresData.append(currentData[j]);
                    else featuresData.append(currentData[j]).append(",");
                }
                /*
                 * 实际输出数据
                 */
                else {
                    if (j == currentData.length - 1) labelData.append(currentData[j]);
                    else labelData.append(currentData[j]).append(",");
                }
            }

            /*
             * 将数据分别写入Labels和Features
             */
            try (RandomAccessFile labelRandomAccessFile = new RandomAccessFile(labelDataFile, "rw");
                 FileChannel labelDataFileChannel = labelRandomAccessFile.getChannel();
                 RandomAccessFile featuresRandomAccessFile = new RandomAccessFile(featuresDataFile, "rw");
                 FileChannel featuresDataFileChannel = featuresRandomAccessFile.getChannel();) {
                ByteBuffer labelDataBuffer = ByteBuffer.wrap(labelData.toString().getBytes());
                labelDataFileChannel.write(labelDataBuffer);

                ByteBuffer FeaturesDataBuffer = ByteBuffer.wrap(featuresData.toString().getBytes());
                featuresDataFileChannel.write(FeaturesDataBuffer);
            }
        }
    }


    /**
     * 获取训练数据List
     *
     * @param stockId
     * @param date
     * @return
     */
    private List<String> getSourceData(Integer stockId, String date, int stepLong, int singleTimeLength) {
        /*
         * 获取标准数据
         */
        List<TabStockPriceInfo> rawDataList = priceInfoMapper.getStockPriceInfoList(stockId, stepLong * NeuralNetworkConstants.singleTimeLength, date);
        BigDecimal max = null;//最大值
        BigDecimal min = null;//最小值
        BigDecimal startPrice = null;//开盘价
        BigDecimal endPrice = null;//收盘价
        BigDecimal turnoverRate = BigDecimal.ZERO;//换手率
        int counterA = 0;
        List<String> bufferList = new ArrayList<>();
        for (TabStockPriceInfo rawData : rawDataList) {
            BigDecimal currentMax = rawData.getStockPriceHighestPrice();
            BigDecimal currentMin = rawData.getStockPriceLowestPrice();
            BigDecimal currentStartPrice = rawData.getStockPriceStartPrice();
            BigDecimal currentEndPrice = rawData.getStockPriceEndPrice();
            BigDecimal currentTurnoverRate = rawData.getStockPriceTurnoverRate();
            counterA++;

            if (max == null || max.compareTo(currentMax) < 0) max = currentMax;
            if (min == null || min.compareTo(currentMin) > 0) min = currentMin;
            if (counterA == 1) startPrice = currentStartPrice;
            if (counterA == singleTimeLength) endPrice = currentEndPrice;
            turnoverRate = turnoverRate.add(currentTurnoverRate);
            if (counterA == singleTimeLength) {
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
        for (int i = 0; i < bufferList.size(); i++) {
            if (i + 1 < bufferList.size()) {
                String[] nextValArray = bufferList.get(i + 1).split(",");
                String nextMax = nextValArray[0];
                String nextMin = nextValArray[1];
                String sourceData = bufferList.get(i) + "," + nextMax + "," + nextMin;
                sourceDataList.add(sourceData);
            }
        }
        return sourceDataList;
    }
}
