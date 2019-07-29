package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.enums.RoleEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.utils.EncodingUtil;
import com.alfred.parkingalfred.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final ParkingLotRepository parkingLotRepository;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository,
      ParkingLotRepository parkingLotRepository) {
    this.employeeRepository = employeeRepository;
    this.parkingLotRepository = parkingLotRepository;
  }

  @Override
  public Employee getEmployeeByMailAndPassword(String mail, String password) {
    String encodedPassword = EncodingUtil.encodingByMd5(password);
    Employee employee = employeeRepository.findByMailAndPassword(mail, encodedPassword);
    if (employee == null) {
      throw new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED);
    }
    return employee;
  }

  @Override
  public boolean doesEmployeeHasNotFullParkingLots(Long employeeId) {
    employeeRepository.findById(employeeId).orElseThrow(() ->
        new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    int result = parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId);
    return result > 0;
  }

  @Override
  public Employee getEmployeeByIdWithRole(Long id, Long selfId, RoleEnum selfRole) {
    Supplier<? extends RuntimeException> throwable = () -> new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED);
    if (RoleEnum.MANAGER == selfRole || RoleEnum.ADMIN == selfRole) {
      return employeeRepository.findById(id).orElseThrow(throwable);
    }
    return employeeRepository.findById(selfId).orElseThrow(throwable);
  }
}
