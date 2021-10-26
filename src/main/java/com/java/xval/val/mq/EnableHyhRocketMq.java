package com.java.xval.val.mq;

import com.java.xval.val.common.config.RocketMqAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启RocketMq注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RocketMqAutoConfiguration.class})
public @interface EnableHyhRocketMq {
}
