package com.hanslv.stock.selector.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 算法结果表（分表）				tab_algorithm_result
 * 算法ID							algorithm_id									BIGINT								FOREIGN KEY (fk_algorithm_result_info)				PRIMARY KEY
 * 执行日期							run_date										VARCHAR(10)																				PRIMARY KEY
 * 股票ID							stock_id										BIGINT								FOREIGN KEY (fk_algorithm_result_stockInfo)			PRIMARY KEY
 * 是否成功							is_success										VARCHAR(10)							DEFAULT 'UNKNOWN'  									SUCCESS FAIL UNKNOWN
 * 成功率							success_rate									VARCHAR(10)
 * @author hanslv
 *
 */
@Data
@ApiModel(value="算法结果表" , description="算法结果表")
public class TabAlgorithmResult {
	@ApiModelProperty(value="算法ID" , name="algorithmId" , required=true , position=0)
	private Integer algorithmId;
	
	@ApiModelProperty(value="执行日期" , name="runDate" , required=true , position=1)
	private String runDate;
	
	@ApiModelProperty(value="股票ID" , name="stockId" , required=true , position=2)
	private Integer stockId;
	
	@ApiModelProperty(value="是否成功" , name="isSuccess" , required=true , position=3)
	private String isSuccess;
	
	@ApiModelProperty(value="成功率" , name="successRate" , required=true , position=4)
	private String successRate;
}
