package com.alfred.parkingalfred.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "orders")
@Entity
@Data
@NoArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String orderId;

  private Integer type;

  private Long reservationTime;

  private String customerAddress;

  private Integer status;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "employeeId",referencedColumnName="id")
  @JsonIgnore
  private Employee employee;

  private String carNumber;

  @OneToOne
  @JoinColumn(name = "parkingLotId",referencedColumnName="id")
  private ParkingLot parkingLot;

  public Order(String orderId, Integer type,  String customerAddress, Integer status) {
    this.orderId = orderId;
    this.type = type;
    this.customerAddress = customerAddress;
    this.status = status;
  }

  public Order(String orderId, Integer type, Long reservationTime, String customerAddress, Integer status, Employee employee,String carNumber , ParkingLot parkingLot) {
    this.orderId = orderId;
    this.type = type;
    this.reservationTime = reservationTime;
    this.customerAddress = customerAddress;
    this.status = status;
    this.employee = employee;
    this.parkingLot = parkingLot;
    this.carNumber=carNumber;
  }

    public void setCar(Car car) {
    }
}
