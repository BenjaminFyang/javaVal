package com.java.xval.val.controller;

import com.alibaba.fastjson.JSON;
import com.java.xval.val.common.api.CommonResult;
import com.java.xval.val.model.Order;
import com.java.xval.val.model.request.OrderDTO;
import com.java.xval.val.model.request.validation.AddParam;
import com.java.xval.val.model.request.validation.UpdateParam;
import com.java.xval.val.mq.RocketMQTemplateProducer;
import com.java.xval.val.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @Resource
    private OrderService orderService;

    @Resource
    private RocketMQTemplateProducer rocketMqHelper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

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


    @PostMapping("/createOrder")
    @ApiOperation(httpMethod = "POST", value = "创建订单分布式事务测试", response = Boolean.class)
    public CommonResult<String> createOrder(@RequestBody Order order) throws MQClientException {
        logger.info("接收到订单数据：{}", order);
        orderService.createOrder(order);
        return CommonResult.success("订单创建中");
    }


    @PostMapping("/delayQueue")
    @ApiOperation(httpMethod = "POST", value = "延时队列测试", response = Boolean.class)
    public CommonResult<String> delayQueue(@RequestBody Order order) {
        rocketMqHelper.asyncSendDelay("topic-orderly", MessageBuilder.withPayload(order).build(), 3000, 3);
        return CommonResult.success("延时队列发动成功");
    }


    /**
     * 发送事务消息
     *
     * @return CommonResult
     */
    @PostMapping("/transactionMessage")
    @ApiOperation(httpMethod = "POST", value = "事务消息", response = Boolean.class)
    public CommonResult<String> sendMessageInTransactionMessage(@RequestBody Order order) {
        rocketMqHelper.sendMessageInTransaction("transaction-message", order);
        return CommonResult.success("success：已发送事务消息：message = " + JSON.toJSONString(order));
    }
}