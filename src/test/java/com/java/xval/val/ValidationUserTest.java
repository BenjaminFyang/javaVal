package com.java.xval.val;

import com.java.xval.val.beanvalidation.User;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValidationUserTest {

    private Validator validator;

    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void ifNameIsNull() {

        User user = new User();
        user.setWorking(true);
        user.setAboutMe("me");
        user.setAge(50);
        user.setPrice(new BigDecimal("34563.33"));


        // validate方法来验证我们的 UserBean User对象中定义的约束都将作为Set返回
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            // getMessage方法获取所有违规消息
            System.out.println(violation.getMessage());
        }
        assertFalse(violations.isEmpty());
    }

}
