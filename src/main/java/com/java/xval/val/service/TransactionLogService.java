package com.java.xval.val.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.java.xval.val.model.TransactionLog;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author fangyang
 * @since 2021-10-25
 */
public interface TransactionLogService extends IService<TransactionLog> {

    String get(String id);

}
