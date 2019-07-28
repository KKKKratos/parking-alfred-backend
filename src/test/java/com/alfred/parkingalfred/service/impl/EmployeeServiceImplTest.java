package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.utils.EncodingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
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
        employeeService = new EmployeeServiceImpl(employeeRepository,parkingLotRepository);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void should_return_employee_when_get_employee_by_name_and_password() throws JsonProcessingException {
        String name = "name";
        String password = "password";
        String encodedPassword = EncodingUtil.encodingByMd5(password);

        Employee employee = new Employee();
        when(employeeRepository.findByNameAndPassword(name, encodedPassword)).thenReturn(employee);
        Employee actualEmployee = employeeService.getEmployeeByNameAndPassword(name, password);
        assertEquals(objectMapper.writeValueAsString(employee), objectMapper.writeValueAsString(actualEmployee));
    }
    @Test
    public void should_return_true_when_call_doesEmplyeeHasNotFullParkingLots_with_employeeId_and_he_or_she_has_notFull_parking_lot(){
      Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));
      when(parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId)).thenReturn(1);
      boolean result = employeeService.doesEmplyeeHasNotFullParkingLots(employeeId);
        assertEquals(true,result);
    }
    @Test
    public void should_return_false_when_call_doesEmplyeeHasNotFullParkingLots_with_employeeId_and_he_or_she_has_not_notFull_parking_lot(){
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));
        when(parkingLotRepository.findALLNotFullParkingLotRowsByEmployeeId(employeeId)).thenReturn(0);
        boolean result = employeeService.doesEmplyeeHasNotFullParkingLots(employeeId);
        assertEquals(false,result);
    }
}