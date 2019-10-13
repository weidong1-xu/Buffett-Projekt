package com.hanslv.stock.selector.algorithm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 算法0001：圆弧底
 * com.hanslv.stock.selector.algorithm.Algorithm0001
 * @author hanslv
 *
 */
public class Algorithm0001 extends AlgorithmLogic{
	private static final String CURRENT_ALGORITHM_CLASS_NAME = "com.hanslv.stock.selector.algorithm.Algorithm0001";
	private static final int CURRENT_ALGORITHM_ID = 1;
	
	
	Logger logger = Logger.getLogger(Algorithm0001.class);
	
	public static final int DAYCOUNT = 5;
	private final BigDecimal FLOATRANGEUP = new BigDecimal(1.02);//y值上浮百分比，抛物线方程得数需要小于y值与该值积
	private final BigDecimal FLOATRANGEDOWN = new BigDecimal(0.98);//y值下浮百分比，抛物线方程得数需要大于y值与该值积
	private final BigDecimal INDEXA = new BigDecimal(DAYCOUNT).divide(new BigDecimal(2) , 0 , BigDecimal.ROUND_HALF_DOWN);//最后一天价格的x坐标
	private final BigDecimal INDEXB = INDEXA.subtract(new BigDecimal(1));//倒数第二天价格x坐标
	private final BigDecimal INDEXC = INDEXB.subtract(new BigDecimal(1));//倒数第三天价格x坐标
	private final BigDecimal FIRSTPRICEFLOATRANGELIMIT = new BigDecimal(3);//最后一天股价上涨需要大于该百分比
	
	private BigDecimal param1;
	private BigDecimal param2;
	private BigDecimal param3;
	private BigDecimal param4;
	private BigDecimal param5;
	
	@Override
	public String algorithmLogic(String lastRunDate) {
		logger.info("-----------------------正在执行" + CURRENT_ALGORITHM_CLASS_NAME + "-----------------------");
		/*
		 * 获取全部股票信息
		 */
		List<TabStockInfo> stockInfoList = getAllStockInfo();
		
		String currentLastRunDate = lastRunDate;
		
		/*
		 * 执行计算
		 */
		for(TabStockInfo stockInfo : stockInfoList) {
			/*
			 * 获取每只股票指定天数的信息
			 */
			List<TabStockPriceInfo> priceInfoList = getPriceInfos(String.valueOf(stockInfo.getStockId()) , lastRunDate , String.valueOf(DAYCOUNT));
			
			/*
			 * 排除退市的股票
			 */
			if(priceInfoList.size() == 0) continue;
			
			/*
			 * 计算结果
			 */
			if(check(priceInfoList , DAYCOUNT)) {
				/*
				 * 符合则插入到算法结果信息表
				 */
				insertAlgorithmResult(CURRENT_ALGORITHM_ID , priceInfoList , stockInfo.getStockId());
				logger.info("-----------------------" + CURRENT_ALGORITHM_CLASS_NAME + "找到了符合股票：" + stockInfo.getStockId() + "-----------------------");
			}else {
				logger.info(CURRENT_ALGORITHM_CLASS_NAME + "：" + stockInfo.getStockId() + "不符合要求");
			}
			
			/*
			 * 更新当前算法最后执行时间
			 */
			currentLastRunDate = updateAlgorithmUpdateDate(CURRENT_ALGORITHM_CLASS_NAME , priceInfoList);
		}
		
		return currentLastRunDate;
	}
	
	
	/**
	 * 判断当前股票的价格List是否符合当前的抛物线算法
	 * @param priceList
	 * @return
	 */
	private boolean check(List<TabStockPriceInfo> priceList , int size) {
		if(!checkFirstPrice(priceList.get(0))) {return false;}//判断第一天价格
		List<BigDecimal> medianPriceList = getAllMedian(priceList);//获取价格的中位数
		if(!checkFirstPriceAndLastPrice(medianPriceList)) {return false;}//判断第一天价格与最后一天价格的关系是否满足要求
		Map<String , BigDecimal> algorithmMap = getAlgorithm(medianPriceList);
		if(!checkAlgorithm(algorithmMap)) {return false;}
		return checkAllPrice(algorithmMap , medianPriceList);
	}
	
	/**
	 * 判断第一天价格与最后一天价格的关系是否满足要求
	 * @param medianPriceList
	 * @return
	 */
	private boolean checkFirstPriceAndLastPrice(List<BigDecimal> medianPriceList) {
		BigDecimal firstPrice = medianPriceList.get(0);//第一天价格
		BigDecimal lastPrice = medianPriceList.get(medianPriceList.size() - 1);//最后一天价格
		return firstPrice.multiply(FLOATRANGEDOWN).compareTo(lastPrice) <= 0 ? true : false;
	}
	
	/**
	 * 判断第一天价格是否大于浮动范围
	 * @param firstPrice
	 * @return
	 */
	private boolean checkFirstPrice(TabStockPriceInfo firstPrice) {
		String floatRange = firstPrice.getStockPriceAmplitude();
		return new BigDecimal(floatRange.substring(0, floatRange.indexOf("%"))).compareTo(FIRSTPRICEFLOATRANGELIMIT) > 0 ? true : false;
	}
	
	/**
	 * 判断推到出来的公式a是否大于0，当a大于0时开口向上
	 * @param algorithm
	 * @return
	 */
	private boolean checkAlgorithm(Map<String , BigDecimal> algorithm) {
		return algorithm.get("a").compareTo(BigDecimal.ZERO) > 0 ? true : false;
	}
	
