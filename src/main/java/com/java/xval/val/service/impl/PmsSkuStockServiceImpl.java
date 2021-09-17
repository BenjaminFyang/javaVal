package com.java.xval.val.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.xval.val.common.utils.ValidatorUtil;
import com.java.xval.val.mapper.PmsSkuStockMapper;
import com.java.xval.val.model.PmsSkuStock;
import com.java.xval.val.service.PmsSkuStockService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.*;
import java.util.Set;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author fangyang
 * @since 2021-09-16
 */
@Service
public class PmsSkuStockServiceImpl extends ServiceImpl<PmsSkuStockMapper, PmsSkuStock> implements PmsSkuStockService {

    @Override
    public void addPmsSkuStock(PmsSkuStock pmsSkuStock) {

        Set<ConstraintViolation<PmsSkuStock>> validate = ValidatorUtil.getValidator().validate(pmsSkuStock);

        if (!CollectionUtils.isEmpty(validate)) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<PmsSkuStock> constraintViolation : validate) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, validate);
        }

        save(pmsSkuStock);
    }


}
