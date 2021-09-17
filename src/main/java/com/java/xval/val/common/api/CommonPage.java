package com.java.xval.val.common.api;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 分页数据封装类
 */

@Data
@ApiModel(value = "分页对象", description = "分页细信息")
public class CommonPage<T> {

    @ApiModelProperty(value = "当前页数")
    private Integer pageNum;

    @ApiModelProperty(value = "当前数量")
    private Integer pageSize;

    private Integer totalPage;
    private Long total;

    @ApiModelProperty(value = "对象")
    private List<T> list;

    /**
     * 将MyBatis Plus 分页结果转化为通用结果
     */
    public static <T> CommonPage<T> restPage(Page<T> pageResult) {
        CommonPage<T> result = new CommonPage<>();
        result.setPageNum(Convert.toInt(pageResult.getCurrent()));
        result.setPageSize(Convert.toInt(pageResult.getSize()));
        result.setTotal(pageResult.getTotal());
        result.setTotalPage(Convert.toInt(pageResult.getTotal() / pageResult.getSize() + 1));
        result.setList(pageResult.getRecords());
        return result;
    }

}
