package com.hanslv.maschinelles.lernen.neural.network.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.allgemein.dto.TabResult;
import com.hanslv.allgemein.dto.TabStockInfo;
import com.hanslv.allgemein.dto.TabStockPriceInfo;
import com.hanslv.maschinelles.lernen.constants.NeuralNetworkConstants;
import com.hanslv.maschinelles.lernen.neural.network.DeepLearning4jStockNNTrainer;
import com.hanslv.maschinelles.lernen.repository.TabResultRepository;
import com.hanslv.maschinelles.lernen.repository.TabStockInfoRepository;
import com.hanslv.maschinelles.lernen.repository.TabStockPriceInfoRepository;

/**
 * 股票神经网络训练
 * 
 * -----------------------------------------
 * 1、dl4j从指定ID开始训练全部股票日期-价格模型					public void dl4jTrainStockNN(Integer stockId)
 * -----------------------------------------
 * @author hanslv
 *
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
	@Autowired
	private TabStockPriceInfoRepository priceInfoMapper;
	
	/*
	 * 训练数据总数量=(训练数据数量+测试数据数量)*单批次数据量+测试数据数量*单批次数据量*初步计算循环次数
	 */
	int mainListSize = (NeuralNetworkConstants.trainDataSize + NeuralNetworkConstants.testDataSize) * NeuralNetworkConstants.singleBatchSize + NeuralNetworkConstants.testDataSize * NeuralNetworkConstants.singleBatchSize * NeuralNetworkConstants.inPlanTrainCount;
	
	/**
	 * 1、dl4j从指定ID开始训练全部股票日期-价格模型
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
//		LocalDate currentDate = LocalDate.now();
		LocalDate currentDate = LocalDate.parse("2019-11-08");
		
		/*
		 * 对全部股票进行初步筛选预测，对初步预测通过的股票再进行最终筛选并存储筛选结果表tab_result
		 */
		for(TabStockInfo stockInfo : stockInfoList) {
			/*
			 * 跳到请求指定的开始股票ID
			 */
			if(stockInfo.getStockId().compareTo(stockId) < 0) continue;
			
			logger.info("正在计算股票：" + stockInfo.getStockCode());
			
			/*
			 * 获取全部训练数据，判断数据量是否正确
			 */
			List<TabStockPriceInfo> priceInfoMainList = priceInfoMapper.getTrainAndTestDataDL4j(stockInfo.getStockId() , mainListSize , currentDate.toString());
			if(priceInfoMainList.size() != mainListSize) continue;
			
			/*
			 * 判断当前结果是否已存在
			 */
			if(resultMapper.selectByIdAndDate(stockInfo.getStockId() , currentDate.toString()) > 0) {
				NeuralNetworkConstants.inPlanMainCounter = 0;
				NeuralNetworkConstants.inPlanGoalCounter = 0;
				continue;
			}
			
			
			
			/*
			 * 初步筛选
			 */
			for(int i = 0 ; i < NeuralNetworkConstants.inPlanTrainCount ; i++) {
				/*
				 * 初步计算List起始位置=当前已进行的初步计算次数*测试数据数量*单批次数据量
				 */
				int startIndex = NeuralNetworkConstants.testDataSize * NeuralNetworkConstants.singleBatchSize * i;
				/*
				 * 初步计算List结束位置=(训练数据数量+测试数据数量)*单批次数据量+(当前已进行的初步计算次数+测试数据量*)单批次数据量
				 */
				int endIndex = (NeuralNetworkConstants.testDataSize + NeuralNetworkConstants.trainDataSize) * NeuralNetworkConstants.singleBatchSize + (NeuralNetworkConstants.testDataSize + i) * NeuralNetworkConstants.singleBatchSize;
				
				List<TabStockPriceInfo> priceInfoList = new ArrayList<>();
				for(int j = startIndex ; j < endIndex ; j++) priceInfoList.add(priceInfoMainList.get(j));
				
				dl4jStockNNTrainer.train(priceInfoList , true);
			}
			
			BigDecimal inPlanScore = (NeuralNetworkConstants.inPlanGoalCounter == 0 ? new BigDecimal(0) : new BigDecimal(NeuralNetworkConstants.inPlanGoalCounter).divide(new BigDecimal(NeuralNetworkConstants.inPlanMainCounter) , 2 , BigDecimal.ROUND_HALF_UP));

			/*
			 * 初步筛选通过
			 */
			if(inPlanScore.compareTo(new BigDecimal(NeuralNetworkConstants.inPlanGoalScore)) >= 0) {
				/*
				 * 执行预测
				 */
				List<TabStockPriceInfo> priceInfoList = new ArrayList<>();
				int endIndex = (NeuralNetworkConstants.testDataSize + NeuralNetworkConstants.trainDataSize) * NeuralNetworkConstants.singleBatchSize;
				for(int j = 0 ; j < endIndex ; j++) priceInfoList.add(priceInfoMainList.get(j));
				
				Map<Boolean , double[]> resultMap = dl4jStockNNTrainer.train(priceInfoList , false);
				double[] forcastResult = null;
				if((forcastResult = resultMap.get(true)) != null) {
					TabResult result = new TabResult();
					result.setStockId(stockInfo.getStockId());
					result.setDate(currentDate.toString());
					result.setSuggestBuyPrice(String.valueOf(forcastResult[1]));
					result.setSuggestSellPrice(String.valueOf(forcastResult[0]));
					BigDecimal profit = new BigDecimal(forcastResult[0] - forcastResult[1]).divide(new BigDecimal(forcastResult[0]) , 3 , BigDecimal.ROUND_HALF_UP);
					result.setSuggestRate(profit.toString());
					resultMapper.insert(result);
				}
			}
			
			/*
			 * 复位计分器
			 */
			NeuralNetworkConstants.inPlanGoalCounter = 0;
			NeuralNetworkConstants.inPlanMainCounter = 0;
			
			/*
			 * 每次运算完毕后休眠
			 */
			try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {}
		}
		logger.info("---------------------------------------计算完成---------------------------------------");
	}
	
	/**
	 * 2、计算全部计算结果的准确率
	 */
	public void successRateCalculate() {
		/*
		 * 获取全部成功为0的结果记录
		 */
		List<TabResult> zeroResultList = resultMapper.selectAllZeroResult();
		
		for(TabResult zeroResult : zeroResultList) {
			 /*
			  * 根据当前结果日期获取其后5天的最高价、最低价
			  */
			 List<TabStockPriceInfo> checkDataList = priceInfoMapper.getPriceInfoByIdAndAfterDateAndCount(zeroResult.getStockId() , zeroResult.getDate() , 5);
			 
			 /*
			  * 判断数据量是否达标
			  */
			 if(checkDataList.size() < 5) continue;
			   
			 /*
			  * 获取后五天的最高价、最低价
			  */
			 BigDecimal[] checkHighAndLow = getMaxAndLow(checkDataList);
			 
			 if(doSuccessCheck(zeroResult , checkHighAndLow)) zeroResult.setSuccess(true);
			 else zeroResult.setSuccess(false);
			   
			 resultMapper.updateSuccess(zeroResult);
		}
		logger.info("成功率更新完毕");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	  * 获取股票价格中的最大值和最小值
	  * @param priceInfoList
	  * @return
	  */
	private BigDecimal[] getMaxAndLow(List<TabStockPriceInfo> priceInfoList) {
		BigDecimal[] maxAndLowArray = new BigDecimal[2];
		BigDecimal maxBuffer = new BigDecimal(0);
		BigDecimal minBuffer = new BigDecimal(0);
		
		for(TabStockPriceInfo priceInfo : priceInfoList) {
			BigDecimal currentMax = priceInfo.getStockPriceHighestPrice();
			BigDecimal currentMin = priceInfo.getStockPriceLowestPrice();
	   
			if(maxBuffer.compareTo(new BigDecimal(0)) == 0 || maxBuffer.compareTo(currentMax) < 0) maxBuffer = currentMax;
			if(minBuffer.compareTo(new BigDecimal(0)) == 0 || minBuffer.compareTo(currentMin) > 0) minBuffer = currentMin;
		}
		maxAndLowArray[1] = minBuffer;
		maxAndLowArray[0] = maxBuffer;
		return maxAndLowArray;
	}
	 
	 /**
	  * 判断预测的最高价、最低价是否符合实际值
	  * @param zeroResult
	  * @param checkMaxAndLow
	  * @return
	  */
	private boolean doSuccessCheck(TabResult zeroResult , BigDecimal[] checkMaxAndLow) {
		BigDecimal zeroHigh = new BigDecimal(zeroResult.getSuggestSellPrice());
		BigDecimal zeroLow = new BigDecimal(zeroResult.getSuggestBuyPrice());
		if(checkMaxAndLow[0].compareTo(zeroHigh) < 0 || checkMaxAndLow[1].compareTo(zeroLow) > 0) return false;
		return true;
	}
}
