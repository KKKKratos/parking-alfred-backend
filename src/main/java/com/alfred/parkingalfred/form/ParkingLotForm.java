package com.alfred.parkingalfred.form;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ParkingLotForm  {
  private Long id;

  @NotEmpty
  private String name;

  private Integer capacity;

  private Integer occupied;
}
