package com.hanslv.stock.selector.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 算法信息表						tab_algorithm_info
 * 算法ID							algorithm_id									BIGINT								PRIMARY KEY											AUTO_INCREMENT
 * 算法名称							algorithm_name									VARCHAR(50)
 * 算法介绍							algorithm_comment								VARCHAR(255)
 * 更新日期							update_date										VARCHAR(10)
 * @author hanslv
 *
 */
@Data
@ApiModel(value="算法信息表" , description="算法信息表")
public class TabAlgorithmInfo {
	@ApiModelProperty(value="算法ID" , name="algorithmId" , required=true , position=0)
	private Integer algorithmId;
	
	@ApiModelProperty(value="算法名称" , name="algorithmName" , required=true , position=1)
	private String algorithmName;
	
	@ApiModelProperty(value="算法介绍" , name="algorithmComment" , required=true , position=2)
	private String algorithmComment;
	
	@ApiModelProperty(value="更新日期" , name="updateDate" , required=true , position=3)
	private String updateDate;
}
