package com.hanslv.stock.selector.crawler.test;

import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.hanslv.crawler.starter.CrawlerServiceStarter;
import com.hanslv.stock.selector.crawler.test.constants.RequestUrlConstants;

/**
 * 测试Controller中初始化股票基本信息请求
 * @author harrylu
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=CrawlerServiceStarter.class)
public class TestControllerInitStockInfo {
	Logger logger = Logger.getLogger(TestControllerInitStockInfo.class);
	
	/**
	 * 模拟Mvc对象
	 */
	private MockMvc mvc;
	
	/**
	 *  Web上下文
	 */
	@Autowired
	private WebApplicationContext applicationContext;
	
	/**
	 * 在执行之前初始化MockMvc对象
	 */
	@Before
	public void initMockMvc() {
		mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}
	
	
	/**
	 * 执行测试逻辑
	 */
//	@Transactional(transactionManager = "dataSourceTransactionManager")
	@Test
	public void testInit() {
		try {
			mvc
				.perform(MockMvcRequestBuilders.get(RequestUrlConstants.INIT_STOCKINFO_URL).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		} catch (Exception e) {
			logger.error("测试请求失败！");
			e.printStackTrace();
		}
	}
}
