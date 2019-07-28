package com.alfred.parkingalfred.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateOrderDto {

    private String carNumber;

    private String customerAddress;

    private Long reservationTime;

    private Integer type;
}
