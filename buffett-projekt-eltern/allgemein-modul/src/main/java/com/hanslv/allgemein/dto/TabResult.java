package com.hanslv.allgemein.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 筛选结果表						tab_result
 * 股票ID							stock_id										BIGINT								FOREIGN KEY(fk_result_stock)				PRIMARY KEY
 * 筛选时间							date											VARCHAR(10)
 * F1								f1												DECIMAL(5,4)
 * 预测最高价						forcast_max										DECIMAL(18,2)
 * 预测最低价						forcast_min										DECIMAL(18,2)
 *
 * @author hanslv
 */
@Data
@ApiModel(value = "筛选结果表", description = "最终筛选，需要进行人工选择的股票")
public class TabResult {
    @ApiModelProperty(value = "股票ID", name = "stockId", required = true, position = 0)
    private Integer stockId;
    @ApiModelProperty(value = "筛选时间", name = "date", required = true, position = 1)
    private String date;
    @ApiModelProperty(value = "f1", name = "f1", required = true, position = 5)
    private BigDecimal f1;
    @ApiModelProperty(value = "预测最高价", name = "forcastMax", required = true, position = 6)
    private BigDecimal forcastMax;
    @ApiModelProperty(value = "预测最低价", name = "forcastMin", required = true, position = 7)
    private BigDecimal forcastMin;
}
