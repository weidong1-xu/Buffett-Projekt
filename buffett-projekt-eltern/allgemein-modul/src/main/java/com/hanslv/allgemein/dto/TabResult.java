package com.hanslv.allgemein.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 筛选结果表						tab_result
 * 股票ID							stock_id										BIGINT								FOREIGN KEY(fk_result_stock)				PRIMARY KEY
 * 筛选时间							date											VARCHAR(10)																		PRIMARY KEY
 * 建议买入价格						suggest_buy_price								VARCHAR(255)
 * 是否成功							success											BOOLEAN								DEFAULT false
 * @author hanslv
 *
 */
@Data
@ApiModel(value="筛选结果表" , description="最终筛选，需要进行人工选择的股票")
public class TabResult {
	@ApiModelProperty(value="股票ID" , name="stockId" , required=true , position=0)
	private Integer stockId;
	@ApiModelProperty(value="筛选时间" , name="date" , required=true , position=1)
	private String date;
	@ApiModelProperty(value="建议买入价格" , name="suggest_buy_price" , required=true , position=2)
	private String suggestBuyPrice;
	@ApiModelProperty(value="最终是否成功" , name="success" , required=true , position=3)
	private boolean success;
}
