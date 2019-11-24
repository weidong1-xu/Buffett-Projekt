package com.hanslv.stock.machine.learning.neural.network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanslv.stock.machine.learning.neural.network.services.NeuralNetworkService;

/**
 * Controller
 * 
 * -----------------------------------------
 * 1、训练全部股票							public void train(Integer stockId)
 * 2、训练指定股票							public void trainAStock(@PathVariable("stockId")Integer stockId)
 * 3、预测全部股票并存入数据库				public void calculateStock()
 * 4、预测指定股票并将结果输出到控制台		public void calculateStock(@PathVariable("stockId")Integer stockId)
 * 5、获取结果并输出到控制台					public void getResult()
 * 6、dl4j训练全部股票						public void dl4jTrain(@PathVariable("stockId")Integer stockId)
 * 7、dl4j训练指定股票						public void dl4jTrainAStock(@PathVariable("stockId")Integer stockId)
 * -----------------------------------------
 * @author hanslv
 *
 */
@RequestMapping("/nn")
@RestController
public class RestfulController {
	@Autowired
	private NeuralNetworkService nnService;
	
	/**
	 * 1、训练全部股票
	 */
	@GetMapping("/train-all/{stockId}")
	public void train(@PathVariable("stockId")Integer stockId) {
		nnService.trainStockNN(stockId);
	}
	
	/**
	 * 2、训练指定股票
	 * @param stockId
	 */
	@GetMapping("/train/{stockId}")
	public void trainAStock(@PathVariable("stockId")Integer stockId) {
		nnService.trainAStockNN(stockId);
	}
	
	/**
	 * 3、预测全部股票并存入数据库
	 */
	@GetMapping("/calculate-all")
	public void calculateStock() {
		nnService.calculateStock();
	}
	
	/**
	 * 4、预测指定股票并将结果输出到控制台
	 * @param stockId
	 */
	@GetMapping("/calculate/{stockId}")
	public void calculateStock(@PathVariable("stockId")Integer stockId) {
		nnService.calculateStock(stockId);
	}
	
	/**
	 * 5、获取结果并输出到控制台
	 */
	@GetMapping("/result")
	public void getResult() {
		nnService.getResult();
	}
	
	/**
	 * 6、dl4j训练全部股票
	 */
	@GetMapping("/train-all-dl4j/{stockId}")
	public void dl4jTrain(@PathVariable("stockId")Integer stockId) {
		nnService.dl4jTrainStockNN(stockId);
	}
	
	/**
	 * 7、dl4j训练指定股票
	 * @param stockId
	 */
	@GetMapping("/train-dl4j/{stockId}")
	public void dl4jTrainAStock(@PathVariable("stockId")Integer stockId) {
		nnService.dl4jTrainAStockNN(stockId);
	}
}
