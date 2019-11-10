package com.hanslv.stock.selector.crawler.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.hanslv.stock.selector.commons.dto.TabStockIndexMacd;
import com.hanslv.stock.selector.commons.dto.TabStockPriceInfo;

/**
 * 股票指标工具类
 * 
 * --------------------------------------------------
 * 1、计算所给股票信息对应的MACD系数							public static List<TabStockIndexMacd> macdIndexCalculation(List<TabStockPriceInfo> stockPriceInfoList)
 * --------------------------------------------------
 * @author hanslv
 *
 */
public class StockIndexUtil {
	
	/**
	 * 1、计算所给股票信息对应的MACD系数
	 * @param stockPriceInfoList
	 * @return
	 */
	public static List<TabStockIndexMacd> macdIndexCalculation(List<TabStockPriceInfo> stockPriceInfoList){
		return macdCalculation(deaCalculation(diffCalculation(stockPriceInfoList)));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 计算给定股票集合的EMA12
	 * @param stockEndPriceList
	 * @return
	 */
	private static List<BigDecimal> ema12Calculation(List<TabStockPriceInfo> stockPriceInfoList){
		List<BigDecimal> ema12ResultList = new ArrayList<>();
		
		for(int i  = 0 ; i < stockPriceInfoList.size() ; i++) {
			if(i == 0) ema12ResultList.add(new BigDecimal(0));
			else {
				/*
				 * 上一个交易日的ema12数值
				 */
				BigDecimal lastEma12Result = ema12ResultList.get(i - 1);
				
				BigDecimal currentEma12Result = lastEma12Result.multiply(new BigDecimal(11)).divide(new BigDecimal(13), BigDecimal.ROUND_HALF_EVEN).add(
						stockPriceInfoList.get(i).getStockPriceEndPrice().multiply(new BigDecimal(2)).divide(new BigDecimal(13), BigDecimal.ROUND_HALF_EVEN)).setScale(2);
				
				ema12ResultList.add(currentEma12Result);
			}
		}
		return ema12ResultList;
	}
	
	/**
	 * 计算给定股票集合的EMA26
	 * @param stockEndPriceList
	 * @return
	 */
	private static List<BigDecimal> ema26Calculation(List<TabStockPriceInfo> stockPriceInfoList){
		List<BigDecimal> ema26ResultList = new ArrayList<>();
		
		for(int i  = 0 ; i < stockPriceInfoList.size() ; i++) {
			if(i == 0) ema26ResultList.add(new BigDecimal(0));
			else {
				/*
				 * 上一个交易日的ema12数值
				 */
				BigDecimal lastEma12Result = ema26ResultList.get(i - 1);
				
				BigDecimal currentEma12Result = lastEma12Result.multiply(new BigDecimal(25)).divide(new BigDecimal(27), BigDecimal.ROUND_HALF_EVEN).add(
						stockPriceInfoList.get(i).getStockPriceEndPrice().multiply(new BigDecimal(2)).divide(new BigDecimal(27), BigDecimal.ROUND_HALF_EVEN)).setScale(2);
				
				ema26ResultList.add(currentEma12Result);
			}
		}
		return ema26ResultList;
	}
	
	/**
	 * 计算给定股票集合的DIFF
	 * @param stockEndPriceList
	 * @return
	 */
	private static List<TabStockIndexMacd> diffCalculation(List<TabStockPriceInfo> stockPriceInfoList){
		/*
		 * 计算EMA12
		 */
		List<BigDecimal> ema12ResultList = ema12Calculation(stockPriceInfoList);
		
		/*
		 * 计算EMA26
		 */
		List<BigDecimal> ema26ResultList = ema26Calculation(stockPriceInfoList);
		
		
		List<TabStockIndexMacd> difResultList = new ArrayList<>();
		for(int i = 0 ; i < stockPriceInfoList.size() ; i++) {
			/*
			 * 获取当前股票价格信息日期对应的EMA12
			 */
			BigDecimal currentEma12 = ema12ResultList.get(i);
			
			/*
			 * 获取当前股票价格信息日期对应的EMA26
			 */
			BigDecimal currentEma26 = ema26ResultList.get(i);
			
			
			/*
			 * 计算当前股票价格信息日期对应的DIFF
			 */
			BigDecimal currentDif = currentEma12.subtract(currentEma26);
			
			/*
			 * 将信息放入List
			 */
			TabStockIndexMacd currentMacdInfo = new TabStockIndexMacd();
			currentMacdInfo.setDate(stockPriceInfoList.get(i).getStockPriceDate());
			currentMacdInfo.setStockId(stockPriceInfoList.get(i).getStockId());
			currentMacdInfo.setDiff(currentDif);
			difResultList.add(currentMacdInfo);
		}
		return difResultList;
	}
	
	
	/**
	 * 计算给定股票集合的DEA
	 * @param stockDifList
	 * @return
	 */
	private static List<TabStockIndexMacd> deaCalculation(List<TabStockIndexMacd> macdInfoList){
		
		for(int i = 0 ; i < macdInfoList.size() ; i++) {
			
			if(i == 0) macdInfoList.get(i).setDea(new BigDecimal(0));
			else 
				macdInfoList.get(i).setDea(new BigDecimal(0.2).multiply(macdInfoList.get(i).getDiff()).setScale(2 , BigDecimal.ROUND_HALF_EVEN).add(
						new BigDecimal(0.8).multiply(macdInfoList.get(i - 1).getDea()).setScale(2 , BigDecimal.ROUND_HALF_EVEN)));
		}
		return macdInfoList;
	}
	
	
	/**
	 * 计算给定股票集合的MACD
	 * @param stockDifList
	 * @param stockDeaList
	 * @return
	 */
	private static List<TabStockIndexMacd> macdCalculation(List<TabStockIndexMacd> macdInfoList){
		for(int i = 0 ; i < macdInfoList.size() ; i++) {
			if(i == 0) macdInfoList.get(i).setMacd(new BigDecimal(0));
			else {
				BigDecimal currentDif = macdInfoList.get(i).getDiff();
				BigDecimal currentDea = macdInfoList.get(i).getDea();
				
				macdInfoList.get(i).setMacd(currentDif.subtract(currentDea).multiply(new BigDecimal(2)));
			}
		}
		return macdInfoList;
	}
}
