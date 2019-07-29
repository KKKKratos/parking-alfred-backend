package com.alfred.parkingalfred.form;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

  @NotEmpty(message = "password must be not null")
  private String password;

  private Integer role;
}
