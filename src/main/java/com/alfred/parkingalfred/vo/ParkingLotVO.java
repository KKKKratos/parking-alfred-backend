package com.alfred.parkingalfred.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ParkingLotVO {

    private Long id;

    private String name;

    private Integer capacity;

    private Integer occupied;

    private Integer status;

    private List<EmployeeVO> employeeVOS;
}
