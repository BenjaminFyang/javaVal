package com.java.xval.val.common.constraints.validator;

import com.java.xval.val.common.constraints.ValueOfEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValueOfEnum annotation) {

        Method getMessage;
        List<String> list = new ArrayList<>();
        try {
            getMessage = annotation.enumClass().getMethod(annotation.enumMethod());
            for (Enum<?> en : annotation.enumClass().getEnumConstants()) {
                String invoke = (String) getMessage.invoke(en);
                list.add(invoke);
                acceptedValues = list;
            }
        } catch (Exception e) {
            acceptedValues = null;
        }

    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (acceptedValues == null) {
            return false;
        }

        return acceptedValues.contains(value.toString());
    }
}
