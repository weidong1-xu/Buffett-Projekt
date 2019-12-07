package com.hanslv.allgemein.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 筛选结果表						tab_result
 * 股票ID							stock_id										BIGINT								FOREIGN KEY(fk_result_stock)				PRIMARY KEY
 * 筛选时间							date											VARCHAR(10)																		PRIMARY KEY
 * 建议买入价格						suggest_buy_price								VARCHAR(255)
 * 建议卖出价格						suggest_sell_price								VARCHAR(255)
 * 建议盈利比率						suggest_rate									VARCHAR(255)
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
	@ApiModelProperty(value="建议买入价格" , name="suggestBuyPrice" , required=true , position=2)
	private String suggestBuyPrice;
	@ApiModelProperty(value="建议卖出价格" , name="suggestSellPrice" , required=true , position=3)
	private String suggestSellPrice;
	@ApiModelProperty(value="建议盈利比率" , name="suggestRate" , required=true , position=5)
	private String suggestRate;
	@ApiModelProperty(value="最终是否成功" , name="success" , required=true , position=6)
	private boolean success;
}