	/**
	 * 判断传入的价格list中除了前三个价格以外的价格是否符合要求
	 * @param algorithm 算法Map
	 * @param medianPriceList 价格中位数List
	 * @return
	 */
	private boolean checkAllPrice(Map<String , BigDecimal> algorithm , List<BigDecimal> medianPriceList) {
		BigDecimal index = INDEXC;//当前价格x轴坐标
		for(int i = 3 ; i < medianPriceList.size() ; i++) {
			index = index.subtract(new BigDecimal(1));
			if(!checkPrice(algorithm , medianPriceList.get(i) , index)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断传入的1个价格是否符合算法
	 * y*(y值上浮百分比) > ax^2 + bx + c > y*(y值下浮百分比)
	 * @param algorithm 算法Map
	 * @param medianPrice 价格中位数
	 * @param index 价格的x轴坐标
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkPrice(Map<String , BigDecimal> algorithm , BigDecimal medianPrice , BigDecimal index) {
		BigDecimal algorithmResult = commonAlgorithm(algorithm , index);
		BigDecimal test1 = medianPrice.multiply(FLOATRANGEUP);
		BigDecimal test2 = medianPrice.multiply(FLOATRANGEDOWN);
		if(medianPrice.multiply(FLOATRANGEUP).compareTo(algorithmResult) >= 0 && medianPrice.multiply(FLOATRANGEDOWN).compareTo(algorithmResult) <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 通用算法
	 * ax^2 + bx + c
	 * @param algorithm
	 * @param index
	 * @return
	 */
	private BigDecimal commonAlgorithm(Map<String , BigDecimal> algorithm , BigDecimal index) {
		return algorithm.get("a").multiply(index.multiply(index)).add(algorithm.get("b").multiply(index)).add(algorithm.get("c"));
	}
	
	/**
	 * 计算抛物线方程，返回Map中a代表x^2参数，b代表x参数，c代表常量
	 * @param priceList 包含最后三天数据的中位数list
	 * @return 包含键为a、b、c的HashMap
	 */
	private Map<String , BigDecimal> getAlgorithm(List<BigDecimal> paramList) {
		initParams(paramList);//初始化param
		Map<String , BigDecimal> result = new HashMap<>();
		BigDecimal a = getA(paramList);
		BigDecimal b = getB(paramList , a);
		BigDecimal c = getC(paramList , a , b);
		
		result.put("a", a);
		result.put("b", b);
		result.put("c", c);
		return result;
	}
	
	/**
	 * 初始化param
	 * param1 = (y1 - y2)
	 * param2 = (x1 - x2)
	 * param3 = (x1^2 - x3^2)
	 * param4 = (x1^2 - x2^2)
	 * param5 = (x1 - x3)
	 * @param paramList
	 */
	private void initParams(List<BigDecimal> paramList) {
		param1 = paramList.get(0).subtract(paramList.get(1));
		param2 = INDEXA.subtract(INDEXB);
		param3 = INDEXA.multiply(INDEXA).subtract(INDEXC.multiply(INDEXC));
		param4 = INDEXA.multiply(INDEXA).subtract(INDEXB.multiply(INDEXB));
		param5 = INDEXA.subtract(INDEXC);
	}
	
	/**
	 * 获取抛物线方程中的A
	 * a=(y1 - x1(y1 - y2)/(x1 - x2) - y3 + x3(y1 - y2)/(x1 - x2))/((x1^2 - x3^2) - (x1^2 - x2^2)(x1 - x3)/(x1 - x2))
	 * a=(y1 - x1param1/param2 - y3 + x3param1/param2)/(param3 - param4param5/param2)
	 * @param paramList
	 * @return
	 */
	private BigDecimal getA(List<BigDecimal> paramList) {
		return paramList.get(0).subtract(INDEXA.multiply(param1).divide(param2 , 2 , BigDecimal.ROUND_HALF_UP)).subtract(paramList.get(2)).add(INDEXC.multiply(param1).divide(param2 , 2 , BigDecimal.ROUND_HALF_UP)).divide(param3.subtract(param4.multiply(param5).divide(param2 , 2 , BigDecimal.ROUND_HALF_UP)) , 2 , BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 获取抛物线方程中的B
	 * b=((y1 - y2) - a(x1^2 - x2^2))/(x1 - x2)
	 * b=(param1 - aparam4)/param2
	 * @param paramList
	 * @param a
	 * @return
	 */
	private BigDecimal getB(List<BigDecimal> paramList , BigDecimal a) {
		return param1.subtract(a.multiply(param4)).divide(param2 , 2 , BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 获取抛物线方程中的C
	 * c = y3 - (ax3^2 + bx3)
	 * @param paramList
	 * @param a
	 * @param b
	 * @return
	 */
	private BigDecimal getC(List<BigDecimal> paramList , BigDecimal a , BigDecimal b) {
		return paramList.get(2).subtract(a.multiply(INDEXC.multiply(INDEXC)).add(b.multiply(INDEXC)));
	}
	
	
	/**
	 * 获取全部价格的中位数
	 * @param param
	 * @return
	 */
	private static List<BigDecimal> getAllMedian(List<TabStockPriceInfo> param) {
		List<BigDecimal> medianPriceList = new ArrayList<>();
		for(TabStockPriceInfo price : param) {
			BigDecimal medianPrice = getMedian(price);
			medianPriceList.add(medianPrice);
		}
		return medianPriceList;
	}
	
	/**
	 * 8、获取当天价格的中位数
	 * @return
	 */
	private static BigDecimal getMedian(TabStockPriceInfo param) {
		return param.getStockPriceEndPrice().add(param.getStockPriceStartPrice()).divide(new BigDecimal(2) , 2 , BigDecimal.ROUND_HALF_UP);
	}
}
