package com.alfred.parkingalfred.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum implements BaseEnum {

    WAIT_FOR_RECEIVE(1, "WAIT_FOR_RECEIVE"),
    WAIT_FOR_CONFIRM(2, "WAIT_FOR_CONFIRM"),
    CONFIRM(3, "CONFIRM"),
    WAIT_FOR_CUSTOMER_CONFIRM(4, "WAIT_FOR_CUSTOMER_CONFIRM");

    private Integer code;

    private String message;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
