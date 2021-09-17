package com.java.xval.val.model.request;

import com.java.xval.val.common.constraints.ValueOfEnum;
import com.java.xval.val.common.enums.OrderWebsiteEnum;
import com.java.xval.val.model.request.validation.AddParam;
import com.java.xval.val.model.request.validation.UpdateParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

/**
 * 订单表
 */
@Data
@ApiModel
public class OrderDTO {

    @ApiModelProperty("id,新增不必传递,修改必须传递")
    @Null(message = "id必须为null或空", groups = {AddParam.class})
    @NotNull(message = "不能为空", groups = {UpdateParam.class})
    private Integer id;

    @ApiModelProperty(value = "购物账号", required = true)
    @NotBlank(message = "购物账号不能为空", groups = {AddParam.class})
    private String account;

    @ApiModelProperty(value = "订单号")
    private String orderNum;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮件格式不正确")
    private String email;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "备注")
    @NotNull(message = "备注不能为空", groups = {UpdateParam.class})
    private String remark;

    @Valid
    @NotNull(message = "礼品卡不能为空", groups = {AddParam.class})
    @ApiModelProperty(value = "礼品卡集合")
    List<OrderNextCardDTO> orderNextCardDTOList;


    /**
     * 下单网站
     */
    @ValueOfEnum(enumClass = OrderWebsiteEnum.class, groups = {AddParam.class}, message = "对应的下单网站不存在")
    private OrderWebsiteEnum orderWebsite;
}
