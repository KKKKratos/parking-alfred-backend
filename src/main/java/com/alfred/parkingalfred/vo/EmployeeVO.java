package com.alfred.parkingalfred.vo;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeVO implements Serializable {
  private static final long serialVersionUID = 5585749034096419420L;

  private Long id;

  private String name;

  private String telephone;

  private Integer status;

  private String mail;

  private Integer role;

  private List<ParkingLotVo> parkingLotVos;
}
