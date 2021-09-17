package com.java.xval.val.common.exception;


import com.java.xval.val.common.api.IErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义API异常
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiException extends RuntimeException {
    private IErrorCode errorCode;

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }


}
