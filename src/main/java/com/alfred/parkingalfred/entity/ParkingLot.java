package com.alfred.parkingalfred.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.*;

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

  @ManyToMany(mappedBy = "parkingLots",fetch = FetchType.EAGER,
          cascade = {CascadeType.REFRESH, CascadeType.REMOVE
                  , CascadeType.MERGE, CascadeType.PERSIST})
  @JsonIgnore
  private List<Employee> employees;

  public ParkingLot(Long id, String name, Integer capacity, Integer occupied) {
    this.id = id;
    this.name = name;
    this.capacity = capacity;
    this.occupied = occupied;
  }
}
