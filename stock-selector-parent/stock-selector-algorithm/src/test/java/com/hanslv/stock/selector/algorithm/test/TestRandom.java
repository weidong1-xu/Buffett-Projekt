package com.hanslv.stock.selector.algorithm.test;

import java.util.Random;

public class TestRandom {
	public static void main(String[] args) {
		Random random = new Random();
		System.out.println(500l + random.ints(0 , 100).findFirst().getAsInt());
	}
}
