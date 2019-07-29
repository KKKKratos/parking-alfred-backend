package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.form.ParkingLotForm;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

public  class ParkingLotServiceImplTest {

  private ParkingLotRepository parkingLotRepository;
  private EmployeeRepository employeeRepository;
  private ParkingLotServiceImpl parkingLotServiceImpl;

  @Before
  public void setUp(){
    employeeRepository = Mockito.mock(EmployeeRepository.class);
    parkingLotRepository = Mockito.mock(ParkingLotRepository.class);
    parkingLotServiceImpl = new ParkingLotServiceImpl(parkingLotRepository, employeeRepository);
  }
  @Test
  public  void should_return_parkingLots_of_employee_when_call_getAllParkingLotsByEmployeeId_with_true_employeeId(){
    ParkingLot parkingLot1 = new ParkingLot((long) 1,"test lot1",100,100);
    ParkingLot parkingLot2 = new ParkingLot((long) 2,"test lot2",100,100);
    List<ParkingLot> parkingLots=Arrays.asList(parkingLot1,parkingLot2);
    Long parkingBoy = new Long(1);
    Employee employee = new Employee();
    employee.setId(parkingBoy);
    employee.setParkingLots(parkingLots);
    ReflectionTestUtils.setField(parkingLotServiceImpl,ParkingLotServiceImpl.class
        ,"employeeRepository",employeeRepository,EmployeeRepository.class);
    Mockito.when((
        employeeRepository.findById(Mockito.anyLong())
    )).thenReturn(java.util.Optional.of(employee));
    List<ParkingLot>parkingLotsResult = parkingLotServiceImpl.getParkingLotsByParkingBoyId((long)1);
    Assert.assertEquals(2,parkingLotsResult.size());
  }
  @Test(expected =EmployeeNotExistedException.class )
  public  void should_return_Exception_when_call_getAllParkingLotsByEmployeeId_with_wrong_employeeId(){
    ReflectionTestUtils.setField(parkingLotServiceImpl,ParkingLotServiceImpl.class
        ,"employeeRepository",employeeRepository,EmployeeRepository.class);
    Optional<Employee> empty = Optional.empty();
    Mockito.when(
        employeeRepository.findById(Mockito.anyLong()
    )).thenReturn(empty);
      List<ParkingLot>parkingLotsResult = parkingLotServiceImpl.getParkingLotsByParkingBoyId(1L);
  }
  @Test
  public void should_return_parkingLot_when_call_createParkingLot_API_with_true_param(){
    ParkingLotForm parkingLotForm = new ParkingLotForm();
    parkingLotForm.setCapacity(100);
    parkingLotForm.setName("test停车场");
    parkingLotForm.setOccupied(99);

    ParkingLot parkingLotExpected = new ParkingLot();
    BeanUtils.copyProperties(parkingLotForm,parkingLotExpected);
    parkingLotExpected.setId(1L);
    Mockito.when(
        parkingLotRepository.save(Mockito.any(ParkingLot.class))
    ).thenReturn(parkingLotExpected);
    ReflectionTestUtils.setField(parkingLotServiceImpl,ParkingLotServiceImpl.class
        ,"parkingLotRepository",parkingLotRepository,ParkingLotRepository.class);
    ParkingLot parkingLotResult = parkingLotServiceImpl.createParkingLot(parkingLotForm);
    Assert.assertEquals(parkingLotExpected.getId(),parkingLotResult.getId());
  }
}