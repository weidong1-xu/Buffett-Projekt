package com.hanslv.stock.selector.algorithm.test;

import java.time.LocalDate;

public class TestLocalDate {
	public static void main(String[] args) {
		LocalDate currentDate = LocalDate.now();
		System.out.println(checkLastRunDate(currentDate , "2" , "2019-10-10"));
	}
	
	
	private static boolean checkLastRunDate(LocalDate currentDate , String algorithmDayCount , String currentLastRunDate) {
		Integer currentLastRunDateInt = Integer.parseInt(currentLastRunDate.replaceAll("-", ""));//当前算法最后运行时间
		Integer checkDateInt = Integer.parseInt(currentDate.minusDays(Long.parseLong(algorithmDayCount)).toString().replaceAll("-", ""));//需要对比的时间（当前日期-算法时间区间）
		return currentLastRunDateInt - checkDateInt > 0 ? true : false;
	}
}
