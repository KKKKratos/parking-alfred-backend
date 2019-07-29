package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.enums.RoleEnum;

public interface EmployeeService {

  Employee getEmployeeByMailAndPassword(String mail, String password);

  boolean doesEmployeeHasNotFullParkingLots(Long employeeId);

  Employee getEmployeeByIdWithRole(Long id, Long selfId, RoleEnum selfRole);
}
