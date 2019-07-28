package com.alfred.parkingalfred.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ParkingLot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  private Integer capacity;

  private Integer occupied;

  @ManyToMany(mappedBy = "parkingLots")
  @JsonIgnore
  private List<Employee> employees;

  public ParkingLot(Long id, String name, Integer capacity, Integer occupied) {
    this.id = id;
    this.name = name;
    this.capacity = capacity;
    this.occupied = occupied;
  }
}
