package com.hanslv.stock.selector.algorithm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanslv.stock.selector.algorithm.AlgorithmLogic;

/**
 * 算法逻辑模块Service
 * 
 * -------------------------------------------
 * 1、运行全部算法													public void runAllAlgorithmLogic()
 * -------------------------------------------
 * @author admin
 *
 */
@Service
public class AlgorithmLogicService {
	
	@Autowired
	private AlgorithmLogic algorithmLogic;
	
	/**
	 * 1、运行全部算法
	 */
	public void runAllAlgorithmLogic() {
		algorithmLogic.runAlgorithm();
	}
}
