package com.hanslv.maschinelles.lernen.neural.network.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanslv.maschinelles.lernen.neural.network.services.NeuralNetworkService;

/**
 * Controller
 * 
 * -----------------------------------------
 * 6、dl4j训练全部股票						public void dl4jTrain(@PathVariable("stockId")Integer stockId)
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
	 * 6、dl4j训练全部股票
	 */
	@GetMapping("/train-all-dl4j/{stockId}")
	public void dl4jTrain(@PathVariable("stockId")Integer stockId) {
		nnService.dl4jTrainStockNN(stockId);
	}
}
