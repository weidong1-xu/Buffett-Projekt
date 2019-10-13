package com.hanslv.stock.selector.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 股票基本信息表					tab_stock_info
 * 股票ID							stock_id										BIGINT								PRIMARY KEY					AUTO_INCREMENT
 * 股票代码							stock_code										VARCHAR(10)							UNIQUE
 * 股票名称							stock_name										VARCHAR(8)
 * @author harrylu
 *
 */
@Data
@ApiModel(value="股票基本信息表 tab_stock_info" , description="股票基本信息表")
public class TabStockInfo {
	@ApiModelProperty(value="股票ID" , name="stockId" , required=true , position=0)
	private Integer stockId;//股票ID
	
	@ApiModelProperty(value="股票代码" , name="stockCode" , required=true , position=1)
	private String stockCode;//股票代码
	
	@ApiModelProperty(value="股票名称" , name="stockName" , required=true , position=2)
	private String stockName;//股票名称
}
