package com.java.xval.val.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;


/**
 * sku的库存
 *
 * @author fangyang
 * @since 2021-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_sku_stock")
@ApiModel(value = "PmsSkuStock对象", description = "sku的库存")
public class PmsSkuStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    @ApiModelProperty(value = "sku编码")
    @NotBlank(message = "sku编码编码不能为空")
    private String skuCode;

    @NotNull(message = "付款金额不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "付款金额不能小于0")
    @Digits(integer = 4, fraction = 2, message = "付款金额必须小于{integer}位数且不能超过{fraction}位小数")
    private BigDecimal price;

    @PositiveOrZero(message = "库存不能小于0")
    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "预警库存")
    @Positive(message = "预警库存必须大于0")
    private Integer lowStock;

    @ApiModelProperty(value = "销售属性1")
    private String sp1;

    private String sp2;

    private String sp3;

    @ApiModelProperty(value = "展示图片")
    private String pic;

    @ApiModelProperty(value = "销量")
    @PositiveOrZero(message = "库存不能为负数")
    private Integer sale;

    @ApiModelProperty(value = "单品促销价格")
    @DecimalMin(value = "0", message = "单品促销价格必须大于0")
    private BigDecimal promotionPrice;

    @ApiModelProperty(value = "锁定库存")
    @Min(value = 0, message = "锁定库存必须大于0")
    private Integer lockStock;

}
