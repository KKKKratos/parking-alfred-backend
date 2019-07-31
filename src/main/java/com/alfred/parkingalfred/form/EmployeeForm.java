package com.alfred.parkingalfred.form;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmployeeForm {

  private Long id;

  private String name;

  private String telephone;

  private Integer status;

  @NotEmpty(message = "mail must be not null")
  private String mail;

  private String password;

  private Integer role;
}
