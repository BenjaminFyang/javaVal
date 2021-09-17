package com.java.xval.val.beanvalidation;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
public class User {

    @NotNull(message = "名字不能为空")
    private String name;

    @AssertTrue
    private boolean working;

    @Size(min = 10, max = 200, message = "字符数应介于10和200之间（含10和200）")
    private String aboutMe;

    @Min(value = 18, message = "年龄不应少于18岁")
    @Max(value = 150, message = "年龄不应超过150岁")
    private int age;

    @Email(message = "电子邮件应该是有效的")
    private String email;

    private List<@NotBlank(message = "备注说明不能为空") String> preferences;

    @Past(message = "出生年月必须是一个过去的时间")
    private LocalDate dateOfBirth;

    @DecimalMin(value = "0.0", inclusive = false, message = "付款金额不能小于0")
    @Digits(integer = 4, fraction = 2, message = "付款金额必须小于{integer}位数且不能超过{fraction}位小数")
    private BigDecimal price;

}
