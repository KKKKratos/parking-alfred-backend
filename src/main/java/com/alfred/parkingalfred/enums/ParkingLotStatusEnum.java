package com.alfred.parkingalfred.enums;

import lombok.Getter;

@Getter
public enum ParkingLotStatusEnum {
    USABLE(1, "usable"),
    UNUSABLE(2, "unusable");

    private Integer code;

    private String message;

    ParkingLotStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
