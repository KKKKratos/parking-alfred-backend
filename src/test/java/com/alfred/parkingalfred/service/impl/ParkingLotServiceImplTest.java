package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.enums.ParkingLotStatusEnum;
import com.alfred.parkingalfred.enums.RoleEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.form.ParkingLotForm;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.alfred.parkingalfred.service.ParkingLotService;
import com.alfred.parkingalfred.utils.RedisLock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public  class ParkingLotServiceImplTest {

  private ParkingLotRepository parkingLotRepository;
  private EmployeeRepository employeeRepository;
  private ParkingLotServiceImpl parkingLotServiceImpl;
  private ObjectMapper objectMapper;

  @Before
  public void setUp(){
    employeeRepository = Mockito.mock(EmployeeRepository.class);
    parkingLotRepository = Mockito.mock(ParkingLotRepository.class);
    parkingLotServiceImpl = new ParkingLotServiceImpl(parkingLotRepository, employeeRepository);
    objectMapper = new ObjectMapper();
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
    when((
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
    when(
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
    when(
        parkingLotRepository.save(any(ParkingLot.class))
    ).thenReturn(parkingLotExpected);
    ReflectionTestUtils.setField(parkingLotServiceImpl,ParkingLotServiceImpl.class
        ,"parkingLotRepository",parkingLotRepository,ParkingLotRepository.class);
    ParkingLot parkingLotResult = parkingLotServiceImpl.createParkingLot(parkingLotForm);
    Assert.assertEquals(parkingLotExpected.getId(),parkingLotResult.getId());
  }

  @Test
  public void should_return_parkingLots_when_call_getAllParkingLotByPageAndSize(){
    PageRequest pageRequest = PageRequest.of(0,10);
    List<ParkingLot> parkingLotList = new ArrayList<ParkingLot>(){
      {
        add(new ParkingLot());
        add(new ParkingLot());
        add(new ParkingLot());
      }
    };
    PageImpl<ParkingLot>  parkingLotPage = new PageImpl<>(parkingLotList);
    when(
        parkingLotRepository.findAllByNameLike(anyString(), any(PageRequest.class))
    ).thenReturn(parkingLotPage);
    Page<ParkingLot> parkingLotPageResult = parkingLotRepository.findAllByNameLike("", pageRequest);
    Assert.assertEquals(3,parkingLotPageResult.getContent().size());
  }

  @Test
  public void should_return_parkingLots_when_search_by_name() {
    String name = "name";
    ParkingLot parkingLot = new ParkingLot();
    parkingLot.setName(name);

    List<ParkingLot> expectParkingLots = new ArrayList<ParkingLot>() {{
      add(parkingLot);
    }};
    PageImpl<ParkingLot> parkingLotPage = new PageImpl<>(expectParkingLots);
    when(parkingLotRepository.findAllByNameLike(anyString(), any(PageRequest.class))).thenReturn(parkingLotPage);
    List<ParkingLot> actualParkingLots = parkingLotServiceImpl.getAllParkingLotsWithFilterByPageAndSize(1, 1, name);

    assertIterableEquals(expectParkingLots, actualParkingLots);
  }

  @Test
  public void should_return_parkingLot_when_call_updateParkingLot_with_true_param() throws JsonProcessingException {
    Long id = 1L;
    ParkingLot parkingLot = new ParkingLot();
    parkingLot.setId(id);
    parkingLot.setStatus(ParkingLotStatusEnum.USABLE.getCode());

    ParkingLot parkingLotExpected = new ParkingLot();
    parkingLotExpected.setId(id);
    parkingLotExpected.setStatus(ParkingLotStatusEnum.USABLE.getCode());

    when(parkingLotRepository.findById(anyLong())).thenReturn(Optional.of(parkingLotExpected));
    when(parkingLotRepository.save(any())).thenReturn(parkingLotExpected);

    ParkingLot actualParkingLot = parkingLotServiceImpl.updateParkingLotById(id, parkingLot);
    assertEquals(objectMapper.writeValueAsString(parkingLot),
            objectMapper.writeValueAsString(actualParkingLot));

  }
  @Test
  public void should_return_parkingLot_when_call_update_updateParkingLotById_with_all_not_null_param(){
    ParkingLot parkingLotInput = new ParkingLot();
    parkingLotInput.setName("32321");
    parkingLotInput.setCapacity(100);
    parkingLotInput.setOccupied(100);
    parkingLotInput.setStatus(ParkingLotStatusEnum.USABLE.getCode());
    Long id = 1l;
    ParkingLot parkingLotExpected = new ParkingLot();
    when(parkingLotRepository.findById(anyLong())).thenReturn(Optional.of(parkingLotExpected));
    when(parkingLotRepository.save(any(ParkingLot.class))).thenReturn(parkingLotInput);
    ParkingLot parkingLotActual = parkingLotServiceImpl.updateParkingLotById(id,parkingLotInput);
    Assert.assertEquals(parkingLotActual.getName(),parkingLotInput.getName());
  }
  @Test
  public void should_return_count_when_call_getParkingLotCount_with_true_param(){
    when(parkingLotRepository.getParkingLotCount()).thenReturn(3);
    assertEquals(3,parkingLotServiceImpl.getParkingLotCount());
  }
  @Test
  public void should_return_parkingLot_list_when_call_getAllParkingLotsWithFilterByPageAndSize(){
    int page =1,size=10;
    String name = "zhangrun";
    List<ParkingLot> parkingLots = new  ArrayList<ParkingLot>(){{
      add(new ParkingLot());
      add(new ParkingLot());
      add(new ParkingLot());
      add(new ParkingLot());
      add(new ParkingLot());
      add(new ParkingLot());
    }};
    PageImpl<ParkingLot> parkingLotPage = new PageImpl<>(parkingLots);
    ReflectionTestUtils.setField(parkingLotServiceImpl,ParkingLotServiceImpl.class
      ,"parkingLotRepository",parkingLotRepository,ParkingLotRepository.class);
    when(parkingLotRepository.findAllByNameLike(anyString(),any(Pageable.class)))
      .thenReturn(parkingLotPage);
    List<ParkingLot> parkingLotListOutput = parkingLotServiceImpl
      .getAllParkingLotsWithFilterByPageAndSize(page,size,null);
    assertEquals(6,parkingLotListOutput.size());
  }
}