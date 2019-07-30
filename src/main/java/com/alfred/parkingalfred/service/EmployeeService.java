package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.form.EmployeeForm;
import com.alfred.parkingalfred.vo.EmployeeVO;

import java.util.List;

public interface EmployeeService {

    Employee getEmployeeByMailAndPassword(String mail, String password);

    boolean doesEmployeeHasNotFullParkingLots(Long employeeId);

    public List<EmployeeVO> getEmployeesByRoleWithFilterByPageAndSize(Integer page, Integer size,Integer role);

    Employee getEmployeeById(Long id);

    EmployeeVO createEmployee(EmployeeForm employeeFormmployee);

    int getEmployeeCount();

    Employee updateEmployeeParkingLots(Long employeeId, List<Long> parkingLotIdList);

}
