package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.converter.EmployeeToEmployeeVOConverter;
import com.alfred.parkingalfred.converter.ParkingLotToParkingLotVOConverter;
import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.IncorrectParameterException;
import com.alfred.parkingalfred.form.EmployeeForm;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.service.ParkingLotService;
import com.alfred.parkingalfred.utils.JwtUtil;
import com.alfred.parkingalfred.vo.EmployeeVO;
import com.alfred.parkingalfred.vo.ParkingLotVO;
import com.alfred.parkingalfred.vo.ResultVO;
import com.alfred.parkingalfred.utils.ResultVOUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
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
    Employee employee = employeeService
        .getEmployeeByMailAndPassword(loginEmployee.getMail(), loginEmployee.getPassword());
    String token = JwtUtil.generateToken(employee);
    return ResultVOUtil.success(token);
  }

  @GetMapping("/employees/{employeeId}/parking-lots")
  public ResultVO getParkingLotsByEmployeeId(@PathVariable Long employeeId) {
    List<ParkingLot> parkingLots = parkingLotService.getParkingLotsByParkingBoyId(employeeId);
    return ResultVOUtil.success(parkingLots);
  }

  @GetMapping("/employees/{employeeId}/status")
  public ResultVO getEmployeeParkingLotStatus(@PathVariable Long employeeId) {
    boolean result = employeeService.doesEmployeeHasNotFullParkingLots(employeeId);
    return ResultVOUtil.success(result);
  }

  @GetMapping("/employees")
  public ResultVO getEmployees(@RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "size", defaultValue = "10") Integer size,
      @RequestParam(value = "name",required = false) String name,
      @RequestParam(value = "telephone",required = false)String telephone,
      @RequestParam(value = "role",required = false)Integer role) {
    EmployeeVO employeeVO = new EmployeeVO();
    employeeVO.setName(name);
    employeeVO.setTelephone(telephone);
    employeeVO.setRole(role);
    List<EmployeeVO> employeeVOList = employeeService.getAllEmployeesByPageAndSize(page, size,employeeVO);
    int totalCount = employeeService.getEmployeeCount(role);
    Map<String,Object> objectMap = new HashMap<>();
    objectMap.put("employees",employeeVOList);
    objectMap.put("totalCount",totalCount);
    return ResultVOUtil.success(objectMap);
  }

  @GetMapping("/employee")
  public ResultVO getSelfEmployee() {
    Long selfId = JwtUtil.getEmployeeId();
    Employee employee = employeeService.getEmployeeById(selfId);
    return ResultVOUtil.success(EmployeeToEmployeeVOConverter.convert(employee));
  }

  @PostMapping("/employees")
  public ResultVO createEmployee(@Valid @RequestBody EmployeeForm employeeForm, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new IncorrectParameterException(ResultEnum.PARAM_ERROR);
    }
    EmployeeVO employeeVO = employeeService.createEmployee(employeeForm);
    return ResultVOUtil.success(employeeVO);
  }

  @PutMapping("/employees/{employeeId}/parking-lots")
  public ResultVO updateEmployeeParkingLots(@PathVariable Long employeeId, @RequestBody List<Long> parkingLotIdList) {
    Employee employee = employeeService.updateEmployeeParkingLots(employeeId, parkingLotIdList);
    EmployeeVO employeeVO = EmployeeToEmployeeVOConverter.convert(employee);
    List<ParkingLotVO> parkingLotVOS = employee.getParkingLots().stream()
            .map(ParkingLotToParkingLotVOConverter::convert)
            .collect(Collectors.toList());
    employeeVO.setParkingLotVOS(parkingLotVOS);
    return ResultVOUtil.success(employeeVO);
  }

  @PostMapping("/customers")
  public ResultVO createCustomer(@Valid @RequestBody EmployeeForm employeeForm, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new IncorrectParameterException(ResultEnum.PARAM_ERROR);
    }
    Employee employee = employeeService.createCustomer(employeeForm);
    return ResultVOUtil.success(EmployeeToEmployeeVOConverter.convert(employee));
  }
}
