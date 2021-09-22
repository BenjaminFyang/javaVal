# SpringBoot 参数校验整理梳理

- [SpringBoot 参数校验整理梳理](#springboot-参数校验整理梳理)
    - [一、前言](#一前言)
    - [二、Java Bean 验证基础](#二java-bean-验证基础)
        - [概述](#概述)
        - [JSR 380](#jsr-380)
        - [依赖、验证API](#依赖验证api)
        - [使用验证注解](#使用验证注解)
        - [常见的约束注解如下](#常见的约束注解如下)
        - [验证注释也可以应用于集合的元素](#验证注释也可以应用于集合的元素)
        - [支持Java 8 中的新**Optional类型**](#支持java-8-中的新optional类型)
        - [程序验证](#程序验证)
        - [总结](#总结)
    - [三、Bean验证中@NotNull、@NotEmpty和@NotBlank约束之间的差异](#三bean验证中notnullnotempty和notblank约束之间的差异)
        - [概述差异](#概述差异)
    - [四、SpringBoot中集成参数校验](#四springboot中集成参数校验)
        - [前言概述](#前言概述)
        - [应用分层](#应用分层)
        - [springBoot Rest验证 开始引入依赖](#springboot-rest验证-开始引入依赖)
        - [定义要参数校验的实体类](#定义要参数校验的实体类)
        - [实现一个REST控制器](#实现一个rest控制器)
        - [@ExceptionHandler](#exceptionhandler)
        - [测试REST控制器](#测试rest控制器)
            - [使用单元测试验证](#使用单元测试验证)
            - [使用 Postmen 来测试REST控制器API](#使用-postmen-来测试rest控制器api)
    - [五、@Valid和@Validated注解的区别](#五valid和validated注解的区别)
        - [@Valid和@Validated注解](#valid和validated注解)
        - [分组验证代码示例](#分组验证代码示例)
        - [使用@Valid注解标记嵌套对象](#使用valid注解标记嵌套对象)
        - [对比总结](#对比总结)
    - [六、枚举类型的验证](#六枚举类型的验证)
        - [验证枚举简介](#验证枚举简介)
        - [验证字符串是否匹配枚举的值](#验证字符串是否匹配枚举的值)
    - [七、总结](#七总结)
        - [回顾](#回顾)
        - [excel导入和关联参数验证](#excel导入和关联参数验证)

## 一、前言

> 目前项目中对于参数验证有各种形式、有的接口参数一个个校验的话就太繁琐了，代码可读性极差。有的用到Spring Boot中自带的验证注解，但是使用的仅仅只是在表层. 所以目前这边做了统一的梳理，方便大家理解和后续的使用。

* 在项目中不管是前端页面表单提交的对象数据和第三方公司进行接口对接又或者是项目中的Excel导入的参数验证，都是需要对接收的数据进行校验（非空、长度、格式等等）。前端js校验可以涵盖大部分的校验指责，但是为了避免用户绕过浏览器，使用http工具直接向后端请求一些违法数据，服务器的数据校验也是必要的，可以防止脏数据落到数据库中。同时后端中比较原始的的写法是使用if一个个进行校验（字段非常多），靠代码对接口参数一个个校验的话就太繁琐了，代码可读性极差。下面主要是说明SprinBoot中如何集成参数校验Validator，以及参数校验的高阶技巧（**自定义校验，分组校验**）。

> 此文是依赖已有代码基础，已经在项目中加入了全局异常校验器。 本地代码已上传[**远程仓库**](https://github.com/BenjaminFyang/javaVal.git) <https://github.com/BenjaminFyang/javaVal.git>

## 二、Java Bean 验证基础

### 概述

* 在项目中使用前，首先需要介绍下标准框架 JSR 380（也称为Bean Validation 2.0）**验证 Java bean 的基础知识**。
* 在大多数应用程序中，验证用户输入是一个非常普遍的要求。而 Java Bean Validation 框架已经成为处理这种逻辑的事实标准。

### JSR 380

* JSR 380是用于bean验证的Java API规范。这确保bean的属性满足特定条件，使用诸如@NotNull、@Min和@Max 之类的注释
* 此版本需要 Java 8或更高版本，并利用Java 8 中添加的新功能，例如类型注释和对Optional和LocalDate等新类型的支持。
* 有关规范的完整信息，请继续阅读[**JSR 380**](https://jcp.org/en/jsr/detail?id=380)

### 依赖、验证API

```xml
<!-- 根据 JSR 380 规范，validation-api依赖项包含标准验证 API -->
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>

<!-- Hibernate Validator 是验证 API 的参考实现 -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.13.Final</version>
</dependency>
```

### 使用验证注解

这里使用一个 Userbean 添加一些简单的验证

```java
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

```

示例中使用的所有注释都是标准的 JSR 注释：

### 常见的约束注解如下

| 验证注解                                 | 验证的数据类型                                                                        | 说明                                                                            |
|--------------------------------------|--------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| @AssertFalse                         | Boolean, boolean                                                                | 验证注解的元素值是false                                                                |
| @AssertTrue                          | Boolean, boolean                                                                | 验证注解的元素值是true                                                                 |
| @NotNull                             | 任意类型                                                                           | 验证注解的元素值不是null                                                                |
| @Null                                | 任意类型                                                                           | 验证注解的元素值是null                                                                 |
| @Min(value=值)                        | BigDecimal，BigInteger, byte, short, int, long，等任何Number或CharSequence（存储的是数字）子类型 | 验证注解的元素值大于等于@Min指定的value值                                                     |
| @Max（value=值）                        | 和@Min要求一样                                                                      | 验证注解的元素值小于等于@Max指定的value值                                                     |
| @DecimalMin(value=值)                 | 和@Min要求一样                                                                      | 验证注解的元素值大于等于@ DecimalMin指定的value值                                             |
| @DecimalMax(value=值)                 | 和@Min要求一样                                                                      | 验证注解的元素值小于等于@ DecimalMax指定的value值                                             |
| @Digits(integer=整数位数, fraction=小数位数) | 和@Min要求一样                                                                      | 验证注解的元素值的整数位数和小数位数上限                                                          |
| @Size(min=下限, max=上限)                | 字符串、Collection、Map、数组等                                                         | 验证注解的元素值的在min和max（包含）指定区间之内，如字符长度、集合大小                                        |
| @Past                                | java.util. Date, java.util. Calendar; Joda Time类库的日期类型                             | 验证注解的元素值（日期类型）比当前时间早                                                          |
| @Future                              | 与@Past要求一样                                                                     | 验证注解的元素值（日期类型）比当前时间晚                                                          |
| @NotBlank                            | CharSequence子类型                                                                | 验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的首位空格 |
| @Length(min=下限, max=上限)              | CharSequence子类型                                                                | 验证注解的元素值长度在min和max区间内                                                         |
| @NotEmpty                            | CharSequence子类型、Collection、Map、数组                                              | 验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）                                          |
| @Range(min=最小值, max=最大值)             | BigDecimal, BigInteger, CharSequence, byte, short, int, long等原子类型和包装类型           | 验证注解的元素值在最小值和最大值之间                                                            |
| @Email(regexp=正则表达式, flag=标志的模式)      | CharSequence子类型（如String）                                                       | 验证注解的元素值是Email，也可以通过regexp和flag指定自定义的email格式                                  |
| @Pattern(regexp=正则表达式, flag=标志的模式)    | String，任何CharSequence的子类型                                                      | 验证注解的元素值与指定的正则表达式匹配                                                           |
| @Valid                               | 任何非原子类型                                                                        | 指定递归验证关联的对象如用户对象中有个地址对象属性，如果想在验证用户对象时一起验证地址对象的话，在地址对象上加@Valid注解即可级联验证         |
| @Positive                            | 和@Min要求一样                                                                      | 适用于数值并验证它们是严格的正数                                                              |
| @PositiveOrZero                      | 和@Min要求一样                                                                      | 适用于数值并验证它们是严格的正数，包括 0                                                         |
| @Negative                            | 和@Min要求一样                                                                      | 适用于数值并验证它们是严格负数                                                               |
| @NegativeOrZero                      | 和@Min要求一样                                                                      | 适用于数值并验证它们是严格负数，包括0                                                           |
| @PastOrPresent                       | java.util. Date, java.util. Calendar; Joda Time类库的日期类型                             | 验证日期值是过去还是过去，包括现在；可以应用于日期类型，包括在 Java 8 中添加的日期类型。                              |
| @FutureOrPresent                     | java.util. Date, java.util. Calendar; Joda Time类库的日期类型                             | 验证日期值是在未来，包括现在                                                                |

### 验证注释也可以应用于集合的元素

```java
    private List<@NotBlank(message = "备注说明不能为空") String> preferences;
```

### 支持Java 8 中的新**Optional类型**

```java
    @Past(message = "出生年月必须是一个过去的时间")
    private LocalDate dateOfBirth;

    public Optional<@Past LocalDate> getDateOfBirth() {
        return Optional.of(dateOfBirth);
    }
```

### 程序验证

* 在框架中（例如：Spring）具有通过使用注释来进行验证，现在主要通过单元测试以编码方式进行设置

```java
package com.java.xval.val;

import com.java.xval.val.beanvalidation.User;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidationTest {

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

        // validate方法来验证我们的 UserBean User对象中定义的约束都将作为Set返回
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        for (ConstraintViolation<User> violation : violations) {

            // getMessage方法获取所有违规消息
            System.out.println(violation.getMessage());
        }
    }

}
```

* 示例如下图所示

![动图1](https://fy-image.oss-cn-beijing.aliyuncs.com/images/4.gif?versionId=CAEQFRiBgICGyYW43xciIDdmNGMzZTRlMzRmODRkMmE4NmUyNzRiNGYzOTI2ZDYw)

### 总结

* 上面介绍Java验证API的简单传递，使用**javax.validation注释和API**进行bean验证的基础知识，代码片段的实现都可以在 GitHub 上找到

## 三、Bean验证中@NotNull、@NotEmpty和@NotBlank约束之间的差异

### 概述差异

> 上面整体上使用bean验证实现，比较简单，但是一些实现这些约束还是有相关的差异，在项目中使用的时候经常会看到大家使用的比较混淆。

* **@NotNull**受约束的CharSequence、Collection、Map或Array只要不为空就有效，但可以为空。
* **@NotEmpty**受约束的CharSequence、Collection、Map或Array是有效的，只要它不为空，并且其大小/长度大于零。
* **@NotBlank**约束字符串只要不为空就有效，并且修剪后的长度大于零。

## 四、SpringBoot中集成参数校验

### 前言概述

* 在验证用户输入方面，Spring Boot为这种常见但关键的任务提供了强大的支持
* 尽管Spring Boot支持与自定义验证器的无缝集成，**但执行验证的事实上的标准是Hibernate Validator**

### 应用分层

> Java业务应用程序有不同的形式和类型。根据这些标准和形式，我们的程序需要确定在那些层进行参数验证需求。

![动图6](https://www.baeldung.com/wp-content/uploads/2021/06/Layered-Architecture.png)

* 消费者层或Web层是Web应用程序的最顶层，主要是**负责用户的输入并提供相应的响应**，Web层是应用程序的入口，负责身份验证并作为防止未经授权用户的第一道防线。

* 服务层验证

> 服务层是应用程序中Web层和持久层之前通信的层，业务逻辑存在服务器中，也包括验证逻辑等。**当验证不绑定到Web层，并允许使用任何可用的验证器**。同时客户端数据的输入并不总是WebREST层控制的。**如果不在服务层进行验证，数据可能流转到持久层，从而导致问题**，在服务层也可以使用**标准的 Java JSR-303 验证**

```java
package com.java.xval.val.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.xval.val.common.utils.ValidatorUtil;
import com.java.xval.val.mapper.PmsSkuStockMapper;
import com.java.xval.val.model.PmsSkuStock;
import com.java.xval.val.service.PmsSkuStockService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.*;
import java.util.Set;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author fangyang
 * @since 2021-09-16
 */
@Service
public class PmsSkuStockServiceImpl extends ServiceImpl<PmsSkuStockMapper, PmsSkuStock> implements PmsSkuStockService {

    @Override
    public void addPmsSkuStock(PmsSkuStock pmsSkuStock) {

        Set<ConstraintViolation<PmsSkuStock>> validate = ValidatorUtil.getValidator().validate(pmsSkuStock);

        if (!CollectionUtils.isEmpty(validate)) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<PmsSkuStock> constraintViolation : validate) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, validate);
        }

        save(pmsSkuStock);
    }

}

```

### springBoot Rest验证 开始引入依赖

从 Boot 2.3 开始，我们还需要显式添加spring-boot-starter-validation依赖项：

```xml
<dependency>
  <groupid>org.springframework.boot</groupid>
  <artifactid>spring-boot-starter-web</artifactid>
</dependency>

<dependency>
  <groupid>org.springframework.boot</groupid>
  <artifactid>spring-boot-starter-validation</artifactid>
</dependency>
```

### 定义要参数校验的实体类

```java
package com.java.xval.val.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;

/**
 * sku的库存
 *
 * @author fangyang
 * @since 2021-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pms_sku_stock")
@ApiModel(value = "PmsSkuStock对象", description = "sku的库存")
public class PmsSkuStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    @ApiModelProperty(value = "sku编码")
    @NotBlank(message = "sku编码编码不能为空")
    private String skuCode;

    @NotNull(message = "付款金额不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "付款金额不能小于0")
    @Digits(integer = 4, fraction = 2, message = "付款金额必须小于{integer}位数且不能超过{fraction}位小数")
    private BigDecimal price;

    @PositiveOrZero(message = "库存不能小于0")
    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "预警库存")
    @Positive(message = "预警库存必须大于0")
    private Integer lowStock;

    @ApiModelProperty(value = "销售属性1")
    private String sp1;

    private String sp2;

    private String sp3;

    @ApiModelProperty(value = "展示图片")
    private String pic;

    @ApiModelProperty(value = "销量")
    @PositiveOrZero(message = "库存不能为负数")
    private Integer sale;

    @ApiModelProperty(value = "单品促销价格")
    @DecimalMin(value = "0", message = "单品促销价格必须大于0")
    private BigDecimal promotionPrice;

    @ApiModelProperty(value = "锁定库存")
    @Min(value = 0, message = "锁定库存必须大于0")
    private Integer lockStock;

}

```

> 这里我列举了商品的sku的库存表展示了如何使用 Bean Validation 约束bean对象的属性，以上就是一个商品库存属性的基本定义。

### 实现一个REST控制器

> 需要实现一个层，允许获取分配给商品库存约束字段的值，可以进一步验证它们根据验证结果执行下一步的任务。
> Spring Boot通过 REST 控制器的实现使这个看似复杂的过程变得非常简单

```java
package com.java.xval.val.controller;

import com.java.xval.val.common.api.CommonResult;
import com.java.xval.val.model.PmsSkuStock;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * sku的库存 前端控制器
 *
 * @author fangyang
 * @since 2021-09-16
 */
@RestController
@RequestMapping("/val/pmsSkuStock")
public class PmsSkuStockController {

    @PostMapping("/add")
    CommonResult<String> addPmsSkuStock(@Valid @RequestBody PmsSkuStock pmsSkuStock) {

        return CommonResult.success("pmsSkuStock is valid");
    }
}

```

* 在spring REST中 addPmsSkuStock()方法的实现是增加商品库存信息，其中在验证参数过程中，最相关的部分是@Valid注释的使用。
* **当 Spring Boot 找到一个用@Valid注释的参数时，它会自动引导默认的JSR380实现Hibernate Validator——并验证该参数。** 当目标参数验证失败时，Spring Boot 会抛出MethodArgumentNotValidException异常

### @ExceptionHandler

* **@ExceptionHandler注解允许我们通过一个单一的方法处理特定类型的异常**

```java
package com.java.xval.val.common.exception;

import com.java.xval.val.common.api.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<String> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return getCommonResult(bindingResult);
    }

    /**
     * 异常数据封装.
     *
     * @param bindingResult 验证框架
     * @return the CommonResult
     */
    private CommonResult<String> getCommonResult(BindingResult bindingResult) {
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }
}

```

* 指定MethodArgumentNotValidException异常作为 <font color=red>**要处理的异常**</font> 。因此，当指定的PmsSkuStock对象无效时，SpringBoot将调用此方法.
* 该方法将无效的字段和验证后错误信息封装被全局异常 <font color=red>**(GlobalExceptionHandler)**</font> 拦截后将错误消息填充在自定义对象<font color=red>**CommonResult**</font>作为JSON表示返回到客户端进一步处理。
* 总之、REST 控制器允许处理不同情况的请求、验证商品库存对象以JSON格式响应返回。

### 测试REST控制器

#### 使用单元测试验证

* 目前springBoot只需要测试到web层，使用@WebMvcTest注释。允许MockMvcRequestBuilders和MockMvcResultMatchers类实现的方法进行单元测试请求和响应

```java
package com.java.xval.val;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class PmsSkuStockControllerIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @Test
    public void pmsSkuStock() throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        String pmsSkuStock = "{\"price\": \"-12\", \"skuCode\" : \"bob\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/val/pmsSkuStock/add")
                .content(pmsSkuStock)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(textPlainUtf8));
    }
}
```

* 单元测试如下

![动图2](https://fy-image.oss-cn-beijing.aliyuncs.com/images/888.gif?versionId=CAEQFRiBgMCGpM3J3xciIDY4M2M1YzliZmVkYzQxZDViMWM1MjUxYTExNDk3ODU4)

> 从上图可以看到、验证拦截生效 返回 <font color=red> **{"code":404, "message":"price付款金额不能小于0", "data":null}** </font>

#### 使用 Postmen 来测试REST控制器API

* 公司项目中如果对单元测试的覆盖率或者没有要求需要进行单元测试（ps: 个人认为如果单元测试是有必要的、能够减少代码的bug率、即时发现项目中的问题暴露出来。但是目前我个人除了必要的逻辑复杂模块进行单元测试，大部分还是使用Postmen）

![动图3](https://fy-image.oss-cn-beijing.aliyuncs.com/images/23.gif?versionId=CAEQFRiBgMCsq5bK3xciIGJjZGI5OGY2ODEyNTRhZDFhNDRlMzI0YWQ0YTUxYzI0)

> 使用postMen测试返回 price付款金额不能小于0

## 五、@Valid和@Validated注解的区别

### @Valid和@Validated注解

* <font color=red> **@Valid(javax.validation)** </font> : 是Bean Validation 中的标准注解，表示对需要校验的 【字段/方法/入参】 进行校验标记
* <font color=red> **@Validated (org.springframework.validation.annotation)** </font>: 是Spring对@Valid扩展后的变体，支持分组校验

### 分组验证代码示例

* 首先使用SpringBoot开发简单的用户订单表。我们只存在用户一些基础信息如下:

```java
package com.java.xval.val.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

/**
 * 订单表
 */
@Data
@ApiModel
public class OrderDTO {

    @ApiModelProperty("id,新增不必传递,修改必须传递")
    @Null(message = "id必须为null或空")
    private Integer id;

    @ApiModelProperty(value = "购物账号", required = true)
    @NotBlank(message = "购物账号不能为空")
    private String account;

    @ApiModelProperty(value = "订单号")
    private String orderNum;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮件格式不正确")
    private String email;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "备注")
    private String remark;
}
```

* REST控制器。在这里，将使用带有@Valid注释的addEmergency方法来验证用户输入的订单

```java
package com.java.xval.val.controller;

import com.java.xval.val.common.api.CommonResult;
import com.java.xval.val.model.request.OrderDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 描述:
 * 〈购物账号模块〉
 *
 * @author fangyang
 * @since 2021-09-16
 */
@Slf4j
@Api(tags = "【后端】订单模块")
@RestController
@RequestMapping(value = "/admin/order")
public class OrderController {

    @PostMapping(value = "add")
    @ApiOperation(httpMethod = "POST", value = "添加", response = Boolean.class)
    public CommonResult<String> addEmergency(@ApiParam(required = true) @RequestBody @Valid OrderDTO orderDTO) {
        return CommonResult.success("添加订单成功");
    }
}
```

* 如果这个时候我们需要将这个功能进行扩展，需要添加后台管理员一个修改订单备注的功能。还是同样的对象我们需要进行扩展，订单id和订单备注在修改的时候不能为空的验证，为了支持这种行为，我们需要分组验证和@Validated注释。
* 我们需要对字段进行分组，创建两个不同的组。首先，需要创建两个标记接口。每个组单独一个, 新增（AddParam)和修改(UpdateParam).
* AddParam

```java
package com.java.xval.val.model.request.validation;

import javax.validation.groups.Default;

public interface AddParam extends Default {
}
```

* UpdateParam

```java
package com.java.xval.val.model.request.validation;

import javax.validation.groups.Default;

public interface UpdateParam extends Default {
 
}
```

* 订单bean实体类

```java
package com.java.xval.val.model.request;

import com.java.xval.val.model.request.validation.AddParam;
import com.java.xval.val.model.request.validation.UpdateParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 订单表
 */
@Data
@ApiModel
public class OrderDTO {

    @ApiModelProperty("id,新增不必传递,修改必须传递")
    @Null(message = "id必须为null或空", groups = {AddParam.class})
    @NotNull(message = "id不能为空", groups = {UpdateParam.class})
    private Integer id;

    @ApiModelProperty(value = "购物账号", required = true)
    @NotBlank(message = "购物账号不能为空", groups = {AddParam.class})
    private String account;

    @ApiModelProperty(value = "订单号")
    private String orderNum;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮件格式不正确")
    private String email;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "备注")
    @NotNull(message = "备注不能为空", groups = {UpdateParam.class})
    private String remark;
}

```

* 对webController进行改造

```java
package com.java.xval.val.controller;

import com.java.xval.val.common.api.CommonResult;
import com.java.xval.val.model.request.OrderDTO;
import com.java.xval.val.model.request.validation.AddParam;
import com.java.xval.val.model.request.validation.UpdateParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 * 〈购物账号模块〉
 *
 * @author fangyang
 * @since 2021-09-16
 */
@Slf4j
@Api(tags = "【后端】订单模块")
@RestController
@RequestMapping(value = "/admin/order")
public class OrderController {

    @PostMapping(value = "add")
    @ApiOperation(httpMethod = "POST", value = "添加", response = Boolean.class)
    public CommonResult<String> addEmergency(@ApiParam(required = true) @RequestBody @Validated({AddParam.class}) OrderDTO orderDTO) {
        return CommonResult.success("添加订单成功");
    }

    @PostMapping(value = "update")
    @ApiOperation(httpMethod = "POST", value = "修改", response = Boolean.class)
    public CommonResult<String> updateEmergency(@ApiParam(required = true) @RequestBody @Validated({UpdateParam.class}) OrderDTO orderDTO) {
        return CommonResult.success("修改订单成功");
    }
}
```

* 以上可以自己进行验证，可以看到@Validated的使用 对于组验证至关重要

### 使用@Valid注解标记嵌套对象

* 所述@Valid注释用于标记尤其嵌套属性。这会触发嵌套对象的验证。例如，在我们当前的场景中，让我们创建一个 OrderNextCardDTO 礼品卡集合对象：

```java
/**
 * 订单表
 */
@Data
@ApiModel
public class OrderDTO {

    //...

    @Valid
    @NotNull(message = "礼品卡不能为空", groups = {AddParam.class})
    @ApiModelProperty(value = "礼品卡集合")
    List<OrderNextCardDTO> orderNextCardDTOList;

}

```

```java
package com.java.xval.val.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * <p>
 * 订单子表-->礼品卡
 * </p>
 */
@Data
@ApiModel
public class OrderNextCardDTO {

    @ApiModelProperty(value = "礼品卡id")
    @NotNull(message = "礼品卡id为空", groups = {AddParam.class})
    private Integer cardId;

    @ApiModelProperty(value = "使用金额")
    @DecimalMin(value = "0", message = "使用金额最小为0", groups = {AddParam.class})
    private BigDecimal amount;

}
```

* 新增接口测试图例

![动图4](https://fy-image.oss-cn-beijing.aliyuncs.com/images/333.gif?versionId=CAEQFRiBgIDM85nM3xciIDViNWMwMzczYjhmYjQwZTNhNmNjNmE1NmVkNDkxNmY2)

* 修改接口测试图例

![动图5](https://fy-image.oss-cn-beijing.aliyuncs.com/images/222.gif?versionId=CAEQFRiBgIDA3ZPM3xciIDYzY2I1ZjRhNzY4NzQ0ZjdiNjgxODMzNDhhZjEyMTlj)

### 对比总结

> @Valid注释保证了整个对象的验证。重要的是，它执行整个对象的验证。这会为仅需要部分验证的场景带来问题. 可以使用@Validated 进行组验证，包括上面的部分验证

## 六、枚举类型的验证

### 验证枚举简介

* 下面我将使用自定义注解为枚举构建验证，**<font color=red>在JSR 380bean验证注解中，大多数标准注解不能应用于enums</font>**
* 当然也可以使用@Pattern注释，但是在匹配的时候也不够全面。唯一可以应用于枚举的标准注释是@NotNull和@Null

### 验证字符串是否匹配枚举的值

* 创建一个注解来检查字符串对于特定枚举是否有效。

```java
package com.joyowo.smarthr.social.common.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证状态是否在指定范围内的注解
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorClass.class)
public @interface EnumValue {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return 枚举类
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * @return 枚举校验的方法
     */
    String enumMethod() default "isValidEnum";

    /**
     * @return 默认提示文字
     */
    String message() default "传参错误,对应枚举未找到";

    /**
     * @return 默认返回为false
     */
    boolean allowNull() default false;

}

```

* 可以将此注解添加到String字段，可以传递任何枚举值

```java
/**
 * 订单表
 */
@Data
@ApiModel
public class OrderDTO {

    //...

    /**
     * 下单网站
     */
    @ValueOfEnum(enumClass = OrderWebsiteEnum.class, groups = {AddParam.class}, message = "对应的下单网站不存在")
    private String orderWebsite;

}

```

* 定义ValueOfEnumValidator来检查字符串（或任何CharSequence）是否包含在 enum 中：

```java
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

```

* 实际验证中将值映射到String, 而不是将值映射到枚举。然后将使用定义的验证器来检查它是否与任何枚举值匹配。

* 枚举接口测试图例

![动图6](https://fy-image.oss-cn-beijing.aliyuncs.com/images/777.gif?versionId=CAEQFRiBgIC3i93O3xciIDMwZWNiZjViNjRiYzQxZjJiYmE1OWY4NGYxOTMxNTY0)

## 七、总结

### 回顾

> 参数验证在目前的实际开发中使用频率非常多，但是还停留在简单的使用上，比如分组校验，自定义注解参数验证还没怎么用到过，项目中如果要进行对象参数验证，会建立多了VO用于接收Create，Update场景的情况，比较荣誉和繁琐。上面做了相关的说明和用例测试。

- 多种常用校验注解
- 单个参数校验
- 全局异常处理自动组装校验异常
- 分组验证
- 嵌套验证
- 自定义注解验证

### excel导入和关联参数验证

* 目前项目中excel导入数据源占用的比例很大，在项目中可以使用 **Hibernate Validator**对基础的参数进行验证，但是对于一些关联的参数验证，比如政策变更申请导入列验证
    * 停止执行年月 需晚于等于执行开始时间
    * 追溯对象不为空的情况下、应该与缴纳主体一致
    * 企缴固定金额（元）收费频率等于按月 缴纳主体等于企业或者全部 当且仅当“当月缴企缴规则等于按固定值”时必填
    * 个缴固定金额（元）收费频率等于按月 缴纳主体等于个人或者全部 当且仅当“月缴个缴规则等于按固定值”时必填
    * 企缴最低基数（元):收费频率为月,月缴企缴计算规则等于缴基数比例,缴纳主体等于企业或者全部时必填
    * ......等100多个需要验证的逻辑

* 面对这样可以使用阿里来源的项目**QLExpress脚本引擎**进行优化，github地址：<https://github.com/alibaba/QLExpress>。介于目前篇幅比较长，后续再单独介绍脚本引擎在导入中的使用。如果感兴趣的可以先提前了解下。
