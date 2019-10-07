package com.hanslv.stock.selector.algorithm.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hanslv.stock.selector.algorithm.starter.AlgorithmServiceStarter;
import com.hanslv.stock.selector.commons.dto.TabStockInfo;
import com.hanslv.stock.selector.commons.util.RedisUtil;

/**
 * 测试Redis存取设置超时时间等
 * @author hanslv
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AlgorithmServiceStarter.class)
public class TestRedis {
	@Autowired
	private RedisUtil redisUtil;
	
	/**
	 * 测试
	 * @throws InterruptedException 
	 */
	@Test
	public void setKey() throws InterruptedException {
		/*
		 * 设置key value并设置超时时间
		 */
		TabStockInfo key = new TabStockInfo();
		key.setStockName("testStock");
		key.setStockCode("123456");
		String value = "123456,2019-10-07";
		redisUtil.setWithExpire(key, value , 20 , TimeUnit.SECONDS);
		
		/*
		 * 在超时前获取值
		 */
		getKey(key);
		TimeUnit.SECONDS.sleep(20);
		
		
		/*
		 * 在超时后获取值
		 */
		getKey(key);
		
		
		
		TimeUnit.SECONDS.sleep(20);
	}
	
	
	/**
	 * 获取对应的value
	 * @param key
	 */
	private void getKey(Object key) {
		System.out.println("------------------获取到key对应的value：" + redisUtil.get(key) + "------------------");
	}
}
