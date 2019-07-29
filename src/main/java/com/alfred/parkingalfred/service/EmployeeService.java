package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.entity.Employee;

public interface EmployeeService {

  Employee getEmployeeByMailAndPassword(String mail, String password);

  boolean doesEmployeeHasNotFullParkingLots(Long employeeId);
}
