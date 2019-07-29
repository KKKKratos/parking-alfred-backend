
package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.OrderNotExistedException;
import com.alfred.parkingalfred.exception.SecKillOrderException;
import com.alfred.parkingalfred.repository.OrderRepository;
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

  @Autowired
  private RedisLock redisLock;

  private static final int TIMEOUT = 10 * 1000;

  private final OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public Order addOrder(CreateOrderDto createOrderDto) {

    Order order = mapToOrder(createOrderDto);
    return orderRepository.save(order);
  }

  @Override
  public Order getOrderById(Long id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
  }

  @Override
  public Order updateOrderStatusById(Long id, Order order) {
    //lock
    long time = System.currentTimeMillis() + TIMEOUT;
    if (!redisLock.lock(id.toString(), String.valueOf(time))) {
      throw new SecKillOrderException(ResultEnum.RESOURCES_NOT_EXISTED);
    }
    Order orderFinded = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    Integer statusResult = orderFinded.getStatus();
    if (order.getStatus().equals(OrderStatusEnum.CONFIRM)){
      orderFinded.setStatus(order.getStatus());
    }else{
      if (statusResult.equals(OrderStatusEnum.WAIT_FOR_CONFIRM)) {
        throw new SecKillOrderException(ResultEnum.RESOURCES_NOT_EXISTED);
      } else {
        orderFinded.setStatus(order.getStatus());
      }
      //unlock
      redisLock.unlock(id.toString(), String.valueOf(time));
    }
    return orderRepository.save(orderFinded);
  }

  @Override
  public List<Order> getOrders(String sortProperty, String sortOrder, Integer filterStatus) {
      List<Order> orders = orderRepository.findAll();
      Stream<Order> stream = orders.stream();
      if (filterStatus != null) {
          stream = stream.filter(order -> order.getStatus().equals(filterStatus));
      }
      if (sortProperty == null || sortProperty.isEmpty()
              || sortOrder == null || sortOrder.isEmpty()
              || orders.isEmpty() || !ReflectionUtil.isComparablePropertyName(orders.get(0), sortProperty)) {
          return stream.collect(Collectors.toList());
      }
      return stream.sorted((first, second) -> {
          Comparable a = ReflectionUtil.getPropertyValue(first, sortProperty);
          Comparable b = ReflectionUtil.getPropertyValue(second, sortProperty);
          if (a == null || b == null)
              return 0;
          if ("ASC".equals(sortOrder.toUpperCase()))
              return a.compareTo(b);
          return b.compareTo(a);
      }).collect(Collectors.toList());
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
}
