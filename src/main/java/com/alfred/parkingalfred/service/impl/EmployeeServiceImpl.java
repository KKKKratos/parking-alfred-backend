package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.utils.EncodingUtil;
import java.util.Optional;
import org.springframework.stereotype.Service;

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
  public Employee getEmployeeByNameAndPassword(String name, String password) {
    String encodedPassword = EncodingUtil.encodingByMd5(password);
    Employee employee = employeeRepository.findByNameAndPassword(name, encodedPassword);
    if (employee == null) {
      throw new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED);
    }
    return employee;
  }

  @Override
  public boolean doesEmplyeeHasNotFullParkingLots(Long employeeId) {
    employeeRepository.findById(employeeId).orElseThrow(() ->
        new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    int result = parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId);
    return result > 0 ? true : false;
  }
}
