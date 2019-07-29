package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.vo.EmployeeVO;
import java.util.List;
import org.springframework.data.domain.Page;

public interface EmployeeService {

  Employee getEmployeeByMailAndPassword(String mail, String password);

  boolean doesEmployeeHasNotFullParkingLots(Long employeeId);


  List<EmployeeVO> getAllEmployeesByPageAndSize(Integer page,Integer size);
  Employee getEmployeeById(Long id);
}
