package com.hanslv.stock.selector.crawler.test;

public class TestDouble {
	public static void main(String[] args) {
		double test = new Double(0.9822);
		double test2 = new Double(0.9821);
//		String testS = test + "";
//		BigDecimal testD = new BigDecimal(testS).setScale(2);
//		System.out.println(String.valueOf(test));
		System.out.println(test < test2);
	}
}
