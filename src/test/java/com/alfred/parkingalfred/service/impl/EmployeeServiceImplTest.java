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
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.exception.IncorrectParameterException;
import com.alfred.parkingalfred.form.EmployeeForm;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.EmployeeRepositoryImpl;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class EmployeeServiceImplTest {

  private EmployeeRepository employeeRepository;

  private ParkingLotRepository parkingLotRepository;

  private EmployeeService employeeService;

  private ObjectMapper objectMapper;

  private EmployeeRepositoryImpl employeeRepositoryImpl;
  @Autowired private ApplicationEventPublisher publisher;

  @Before
  public void setUp() {
    employeeRepository = mock(EmployeeRepository.class);
    parkingLotRepository = mock(ParkingLotRepository.class);
    publisher = mock(ApplicationEventPublisher.class);
    employeeRepositoryImpl = mock(EmployeeRepositoryImpl.class);
    employeeService =
        new EmployeeServiceImpl(
            employeeRepository, parkingLotRepository, publisher, employeeRepositoryImpl);
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
    assertEquals(
        objectMapper.writeValueAsString(employee), objectMapper.writeValueAsString(actualEmployee));
  }

  @Test
  public void
      should_return_true_when_call_doesEmployeeHasNotFullParkingLots_with_employeeId_and_he_or_she_has_notFull_parking_lot() {
    Long employeeId = 1L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));
    when(parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId)).thenReturn(1);
    boolean result = employeeService.doesEmployeeHasNotFullParkingLots(employeeId);
    assertTrue(result);
  }

  @Test
  public void
      should_return_false_when_call_doesEmployeeHasNotFullParkingLots_with_employeeId_and_he_or_she_has_not_notFull_parking_lot() {
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

    assertEquals(
        objectMapper.writeValueAsString(EmployeeToEmployeeVOConverter.convert(employee)),
        objectMapper.writeValueAsString(EmployeeToEmployeeVOConverter.convert(actualEmployee)));
  }

  @Test
  public void
      should_return_employeeVOList_when_call_getAllEmployeesByPageAndSize_with_page_and_size() {
    List<Employee> employeeList = new ArrayList<Employee>();
    for (int i = 0; i < 10; i++) {
      Employee employee = new Employee();
      employee.setName("name");
      employee.setTelephone("13" + i);
      employeeList.add(employee);
    }
    EmployeeVO employeeVO = new EmployeeVO();
    employeeVO.setName("name");
    employeeVO.setTelephone("13");
    when(employeeRepositoryImpl.getAllByQueryWithPageAndEmployeeVO(1, 10, employeeVO))
        .thenReturn(employeeList);
    List<EmployeeVO> employeeListResult =
        employeeService.getAllEmployeesByPageAndSize(1, 10, employeeVO);
    Assert.assertEquals(10, employeeListResult.size());
  }

  @Test
  public void should_return_employee_when_call_createEmployee_with_true_param() {
    EmployeeForm employeeForm = new EmployeeForm();
    employeeForm.setName("test");
    employeeForm.setStatus(RoleEnum.PARKING_BOY.getCode());
    employeeForm.setMail("764974614@qq.com");
    employeeForm.setPassword("123456");
    Employee employeeExpected = new Employee();
    employeeExpected.setId(1L);
    employeeExpected.setName("test");
    employeeExpected.setStatus(RoleEnum.PARKING_BOY.getCode());
    employeeExpected.setMail("764974614@qq.com");
    when(employeeRepository.save(any(Employee.class))).thenReturn(employeeExpected);
    EmployeeVO employeeVOActual = employeeService.createEmployee(employeeForm);
    Assert.assertEquals(employeeExpected.getId(), employeeVOActual.getId());
  }

  @Test
  public void should_add_parking_lots_to_employee() throws JsonProcessingException {
    Long id = 1L;
    Employee beforeUpdateEmployee = new Employee();
    beforeUpdateEmployee.setId(id);
    beforeUpdateEmployee.setParkingLots(new ArrayList<>());
    List<Long> parkingLotIdList =
        new ArrayList<Long>() {
          {
            add(1L);
            add(2L);
          }
        };
    List<ParkingLot> parkingLots =
        new ArrayList<ParkingLot>() {
          {
            add(new ParkingLot());
            add((new ParkingLot()));
          }
        };

    Employee afterUpdateEmployee = new Employee();
    afterUpdateEmployee.setId(id);
    afterUpdateEmployee.setParkingLots(parkingLots);
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(beforeUpdateEmployee));
    when(parkingLotRepository.findAllByIdIn(anyList())).thenReturn(parkingLots);
    Employee actualEmployee = employeeService.updateEmployeeParkingLots(id, parkingLotIdList);

    assertEquals(
        objectMapper.writeValueAsString(afterUpdateEmployee),
        objectMapper.writeValueAsString(actualEmployee));
  }

  @Test
  public void should_return_employee_when_call_updateEmployee_API_with_true_param() {
    EmployeeVO employeeVO = new EmployeeVO();
    employeeVO.setRole(1);
    employeeVO.setTelephone("1316");
    employeeVO.setName("1232131");
    employeeVO.setStatus(2);
    Long employeeId = 1L;
    Employee employee = new Employee();
    employeeVO.setRole(1);
    employeeVO.setTelephone("1");
    employeeVO.setName("2");
    employeeVO.setStatus(1);
    employee.setId(1L);
    Employee employeeExpected = new Employee();
    BeanUtils.copyProperties(employeeVO, employeeExpected);
    employeeExpected.setId(employeeId);
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
    when(employeeRepository.save(employee)).thenReturn(employeeExpected);
    Employee employeeActual = employeeService.updateEmployee(employeeId, employeeVO);
    Assert.assertEquals(employeeVO.getName(), employeeActual.getName());
  }

  @Test
  public void should_create_customer_when_customer_sign_up() throws JsonProcessingException {
    EmployeeForm employeeForm = new EmployeeForm();
    employeeForm.setName("name");
    employeeForm.setMail("mail");
    employeeForm.setPassword("password");
    employeeForm.setTelephone("telephone");

    Employee expectEmployee = new Employee();
    expectEmployee.setName("name");
    expectEmployee.setMail("mail");
    expectEmployee.setPassword(EncodingUtil.encodingByMd5("password"));
    expectEmployee.setTelephone("telephone");
    expectEmployee.setRole(RoleEnum.CUSTOMER.getCode());
    when(employeeRepository.save(any())).thenReturn(expectEmployee);
    Employee actualEmployee = employeeService.createCustomer(employeeForm);

    assertEquals(
        objectMapper.writeValueAsString(expectEmployee),
        objectMapper.writeValueAsString(actualEmployee));
  }

  @Test
  public void should_return_count_of_employees_when_call_getEmployeeCount_with_true_param() {
    Integer role = RoleEnum.PARKING_BOY.getCode();
    when(employeeRepository.getEmployeeCount(role)).thenReturn(4);
    int result = employeeService.getEmployeeCount(role);
    assertEquals(4, result);
  }

  @Test(expected = IncorrectParameterException.class)
  public void
      should_return_IncorrectParameterException_when_call_createCustomer_with_wrong_param() {
    EmployeeForm employeeForm = new EmployeeForm();
    employeeForm.setName("23123");
    employeeService.createCustomer(employeeForm);
  }

  @Test(expected = EmployeeNotExistedException.class)
  public void
      should_return_EmployeeNotExistedException_when_call_getEmployeeByMailAndPassword_with_wrong_param() {
      when(employeeRepository.findByMailAndPassword(anyString(),anyString())).thenReturn(null);
      employeeService.getEmployeeByMailAndPassword("321","2312");
  }
}
