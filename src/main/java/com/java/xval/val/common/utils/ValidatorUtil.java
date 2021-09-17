package com.java.xval.val.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.stereotype.Component;

import javax.validation.Validation;
import javax.validation.Validator;


@Component
@Slf4j
public class ValidatorUtil {

    public static Validator getValidator() {
        // 使用HibernateValidator
        return Validation.byProvider(HibernateValidator.class).configure()
                // 快速失败（即：第一个参数校验失败就返回错误信息，而不是校验所有的参数，并一次性返回所有的错误信息）
                .failFast(true)
                .buildValidatorFactory().getValidator();

    }
}
