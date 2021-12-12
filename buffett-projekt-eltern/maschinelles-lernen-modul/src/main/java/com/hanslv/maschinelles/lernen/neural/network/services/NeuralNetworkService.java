package com.hanslv.maschinelles.lernen.neural.network.services;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.allgemein.dto.TabResult;
import com.hanslv.allgemein.dto.TabStockInfo;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.neural.network.DeepLearning4jStockNNTrainer;
import com.hanslv.maschinelles.lernen.repository.TabResultRepository;
import com.hanslv.maschinelles.lernen.repository.TabStockInfoRepository;

/**
 * 股票神经网络训练
 * <p>
 * -----------------------------------------
 * 1、dl4j从指定ID开始训练全部股票日期-价格模型					public void dl4jTrainStockNN(Integer stockId)
 * -----------------------------------------
 *
 * @author hanslv
 */
@Service
public class NeuralNetworkService {
    Logger logger = Logger.getLogger(NeuralNetworkService.class);

    @Autowired
    private DeepLearning4jStockNNTrainer dl4jStockNNTrainer;

    @Autowired
    private TabStockInfoRepository tabStockInfoMapper;
    @Autowired
    private TabResultRepository resultMapper;

    /**
     * 1、dl4j从指定ID开始训练全部股票日期-价格模型
     *
     * @param stockId
     */
    public void dl4jTrainStockNN(Integer stockId) {
        /*
         * 获取全部股票
         */
        List<TabStockInfo> stockInfoList = tabStockInfoMapper.selectAllStockInfo();

        /*
         * 当前日期
         */
        LocalDate currentDate = LocalDate.parse(NeuralNetworkConstants.trainDate);

        /*
         * 对全部股票进行初步筛选预测，对初步预测通过的股票再进行最终筛选并存储筛选结果表tab_result
         */
        for (TabStockInfo stockInfo : stockInfoList) {
            /*
             * 跳到请求指定的开始股票ID
             */
            if (stockInfo.getStockId().compareTo(stockId) < 0) continue;

            logger.info("正在计算股票：" + stockInfo.getStockCode());

            /*
             * 判断当前结果是否已存在
             */
            if (resultMapper.selectByIdAndDate(stockInfo.getStockId(), currentDate.toString()) > 0) continue;

            /*
             * 执行预测，并将结果集插入数据库
             */
            TabResult result = dl4jStockNNTrainer.train(stockInfo.getStockId(), currentDate.toString());

            /*
             * 判空
             */
            if (result == null) continue;

            resultMapper.insert(result);

            /*
             * 每次运算完毕后休眠
             */
            try {
                TimeUnit.SECONDS.sleep(NeuralNetworkConstants.sleepSecondCount);
            } catch (InterruptedException e) {
            }
        }
        logger.info("---------------------------------------计算完成---------------------------------------");
    }
}
