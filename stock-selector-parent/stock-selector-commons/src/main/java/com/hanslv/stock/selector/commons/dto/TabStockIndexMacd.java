package com.hanslv.stock.selector.commons.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 股票指标-MACD（分表）			tab_stock_index_macd
 * 股票ID						stock_id										BIGINT								FOREIGN KEY (fk_index_macd_stockInfo)				PRIMARY KEY
 * 日期							date											VARCHAR(10)																				PRIMARY KEY
 * DIFF							diff											DECIMAL(2,2)
 * DEA							dea												DECIMAL(2,2)
 * MACD							macd											DECIMAL(2,2)
 * @author hanslv
 *
 */
@Data
public class TabStockIndexMacd {
	@ApiModelProperty(value="股票ID" , name="stockId" , required=true , position=0)
	private Integer stockId;
	@ApiModelProperty(value="日期" , name="date" , required=true , position=1)
	private String date;
	@ApiModelProperty(value="DIFF" , name="diff" , required=true , position=2)
	private BigDecimal diff;
	@ApiModelProperty(value="DEA" , name="dea" , required=true , position=3)
	private BigDecimal dea;
	@ApiModelProperty(value="MACD" , name="macd" , required=true , position=4)
	private BigDecimal macd;
}
