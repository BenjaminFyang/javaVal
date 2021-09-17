package com.java.xval.val.model.request;

import com.java.xval.val.model.request.validation.AddParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * <p>
 * 订单子表-->礼品卡
 * </p>
 */
@Data
@ApiModel
public class OrderNextCardDTO {

    @ApiModelProperty(value = "礼品卡id")
    @NotNull(message = "礼品卡id为空", groups = {AddParam.class})
    private Integer cardId;

    @ApiModelProperty(value = "使用金额")
    @DecimalMin(value = "0", message = "使用金额最小为0", groups = {AddParam.class})
    private BigDecimal amount;

}
