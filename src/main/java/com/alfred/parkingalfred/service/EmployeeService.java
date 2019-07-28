package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.entity.Employee;

public interface EmployeeService {

  Employee getEmployeeByNameAndPassword(String name, String password);

  boolean doesEmployeeHasNotFullParkingLots(Long employeeId);
}
