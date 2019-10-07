package com.hanslv.stock.selector.algorithm.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.algorithm.starter.AlgorithmServiceStarter;
import com.hanslv.stock.selector.algorithm.util.AlgorithmMessageTransUtil;

/**
 * 测试从Kafka中接收股票价格信息
 * @author hanslv
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AlgorithmServiceStarter.class)
public class TestReceivePriceInfoMessageFromTopic {
	@Autowired
	private AlgorithmMessageTransUtil messageTransUtil;
	
	
	/**
	 * 接收信息
	 */
	@Test
	public void receiveMessage() {
		messageTransUtil.getPriceInfoFromKafka();
		
		try {
			TimeUnit.SECONDS.sleep(10 * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
