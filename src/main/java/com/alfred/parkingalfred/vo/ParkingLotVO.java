package com.alfred.parkingalfred.vo;

import com.alfred.parkingalfred.entity.Employee;
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

    private List<EmployeeVO> employeeVOS;
}
