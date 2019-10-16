package com.hanslv.stock.selector.algorithm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hanslv.stock.selector.algorithm.services.AlgorithmLogicService;
import com.hanslv.stock.selector.algorithm.services.AlgorithmResultService;
import com.hanslv.stock.selector.algorithm.services.PriceInfoConsumerService;
/**
 * --------------------------------------------
 * 1、从Kafka接收接收PriceInfo消息											public void runPriceInfoConsumer()
 * 2、更新当前全部算法结果													public void updateAlgorithmResult()
 * 3、执行全部算法逻辑														public void algorithmLogic()
 * --------------------------------------------
 * @author hanslv
 *
 */
@Controller
@RequestMapping("/algorithm-service")
public class RestfulController {
	@Autowired
	private PriceInfoConsumerService priceInfoService;
	
	@Autowired
	private AlgorithmResultService algorithmResult;
	
	@Autowired
	private AlgorithmLogicService algorithmLogic;
	
	
	/**
	 * 1、从Kafka接收接收PriceInfo消息
	 */
	@GetMapping("/stock-price-consumer")
	public void runPriceInfoConsumer() {
		priceInfoService.runConsumer();
	}
	
	
	/**
	 * 2、更新当前全部算法结果
	 */
	@GetMapping("/algorithm-result")
	public void updateAlgorithmResult() {
		algorithmResult.updateAlgorithmResult();
	}
	
	
	/**
	 * 3、执行全部算法逻辑
	 */
	@GetMapping("/algorithm-logic")
	public void algorithmLogic() {
		algorithmLogic.runAllAlgorithmLogic();
	}
}
