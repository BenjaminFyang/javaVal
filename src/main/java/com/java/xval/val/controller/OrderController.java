package com.java.xval.val.controller;

import com.java.xval.val.common.api.CommonResult;
import com.java.xval.val.model.request.OrderDTO;
import com.java.xval.val.model.request.validation.AddParam;
import com.java.xval.val.model.request.validation.UpdateParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 * 〈购物账号模块〉
 *
 * @author fangyang
 * @since 2021-09-16
 */
@Slf4j
@Api(tags = "【后端】订单模块")
@RestController
@RequestMapping(value = "/admin/order")
public class OrderController {

    @PostMapping(value = "/add")
    @ApiOperation(httpMethod = "POST", value = "添加", response = Boolean.class)
    public CommonResult<String> addEmergency(@ApiParam(required = true) @RequestBody @Validated({AddParam.class}) OrderDTO orderDTO) {
        return CommonResult.success("添加订单成功");
    }

    @PostMapping(value = "/update")
    @ApiOperation(httpMethod = "POST", value = "修改", response = Boolean.class)
    public CommonResult<String> updateEmergency(@ApiParam(required = true) @RequestBody @Validated({UpdateParam.class}) OrderDTO orderDTO) {
        return CommonResult.success("修改订单成功");
    }
}