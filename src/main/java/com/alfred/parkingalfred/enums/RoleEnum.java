package com.alfred.parkingalfred.enums;

import lombok.Getter;

@Getter
public enum RoleEnum implements BaseEnum {

    PARKING_BOY(1,"PARKING_BOY"),
    MANAGER(2,"MANAGER"),
    ADMIN(3,"ADMIN"),
    CUSTOMER(4, "CUSTOMER");

    private Integer code;

    private String message;

    RoleEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
