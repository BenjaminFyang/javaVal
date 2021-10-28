package com.java.xval.val.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author fangyang
 * @since 2021-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("transaction_log")
@ApiModel(value = "TransactionLog对象", description = "")
public class TransactionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "事务ID")
    private String id;

    @ApiModelProperty(value = "业务标识")
    private String business;

    @ApiModelProperty(value = "对应业务表中的主键")
    private String foreignKey;

    public TransactionLog() {
    }
}
