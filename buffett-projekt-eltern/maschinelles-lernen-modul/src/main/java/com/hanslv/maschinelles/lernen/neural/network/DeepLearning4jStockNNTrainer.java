package com.hanslv.maschinelles.lernen.neural.network;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jboss.logging.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hanslv.allgemein.dto.TabResult;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.util.DataUtil;

/**
 * 训练DeepLearning4j构建的股票神经网络
 * <p>
 * 波段选股SQL：
 * DELETE FROM tab_stock_label WHERE sort_id = (SELECT sort_id FROM tab_stock_sort WHERE sort_name = '波段');
 * DELETE FROM tab_stock_sort WHERE sort_name = '波段';
 * INSERT INTO tab_stock_sort (sort_name , sort_code) VALUES ('波段' , '-');
 * SELECT * FROM tab_stock_sort WHERE sort_name = '波段';
 * SELECT * FROM tab_stock_info WHERE stock_code IN ('');
 * INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
 * (2257 , 1683);
 *
 * @author hanslv
 */
@Component
public class DeepLearning4jStockNNTrainer {
    Logger logger = Logger.getLogger(DeepLearning4jStockNNTrainer.class);

    @Autowired
    private DataUtil dataUtil;

    /*
     * 记录当前股票当前日期每个Epoch的分值和结果
     */
    private static Map<Double, TabResult> scoreMap = new HashMap<>();

    /**
     * 训练LSTM股票模型
     *
     * @param priceInfoList
     * @param idInPlanTest
     */
    public TabResult train(Integer stockId, String endDate) {
        /*
         * 清空上次计算结果
         */
        scoreMap.clear();

        /*
         * 获取训练数据、预测数据
         */
        DataSetIterator[] dataSetIterators = null;
        try {
            dataSetIterators = dataUtil.getSourceData(stockId, endDate);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * 判空
         */
        if (dataSetIterators == null) return null;

        /*
         * 数据标准化处理
         */
        dataUtil.normalize(dataSetIterators);

        /*
         * 获取LSTM神经网络
         */
        MultiLayerNetwork lstmNetwork = DeepLearning4jStockNNBuilder.build();

        /*
         * 训练和预测数据集
         */
        DataSetIterator trainDataSetIterator = dataSetIterators[0];
        DataSetIterator forcastDataSetIterator = dataSetIterators[1];

        /*
         * 拟合模型，并获取最佳结果
         */
        Evaluation eval = null;
        for (int i = 0; i < NeuralNetworkConstants.epoch; i++) {
            while (trainDataSetIterator.hasNext()) {
                DataSet trainDataSet = trainDataSetIterator.next();
                lstmNetwork.fit(trainDataSet);
            }
            eval = new Evaluation(NeuralNetworkConstants.idealOutputSize);

            /*
             * 使用部分预测数据集对模型进行评估
             */
            int testCounter = 0;
            while (forcastDataSetIterator.hasNext()) {
                testCounter++;
                DataSet testData = forcastDataSetIterator.next();
                INDArray features = testData.getFeatures();
                INDArray labels = testData.getLabels();
                INDArray predicted = lstmNetwork.output(features, true);

                eval.evalTimeSeries(labels, predicted);
                if (testCounter == NeuralNetworkConstants.forcastDataSize - 1) break;
            }

            /*
             * 当前F1分值
             */
            double currentF1 = eval.f1();

            /*
             * 重置训练、预测数据集
             */
            trainDataSetIterator.reset();
            forcastDataSetIterator.reset();

            /*
             * 当F1大于等于阈值时执行预测
             */
            if (currentF1 >= NeuralNetworkConstants.f1MinLimit) {
                /*
                 * 执行测试并将测试结果记录到scoreMap中
                 */
                TabResult currentResult = doForcast(lstmNetwork, dataSetIterators);
                scoreMap.put(currentF1, currentResult);
            }
        }

        /*
         * 对scoreMap中的结果进行排序
         */
        TabResult result = getFinalResult(scoreMap);
        result.setDate(endDate);
        result.setStockId(stockId);

        System.out.println(result);
        return result;
    }


    /**
     * 执行预测并返回当前预测结果
     *
     * @param lstmNetwork
     * @param dataSetIterators
     * @return
     */
    private TabResult doForcast(MultiLayerNetwork lstmNetwork, DataSetIterator[] dataSetIterators) {
        DataSetIterator trainDataSetIterator = dataSetIterators[0];
        DataSetIterator forcastDataSetIterator = dataSetIterators[1];
        DataNormalization normalizer = (DataNormalization) trainDataSetIterator.getPreProcessor();

        /*
         * 执行预测
         */
        TabResult result = new TabResult();
        while (trainDataSetIterator.hasNext()) {
            DataSet trainData = trainDataSetIterator.next();
            lstmNetwork.rnnTimeStep(trainData.getFeatures());
        }
        INDArray predictedResult = null;
        DataSet testData = null;
        while (forcastDataSetIterator.hasNext()) {
            testData = forcastDataSetIterator.next();
            predictedResult = lstmNetwork.rnnTimeStep(testData.getFeatures());
        }

        /*
         * 反标准化结果
         */
        normalizer.revertLabels(predictedResult);
        double[] predictedResultDouble = predictedResult.data().asDouble();

        result.setForcastMax(new BigDecimal(String.valueOf(predictedResultDouble[0])).setScale(2, BigDecimal.ROUND_HALF_DOWN));
        result.setForcastMin(new BigDecimal(String.valueOf(predictedResultDouble[1])).setScale(2, BigDecimal.ROUND_HALF_DOWN));

        /*
         * 预测结束后复位LSTM和数据Iterator
         */
        lstmNetwork.rnnClearPreviousState();
        trainDataSetIterator.reset();
        forcastDataSetIterator.reset();
        return result;
    }


    /**
     * 对结果集进行排序并获取最终结果
     *
     * @param scoreMap
     * @return
     */
    private TabResult getFinalResult(Map<Double, TabResult> scoreMap) {
        if (scoreMap.size() == 0) return null;
        Set<Double> keySet = scoreMap.keySet();
        Object[] keyArray = keySet.toArray();
        Arrays.sort(keyArray);

        for (Object key : keyArray) {
            Double keyDouble = Double.parseDouble(key.toString());
            System.err.println("key = " + keyDouble + "，value = " + scoreMap.get(key));
        }

        BigDecimal index = new BigDecimal(keyArray.length).divide(new BigDecimal(2), 0, BigDecimal.ROUND_HALF_DOWN);
        Double f1 = Double.parseDouble(keyArray[index.intValue()].toString());
        TabResult result = scoreMap.get(f1);
        result.setF1(new BigDecimal(String.valueOf(String.valueOf(f1))).setScale(4, BigDecimal.ROUND_HALF_UP));
        return result;
    }
}
