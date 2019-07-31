package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.enums.OrderTypeEnum;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.OrderNotExistedException;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.OrderRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.utils.RedisLock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceImplTest {

  private OrderService orderService;

  private OrderRepository orderRepository;

  private EmployeeRepository employeeRepository;

  private ParkingLotRepository parkingLotRepository;

  private ObjectMapper objectMapper;

  private RedisLock redisLock;

  @Before
  public void setUp() {
    redisLock = Mockito.mock(RedisLock.class);
    orderRepository = mock(OrderRepository.class);
    employeeRepository = mock(EmployeeRepository.class);
    parkingLotRepository = mock(ParkingLotRepository.class);
    orderService = new OrderServiceImpl(orderRepository, employeeRepository, parkingLotRepository);
    objectMapper = new ObjectMapper();
  }

  @Test
  public void should_add_new_order_when_create_new_order() throws JsonProcessingException {
    String carNumber = "123456";
    String address = "address";
    Long reservationTime = new Date().getTime();
    CreateOrderDto createOrderDto = new CreateOrderDto();
    createOrderDto.setCarNumber(carNumber);
    createOrderDto.setCustomerAddress(address);
    createOrderDto.setReservationTime(reservationTime);
    createOrderDto.setType(OrderTypeEnum.PARK_CAR.getCode());

    Order expectOrder = new Order();
    expectOrder.setCarNumber(carNumber);
    expectOrder.setCustomerAddress(address);
    expectOrder.setReservationTime(reservationTime);
    expectOrder.setType(OrderTypeEnum.PARK_CAR.getCode());
    expectOrder.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());
    Mockito.when(orderRepository.save(any())).thenReturn(expectOrder);
    Order actualOrder = orderService.addOrder(createOrderDto);
    expectOrder.setOrderId(actualOrder.getOrderId());

    assertEquals(
        objectMapper.writeValueAsString(expectOrder), objectMapper.writeValueAsString(actualOrder));
  }

  @Test
  public void should_get_order_when_get_order_by_id() throws JsonProcessingException {
    Long id = 1L;
    Order order = new Order();
    order.setId(id);

    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    Order actualOrder = orderService.getOrderById(id);

    assertEquals(
        objectMapper.writeValueAsString(order), objectMapper.writeValueAsString(actualOrder));
  }

  @Test
  public void should_throw_exception_when_get_order_by_invalid_id() {
    Long id = 1L;
    Order order = new Order();
    order.setId(id);

    when(orderRepository.findById(anyLong()))
        .thenThrow(new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));

    assertThrows(OrderNotExistedException.class, () -> orderService.getOrderById(id));
  }

  @Test
  public void should_return_order_List_when_get_orders_by_status() {
    Order order_1 = new Order();
    order_1.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());
    Order order_2 = new Order();
    order_2.setStatus(OrderStatusEnum.CONFIRM.getCode());
    List<Order> allOrders =
        new ArrayList<Order>() {
          {
            add(order_1);
            add(order_2);
          }
        };
    List<Order> expectOrders =
        new ArrayList<Order>() {
          {
            add(order_1);
          }
        };

    Mockito.when(orderRepository.findAll()).thenReturn(allOrders);
    List<Order> actualOrderList =
        orderService.getOrders(null, null, OrderStatusEnum.WAIT_FOR_RECEIVE.getCode(), null);

    assertEquals(expectOrders.size(), actualOrderList.size());
  }

  @Test
  public void should_return_Order_when_call_updateOrderStatusById_with_true_param()
      throws JsonProcessingException {
    Long id = 1L;
    Order order = new Order();
    order.setId(id);
    order.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());
    order.setType(OrderTypeEnum.FETCH_CAR.getCode());
    Order orderExpected = new Order();
    orderExpected.setId(id);
    orderExpected.setStatus(OrderStatusEnum.WAIT_FOR_CONFIRM.getCode());
    orderExpected.setType(OrderTypeEnum.FETCH_CAR.getCode());

    ParkingLot parkingLotInput = new ParkingLot();
    parkingLotInput.setId(1L);
    parkingLotInput.setOccupied(8);
    parkingLotInput.setCapacity(10);
    order.setParkingLot(parkingLotInput);
    ParkingLot parkingLotOutPut = new ParkingLot();
    BeanUtils.copyProperties(parkingLotInput, parkingLotOutPut);
    parkingLotOutPut.setOccupied(7);
    orderExpected.setParkingLot(parkingLotOutPut);

    ReflectionTestUtils.setField(
        orderService, OrderServiceImpl.class, "redisLock", redisLock, RedisLock.class);
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenReturn(orderExpected);
    when(redisLock.lock(any(String.class), any(String.class))).thenReturn(true);
    Order actualOrder = orderService.updateOrderById(id, orderExpected);
    assertEquals(
        objectMapper.writeValueAsString(orderExpected),
        objectMapper.writeValueAsString(actualOrder));
  }

  @Test
  public void should_return_Order_when_call_updateOrderStatusById_fetch_car_with_true_param()
      throws JsonProcessingException {
    Long id = 1L;
    Order orderOutput = new Order();
    orderOutput.setId(id);
    orderOutput.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());

    Order orderInput = new Order();
    orderInput.setId(id);
    orderInput.setType(OrderTypeEnum.FETCH_CAR.getCode());
    orderInput.setStatus(OrderStatusEnum.WAIT_FOR_CONFIRM.getCode());

    ParkingLot parkingLotInput = new ParkingLot();
    parkingLotInput.setId(1L);
    parkingLotInput.setOccupied(8);
    parkingLotInput.setCapacity(10);
    orderInput.setParkingLot(parkingLotInput);
    ParkingLot parkingLotOutPut = new ParkingLot();
    BeanUtils.copyProperties(parkingLotInput, parkingLotOutPut);
    parkingLotOutPut.setOccupied(7);
    orderOutput.setParkingLot(parkingLotOutPut);
    ReflectionTestUtils.setField(
        orderService, OrderServiceImpl.class, "redisLock", redisLock, RedisLock.class);
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(orderOutput));
    when(orderRepository.save(any())).thenReturn(orderInput);
    when(redisLock.lock(any(String.class), any(String.class))).thenReturn(true);
    when(parkingLotRepository.save(parkingLotInput)).thenReturn(parkingLotOutPut);
    Order orderActual = orderService.updateOrderById(id, orderInput);
    assertEquals(new Integer(7), orderActual.getParkingLot().getOccupied());
  }

  @Test
  public void should_return_Order_when_call_updateOrderStatusById_park_car_with_true_param()
      throws JsonProcessingException {
    Long id = 1L;
    Order orderOutput = new Order();
    orderOutput.setId(id);
    orderOutput.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());

    Order orderInput = new Order();
    orderInput.setId(id);
    orderInput.setType(OrderTypeEnum.PARK_CAR.getCode());
    orderInput.setStatus(OrderStatusEnum.WAIT_FOR_CONFIRM.getCode());

    ParkingLot parkingLotInput = new ParkingLot();
    parkingLotInput.setId(1L);
    parkingLotInput.setOccupied(8);
    parkingLotInput.setCapacity(10);
    orderInput.setParkingLot(parkingLotInput);
    ParkingLot parkingLotOutPut = new ParkingLot();
    BeanUtils.copyProperties(parkingLotInput, parkingLotOutPut);
    parkingLotOutPut.setOccupied(9);
    orderOutput.setParkingLot(parkingLotOutPut);
    ReflectionTestUtils.setField(
        orderService, OrderServiceImpl.class, "redisLock", redisLock, RedisLock.class);
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(orderOutput));
    when(orderRepository.save(any())).thenReturn(orderInput);
    when(redisLock.lock(any(String.class), any(String.class))).thenReturn(true);
    when(parkingLotRepository.save(parkingLotInput)).thenReturn(parkingLotOutPut);
    Order orderActual = orderService.updateOrderById(id, orderInput);
    assertEquals(new Integer(9), orderActual.getParkingLot().getOccupied());
  }

  @Test
  public void should_return_order_List_when_get_orders_order_by_reservation_time() {
    Order order_1 = new Order();
    order_1.setReservationTime(1L);
    Order order_2 = new Order();
    order_2.setReservationTime(2L);
    List<Order> allOrders =
        new ArrayList<Order>() {
          {
            add(order_1);
            add(order_2);
          }
        };
    List<Order> expectOrders =
        new ArrayList<Order>() {
          {
            add(order_2);
            add(order_1);
          }
        };

    Mockito.when(orderRepository.findAll()).thenReturn(allOrders);
    List<Order> actualOrderList = orderService.getOrders("reservationTime", "desc", null, null);

    assertEquals(expectOrders.size(), actualOrderList.size());
  }

  @Test
    public void should_return_order_List_when_get_orders_by_Employee_Id() {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        List<Order>orders=new ArrayList<>();
        orders.add(new Order("1", 1,  "A", 2, employee));
        Mockito.when(orderRepository.findByEmployee_Id(anyLong())).thenReturn(orders);
        List<Order> actualOrderList = orderService.getOrdersByEmployeeId(new Long((long)1));

        assertEquals(orders.size(), actualOrderList.size());
    }

    @Test
  public void should_assign_order_to_employee_when_manager_assign_order()
      throws JsonProcessingException {
    Order order = new Order();
    order.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());
    order.setType(OrderTypeEnum.FETCH_CAR.getCode());
    Employee employee = new Employee();
    employee.setId(1L);
    Order expectOrder = new Order();
    expectOrder.setStatus(OrderStatusEnum.WAIT_FOR_CONFIRM.getCode());
    expectOrder.setEmployee(employee);
    expectOrder.setType(OrderTypeEnum.FETCH_CAR.getCode());
    ParkingLot parkingLotInput = new ParkingLot();
    parkingLotInput.setId(1L);
    parkingLotInput.setOccupied(8);
    parkingLotInput.setCapacity(10);
    order.setParkingLot(parkingLotInput);
    ParkingLot parkingLotOutPut = new ParkingLot();
    BeanUtils.copyProperties(parkingLotInput, parkingLotOutPut);
    parkingLotOutPut.setOccupied(9);
    expectOrder.setParkingLot(parkingLotOutPut);
    ReflectionTestUtils.setField(
        orderService, OrderServiceImpl.class, "redisLock", redisLock, RedisLock.class);
    when(redisLock.lock(any(String.class), any(String.class))).thenReturn(true);
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenReturn(expectOrder);
    Order actualOrder = orderService.updateOrderById(1L, expectOrder);

    assertEquals(
        objectMapper.writeValueAsString(expectOrder), objectMapper.writeValueAsString(actualOrder));
  }

  @Test
  public void should_return_order_list_when_call_getAll_employees_with_true_param() {
    Order order_1 = new Order();
    order_1.setCarNumber("123");
    order_1.setId(1l);
    order_1.setOrderId("test");
    order_1.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());
    Order order_2 = new Order();
    order_2.setId(2l);
    order_2.setCarNumber("222");
    order_2.setOrderId("test2");
    order_2.setStatus(OrderStatusEnum.CONFIRM.getCode());
    List<Order> allOrders =
        new ArrayList<Order>() {
          {
            add(order_1);
            add(order_2);
          }
        };
    List<Order> expectOrders =
        new ArrayList<Order>() {
          {
            add(order_1);
          }
        };

    Mockito.when(orderRepository.findAll()).thenReturn(allOrders);
    List<Order> actualOrderList =
        orderService.getOrders("carNumber", "ASC", OrderStatusEnum.WAIT_FOR_RECEIVE.getCode(), "2");
    assertEquals(expectOrders.size(), actualOrderList.size());
  }
}
