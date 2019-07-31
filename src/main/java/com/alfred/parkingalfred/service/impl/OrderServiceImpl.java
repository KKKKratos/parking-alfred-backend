package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.entity.ParkingLot;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.enums.OrderTypeEnum;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.exception.OrderNotExistedException;
import com.alfred.parkingalfred.exception.SecKillOrderException;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.repository.OrderRepository;
import com.alfred.parkingalfred.repository.ParkingLotRepository;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.utils.RedisLock;
import com.alfred.parkingalfred.utils.ReflectionUtil;
import com.alfred.parkingalfred.utils.UUIDUtil;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;

  @Autowired private RedisLock redisLock;

  private static final int TIMEOUT = 10 * 1000;


  private final ParkingLotRepository parkingLotRepository;
  public OrderServiceImpl(OrderRepository orderRepository, EmployeeRepository employeeRepository,
                          ParkingLotRepository parkingLotRepository) {
    this.orderRepository = orderRepository;
    this.employeeRepository = employeeRepository;
    this.parkingLotRepository=parkingLotRepository;
  }

  @Override
  public Order addOrder(CreateOrderDto createOrderDto) {

    Order order = mapToOrder(createOrderDto);
    return orderRepository.save(order);
  }

  @Override
  public Order getOrderById(Long id) {
    return orderRepository
        .findById(id)
        .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
  }

  @Override
  public Order updateOrderById(Long id, Order order) {
    // lock
    long time = System.currentTimeMillis() + TIMEOUT;
    if (!redisLock.lock(id.toString(), String.valueOf(time))) {
      throw new SecKillOrderException(ResultEnum.RESOURCES_NOT_EXISTED);
    }
    Order orderFinded =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    Integer statusResult = orderFinded.getStatus();
    if (order.getStatus().equals(OrderStatusEnum.CONFIRM)) {
      this.updateOrder(orderFinded, order);
    } else {
      if (statusResult.equals(OrderStatusEnum.WAIT_FOR_CONFIRM)) {
        throw new SecKillOrderException(ResultEnum.RESOURCES_NOT_EXISTED);
      } else {
        this.updateOrder(orderFinded, order);
      }
      // unlock
      redisLock.unlock(id.toString(), String.valueOf(time));
    }
    return orderRepository.save(orderFinded);
  }

  @Override
  public List<Order> getOrders(
      String sortProperty, String sortOrder, Integer filterStatus, String carNumber) {
    List<Order> orders = orderRepository.findAll();
    Stream<Order> stream = orders.stream();
    if (filterStatus != null) {
      stream = stream.filter(order -> order.getStatus().equals(filterStatus));
    }
    if (carNumber != null) {
      stream = stream.filter(order -> order.getCarNumber().contains(carNumber));
    }
    if (sortProperty == null
        || sortProperty.isEmpty()
        || sortOrder == null
        || sortOrder.isEmpty()
        || orders.isEmpty()
        || !ReflectionUtil.isComparablePropertyName(orders.get(0), sortProperty)) {
      return stream.collect(Collectors.toList());
    }
    return stream
        .sorted(
            (first, second) -> {
              Comparable a = ReflectionUtil.getPropertyValue(first, sortProperty);
              Comparable b = ReflectionUtil.getPropertyValue(second, sortProperty);
              if (a == null || b == null) return 0;
              if ("ASC".equals(sortOrder.toUpperCase())) return a.compareTo(b);
              return b.compareTo(a);
            })
        .collect(Collectors.toList());
  }

  private Order mapToOrder(CreateOrderDto createOrderDto) {
    Order order = new Order();
    order.setCarNumber(createOrderDto.getCarNumber());
    order.setType(createOrderDto.getType());
    order.setCustomerAddress(createOrderDto.getCustomerAddress());
    order.setReservationTime(createOrderDto.getReservationTime());
    order.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());
    order.setOrderId(UUIDUtil.generateUUID());
    return order;
  }

  private void updateOrder(Order orderFinded, Order order) {
    if (order.getType() != null) {
      orderFinded.setType(order.getType());
    }
    if (order.getReservationTime() != null) {
      orderFinded.setReservationTime(order.getReservationTime());
    }
    if (order.getCustomerAddress() != null) {
      orderFinded.setCustomerAddress(order.getCustomerAddress());
    }
    if (order.getStatus() != null) {
      orderFinded.setStatus(order.getStatus());
      if (order.getType().equals(OrderTypeEnum.PARK_CAR.getCode())&&order.getStatus()
        .equals(OrderStatusEnum.WAIT_FOR_CONFIRM.getCode())){
        ParkingLot parkingLot =order.getParkingLot();
        parkingLot.setOccupied(parkingLot.getOccupied()+1);
        parkingLotRepository.save(parkingLot);
      }
      if (order.getType().equals(OrderTypeEnum.FETCH_CAR.getCode())&&order.getStatus()
        .equals(OrderStatusEnum.WAIT_FOR_CONFIRM.getCode())){
        ParkingLot parkingLot =order.getParkingLot();
        parkingLot.setOccupied(parkingLot.getOccupied()-1);
        parkingLotRepository.save(parkingLot);
      }
    }
    if (order.getEmployee() != null && order.getEmployee().getId() != null) {
      updateOrderEmployee(orderFinded, order);
    }
    if (order.getCarNumber() != null) {
      orderFinded.setCarNumber(order.getCarNumber());
    }
    if (order.getParkingLot() != null) {
      orderFinded.setParkingLot(order.getParkingLot());
    }
  }

  private void updateOrderEmployee(Order orderFinded, Order order) {
    Employee employee =
        employeeRepository
            .findById(order.getEmployee().getId())
            .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    orderFinded.setEmployee(employee);
  }
    @Override
    public List<Order> getOrdersByEmployeeId(Long id) {
        return orderRepository.findByEmployee_Id(id);
    }
}
