package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(value = "spu大字段数据传输类")
@Data
public class SpuDetailDTO {

    @ApiModelProperty(value = "spu主键",example = "1")
    @NotNull(message = "spu主键id不能为空",groups = {MingruiOperation.Update.class})
    private Integer spuId;

    @ApiModelProperty(value = "商品描述信息")
    @NotNull(message = "商品描述信息不能为空",groups = {MingruiOperation.Add.class})
    private String description;

    @ApiModelProperty(value = "通用规格参数数据")
    private String genericSpec;

    @ApiModelProperty(value = "特有规格参数及可选值信息，json格式")
    private String specialSpec;

    @ApiModelProperty(value = "包装清单")
    @NotNull(message = "包装清单不能为空",groups = {MingruiOperation.Add.class})
    private String packingList;

    @ApiModelProperty(value = "售后服务")
    @NotNull(message = "售后服务不能为空",groups = {MingruiOperation.Add.class})
    private String afterService;

}
