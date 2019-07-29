package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.RoleEnum;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.service.ParkingLotService;
import com.alfred.parkingalfred.utils.JwtUtil;
import com.alfred.parkingalfred.vo.ResultVO;
import com.alfred.parkingalfred.utils.ResultVOUtil;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

  private final EmployeeService employeeService;

  private final ParkingLotService parkingLotService;

  public EmployeeController(EmployeeService employeeService, ParkingLotService parkingLotService) {
    this.employeeService = employeeService;
    this.parkingLotService = parkingLotService;
  }

  @PostMapping(value = "/login")
  public ResultVO login(@RequestBody Employee loginEmployee) {
    Employee employee = employeeService.getEmployeeByMailAndPassword(loginEmployee.getMail(), loginEmployee.getPassword());
    String token = JwtUtil.generateToken(employee);
    return ResultVOUtil.success(token);
  }

  @GetMapping(value = "/employee/{employeeId}/parking-lots")
  public ResultVO getParkingLotsByEmployeeId(@PathVariable Long employeeId) {
    List<ParkingLot> parkingLots = parkingLotService.getParkingLotsByParkingBoyId(employeeId);
    return  ResultVOUtil.success(parkingLots);
  }

  @GetMapping(value = "/employee/{employeeId}/status")
  public ResultVO getEmployeeParkingLotStatus(@PathVariable Long employeeId){
    boolean result = employeeService.doesEmployeeHasNotFullParkingLots(employeeId);
    return  ResultVOUtil.success(result);
  }

  @GetMapping("/employees/{employeeId}")
  public ResultVO getEmployeeByIdWithRole(@PathVariable Long employeeId) {
      Long selfId = JwtUtil.getEmployeeId();
      RoleEnum role = JwtUtil.getRole();
      Employee employee = employeeService.getEmployeeByIdWithRole(employeeId, selfId, role);
      return ResultVOUtil.success(employee);
  }
}
