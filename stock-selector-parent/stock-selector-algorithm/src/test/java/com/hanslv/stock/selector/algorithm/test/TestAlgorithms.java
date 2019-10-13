package com.hanslv.stock.selector.algorithm.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.algorithm.AlgorithmLogic;
import com.hanslv.stock.selector.algorithm.starter.AlgorithmServiceStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=AlgorithmServiceStarter.class)
public class TestAlgorithms {
	@Autowired
	private AlgorithmLogic algorithm;
	
	@Test
	public void runAlgorithms() {
		algorithm.runAlgorithm();
		
		while(true) {}
	}
}
