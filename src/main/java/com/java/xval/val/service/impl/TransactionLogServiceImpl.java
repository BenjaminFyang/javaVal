package com.java.xval.val.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.xval.val.mapper.TransactionLogMapper;
import com.java.xval.val.model.TransactionLog;
import com.java.xval.val.service.TransactionLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author fangyang
 * @since 2021-10-25
 */
@Service
public class TransactionLogServiceImpl extends ServiceImpl<TransactionLogMapper, TransactionLog> implements TransactionLogService {

    @Override
    public String get(String id) {
        TransactionLog transactionLog = getById(id);
        if (transactionLog == null) {
            return null;
        }
        return transactionLog.getId();
    }
}
