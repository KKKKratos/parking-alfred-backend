package com.alfred.parkingalfred.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParkingLotVo {

    private Long id;

    private String name;

    private Integer capacity;

    private Integer occupied;
}
