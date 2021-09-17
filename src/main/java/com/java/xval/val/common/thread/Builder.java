package com.java.xval.val.common.thread;

import java.io.Serializable;

/**
 * <构造者模式接口定义>
 *
 * @author fangyang
 * @create 2020-09-02
 * @since 1.0.0
 */
public interface Builder<T> extends Serializable {

    /**
     * 构建
     *
     * @return 被构建的对象
     */
    T build();

}
