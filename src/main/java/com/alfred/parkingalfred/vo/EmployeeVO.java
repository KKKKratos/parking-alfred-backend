package com.alfred.parkingalfred.vo;


import java.io.Serializable;
import javax.persistence.Column;
import lombok.Data;

@Data
public class EmployeeVO implements Serializable {
  private static final long serialVersionUID = 5585749034096419420L;

  private Long id;

  private String name;

  private String telephone;

  private Integer status;

  private String mail;

  private String password;

  private Integer role;
}
