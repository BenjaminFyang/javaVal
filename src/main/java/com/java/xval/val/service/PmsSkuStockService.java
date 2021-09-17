package com.java.xval.val.service;

import com.java.xval.val.model.PmsSkuStock;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * sku的库存 服务类
 * </p>
 *
 * @author fangyang
 * @since 2021-09-16
 */
public interface PmsSkuStockService extends IService<PmsSkuStock> {

    void addPmsSkuStock(PmsSkuStock pmsSkuStock);

}
