package com.hanslv.stock.selector.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 价格-日期ML预测结果表5日		tab_price_date_ml_result_five_days
 * 股票ID						stock_id										BIGINT								FOREIGN KEY (fk_ml_stock_info)						PRIMARY KEY
 * 运行日期						run_date										VARCHAR(10)	
 * 当前收盘价					end_price_current								VARCHAR(50)																			PRIMARY KEY
 * 第一天收盘价					end_price_a										VARCHAR(50)
 * 第二天收盘价					end_price_b										VARCHAR(50)
 * 第三天收盘价					end_price_c										VARCHAR(50)
 * 第四天收盘价					end_price_d										VARCHAR(50)
 * 第五天收盘价					end_price_e										VARCHAR(50)
 * @author hanslv
 *
 */
@Data
public class TabPriceDateMLResultFiveDays {
	@ApiModelProperty(value="股票ID" , name="stockId" , required=true , position=0)
	private Integer stockId;
	@ApiModelProperty(value="运行日期" , name="runDate" , required=true , position=1)
	private String runDate;
	@ApiModelProperty(value="当前收盘价" , name="endPriceCurrent" , required=true , position=2)
	private String endPriceCurrent;
	@ApiModelProperty(value="第一天收盘价" , name="endPriceA" , required=true , position=3)
	private String endPriceA;
	@ApiModelProperty(value="第二天收盘价" , name="endPriceB" , required=true , position=4)
	private String endPriceB;
	@ApiModelProperty(value="第三天收盘价" , name="endPriceC" , required=true , position=5)
	private String endPriceC;
	@ApiModelProperty(value="第四天收盘价" , name="endPriceD" , required=true , position=6)
	private String endPriceD;
	@ApiModelProperty(value="第五天收盘价" , name="endPriceE" , required=true , position=7)
	private String endPriceE;
}
