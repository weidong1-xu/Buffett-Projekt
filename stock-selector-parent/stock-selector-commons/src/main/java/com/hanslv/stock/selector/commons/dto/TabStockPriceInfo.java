package com.hanslv.stock.selector.commons.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 股票价格信息
 * 
 * 股票价格信息表（分表）				tab_stock_price
 * 股票ID							stock_id										BIGINT								FOREIGN KEY (fk_stock_price_info)					PRIMARY KEY
 * 日期								stock_price_date								VARCHAR(10)																				PRIMARY KEY
 * 开盘价							stock_price_start_price							DECIMAL(18,2)
 * 收盘价							stock_price_end_price							DECIMAL(18,2)
 * 最高价							stock_price_highest_price						DECIMAL(18,2)
 * 最低价							stock_price_lowest_price						DECIMAL(18,2)
 * 成交量							stock_price_volume								INT(10)
 * 成交额							stock_price_turnover							INT(10)
 * 振幅								stock_price_amplitude							VARCHAR(6)
 * 换手率							stock_price_turnover_rate						DECIMAL(4,1)
 * @author harrylu
 *
 */
@Data
@ApiModel(value="股票价格信息" , description="股票价格信息表，分表逻辑：拆分为shangzheng、shenzheng并将每个类别拆分为多张表")
public class TabStockPriceInfo {
	@ApiModelProperty(value="股票ID" , name="stockId" , required=true , position=0)
	private Integer stockId;//股票ID
	
	@ApiModelProperty(value="日期" , name="stockPriceDate" , required=true , position=1)
	private String stockPriceDate;//日期
	
	@ApiModelProperty(value="开盘价" , name="stockPriceStartPrice" , required=true , position=2)
	private BigDecimal stockPriceStartPrice;//开盘价
	
	@ApiModelProperty(value="收盘价" , name="stockPriceEndPrice" , required=true , position=3)
	private BigDecimal stockPriceEndPrice;//收盘价
	
	@ApiModelProperty(value="最高价" , name="stockPriceHighestPrice" , required=true , position=4)
	private BigDecimal stockPriceHighestPrice;//最高价
	
	@ApiModelProperty(value="最低价" , name="stockPriceLowestPrice" , required=true , position=5)
	private BigDecimal stockPriceLowestPrice;//最低价
	
	@ApiModelProperty(value="成交量" , name="stockPriceVolume" , required=true , position=6)
	private Integer stockPriceVolume;//成交量
	
	@ApiModelProperty(value="成交额" , name="stockPriceTurnover" , required=true , position=7)
	private Integer stockPriceTurnover;//成交额
	
	@ApiModelProperty(value="振幅" , name="stockPriceAmplitude" , required=true , position=8)
	private String stockPriceAmplitude;//振幅
	
	@ApiModelProperty(value="换手率" , name="stockPriceTurnoverRate" , required=true , position=9)
	private BigDecimal stockPriceTurnoverRate;//换手率
	
}
