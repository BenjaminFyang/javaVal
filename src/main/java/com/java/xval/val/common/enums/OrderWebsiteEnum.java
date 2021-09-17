package com.java.xval.val.common.enums;

import lombok.Getter;


@Getter
public enum OrderWebsiteEnum {

    TYPE0(0, "Nike"),
    TYPE1(1, "Footlocker"),
    TYPE2(2, "Eastbay"),
    TYPE3(3, "Champs"),
    TYPE4(4, "Footaction"),
    TYPE5(5, "Kidsfootlocker"),
    TYPE6(6, "Footsites"),
    TYPE7(6, "Supreme"),
    TYPE8(6, "YeezySupply");

    private Integer code;
    private String message;

    OrderWebsiteEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


}
