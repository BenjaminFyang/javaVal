package com.java.xval.val.common.constraints;


import com.java.xval.val.common.constraints.validator.ValueOfEnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
public @interface ValueOfEnum {
    /**
     * @return class containing enum values to which this String should match
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * @return the error message template
     */
    String message() default "must be any of enum {enumClass}";

    /**
     * @return 默认取方法名
     */
    String enumMethod() default "getMessage";

    /**
     * @return the groups the constraint belongs to
     */
    Class<?>[] groups() default {};

    /**
     * @return the payload associated to the constraint
     */
    Class<? extends Payload>[] payload() default {};
}