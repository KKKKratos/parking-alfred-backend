package com.alfred.parkingalfred.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String name;

  private String telephone;

  private Integer status;

  private String mail;

  private String password;

  private Integer role;
  @OneToMany(mappedBy = "employee",
      targetEntity = Order.class,
      cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Order> orders;

  @ManyToMany(fetch = FetchType.EAGER,
      cascade = {CascadeType.REFRESH, CascadeType.REMOVE
          , CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(name = "employee_parkingLot",
      joinColumns = {
          @JoinColumn(name = "eid"),
      },
      inverseJoinColumns = {
          @JoinColumn(name = "pid")
      })
  @JsonIgnore
  private List<ParkingLot> parkingLots;
}
