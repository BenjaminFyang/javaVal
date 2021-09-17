package com.java.xval.val.controller;


import com.java.xval.val.common.api.CommonResult;
import com.java.xval.val.model.PmsSkuStock;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * sku的库存 前端控制器
 *
 * @author fangyang
 * @since 2021-09-16
 */
@RestController
@RequestMapping("/val/pmsSkuStock")
public class PmsSkuStockController {

    @PostMapping("/add")
    CommonResult<String> addPmsSkuStock(@RequestBody PmsSkuStock pmsSkuStock) {
        return CommonResult.success("pmsSkuStock is valid");
    }
}

