package com.alfred.parkingalfred.service.impl;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alfred.parkingalfred.converter.EmployeeToEmployeeVOConverter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.RoleEnum;
import com.alfred.parkingalfred.form.EmployeeForm;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.utils.EncodingUtil;
import com.alfred.parkingalfred.vo.EmployeeVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmployeeServiceImplTest {

  private EmployeeRepository employeeRepository;

  private ParkingLotRepository parkingLotRepository;
  private EmployeeService employeeService;

  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    employeeRepository = mock(EmployeeRepository.class);
    parkingLotRepository = mock(ParkingLotRepository.class);
    employeeService = new EmployeeServiceImpl(employeeRepository, parkingLotRepository);
    objectMapper = new ObjectMapper();
  }

  @Test
  public void should_return_employee_when_get_employee_by_name_and_password()
      throws JsonProcessingException {
    String mail = "mail";
    String password = "password";
    String encodedPassword = EncodingUtil.encodingByMd5(password);

    Employee employee = new Employee();
    when(employeeRepository.findByMailAndPassword(mail, encodedPassword)).thenReturn(employee);
    Employee actualEmployee = employeeService.getEmployeeByMailAndPassword(mail, password);
    assertEquals(objectMapper.writeValueAsString(employee),
        objectMapper.writeValueAsString(actualEmployee));
  }

  @Test
  public void should_return_true_when_call_doesEmployeeHasNotFullParkingLots_with_employeeId_and_he_or_she_has_notFull_parking_lot() {
    Long employeeId = 1L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));
    when(parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId)).thenReturn(1);
    boolean result = employeeService.doesEmployeeHasNotFullParkingLots(employeeId);
    assertTrue(result);
  }

  @Test
  public void should_return_false_when_call_doesEmployeeHasNotFullParkingLots_with_employeeId_and_he_or_she_has_not_notFull_parking_lot() {
    Long employeeId = 1L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));
    when(parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId)).thenReturn(0);
    boolean result = employeeService.doesEmployeeHasNotFullParkingLots(employeeId);
    assertFalse(result);
  }

  @Test
  public void should_get_employee_when_get_self_employee() throws JsonProcessingException {
    Long employeeId = 1L;

    Employee employee = new Employee();
    employee.setId(employeeId);
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
    Employee actualEmployee = employeeService.getEmployeeById(employeeId);

    assertEquals(objectMapper.writeValueAsString(EmployeeToEmployeeVOConverter.convert(employee)),
        objectMapper.writeValueAsString(EmployeeToEmployeeVOConverter.convert(actualEmployee)));
  }

  @Test
  public void should_return_employeeVOList_when_call_getAllEmployeesByPageAndSize_with_page_and_size() {
    int page = 1, size = 5;
    List<Employee> employeeList = new ArrayList<Employee>() {{
      add(new Employee());
      add(new Employee());
      add(new Employee());
      add(new Employee());
      add(new Employee());
    }};
    PageImpl<Employee> employeePageActual = new PageImpl<>(employeeList);
    when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePageActual);
    List<EmployeeVO> employeeListResult = employeeService.getAllEmployeesByPageAndSize(page, size);
    Assert.assertEquals(5, employeeListResult.size());
  }

  @Test
  public void should_return_employee_when_call_createEmployee_with_true_param(){
    EmployeeForm employeeForm = new EmployeeForm();
    employeeForm.setName("test");
    employeeForm.setStatus(RoleEnum.PARKING_BOY.getCode());
    employeeForm.setMail("aqqsda@qq.com");
    employeeForm.setPassword("123456");

    Employee employeeExpected = new Employee();
    employeeExpected.setId(1L);
    employeeExpected.setName("test");
    employeeExpected.setStatus(RoleEnum.PARKING_BOY.getCode());
    employeeExpected.setMail("aqqsda@qq.com");

    when(employeeRepository.save(any(Employee.class))).thenReturn(employeeExpected);
    EmployeeVO employeeVOActual = employeeService.createEmployee(employeeForm);
    Assert.assertEquals(employeeExpected.getId(),employeeVOActual.getId());
  }

  @Test
  public void should_add_parking_lots_to_employee() throws JsonProcessingException {
    Long id = 1L;
    Employee beforeUpdateEmployee = new Employee();
    beforeUpdateEmployee.setId(id);
    beforeUpdateEmployee.setParkingLots(new ArrayList<>());
    List<Long> parkingLotIdList = new ArrayList<Long>() {{
      add(1L);
      add(2L);
    }};
    List<ParkingLot> parkingLots = new ArrayList<ParkingLot>() {{
      add(new ParkingLot());
      add((new ParkingLot()));
    }};

    Employee afterUpdateEmployee = new Employee();
    afterUpdateEmployee.setId(id);
    afterUpdateEmployee.setParkingLots(parkingLots);
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(beforeUpdateEmployee));
    when(parkingLotRepository.findAllByIdIn(anyList())).thenReturn(parkingLots);
    Employee actualEmployee = employeeService.updateEmployeeParkingLots(id, parkingLotIdList);

    assertEquals(objectMapper.writeValueAsString(afterUpdateEmployee),
            objectMapper.writeValueAsString(actualEmployee));
  }
}