package com.hanslv.allgemein.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 股票分类信息表							tab_stock_sort
 * 分类ID									sort_id										BIGINT					PRIMARY KEY 						AUTO_INCREMENT
 * 分类名称									sort_name									VARCHAR(255)
 * 分类简码									sort_code									VARCHAR(50)
 * @author hanslv
 */
@Data
@ApiModel(value="股票分类信息表" , description="存储股票分类的标签信息")
public class TabStockSort {
	@ApiModelProperty(value="分类ID" , name="sortId" , required=true , position=0)
	private Integer sortId;
	@ApiModelProperty(value="分类名称" , name="sortName" , required=true , position=1)
	private String sortName;
	@ApiModelProperty(value="分类编码" , name="sortCode" , required=true , position=2)
	private String sortCode;
}
