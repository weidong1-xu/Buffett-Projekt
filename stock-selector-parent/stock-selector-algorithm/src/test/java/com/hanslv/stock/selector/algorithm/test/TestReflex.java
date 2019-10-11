package com.hanslv.stock.selector.algorithm.test;

public class TestReflex {
	public static void main(String[] args) {
		try {
			TestReflex instance = (TestReflex) Class.forName("com.hanslv.stock.selector.algorithm.test.TestReflex2").newInstance();
			instance.test();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public void test() {
		System.out.println("This is test");
	}
}
