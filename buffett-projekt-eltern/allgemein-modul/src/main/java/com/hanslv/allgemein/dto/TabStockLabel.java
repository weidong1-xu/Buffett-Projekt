package com.hanslv.allgemein.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 股票标签信息表								tab_stock_label
 * 分类ID										sort_id										BIGINT					FOREIGN_KEY fk_label_sort
 * 股票ID										stock_id									BIGINT					FOREIGN_KEY fk_label_stock_info
 * @author hanslv
 */
@Data
@ApiModel(value="股票标签信息表" , description="股票标签信息表，存放股票-标签之间的对应关系")
public class TabStockLabel {
	@ApiModelProperty(value="分类ID" , name="sortId" , required=true , position=0)
	private Integer sortId;
	@ApiModelProperty(value="股票ID" , name="sortId" , required=true , position=1)
	private Integer stockId;
}
