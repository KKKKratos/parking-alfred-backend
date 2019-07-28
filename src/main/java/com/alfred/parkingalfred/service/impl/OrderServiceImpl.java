
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
import com.alfred.parkingalfred.utils.UUIDUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private RedisLock redisLock;
  private static final int TIMEOUT = 10 * 1000;//超时时间 10s

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
    if (statusResult.equals(OrderStatusEnum.CONFIRM)){
      throw new SecKillOrderException(ResultEnum.RESOURCES_NOT_EXISTED);
    }else{
      orderFinded.setStatus(order.getStatus());
    }
    //unlock
    redisLock.unlock(id.toString(),String.valueOf(time));
    return orderRepository.save(orderFinded);
  }

  @Override
  public List<Order> getOrders() {
    return orderRepository.findAll();
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

  @Override
    public List<Order> getOrdersByStatus(int[] status){
        List<Order>result=new ArrayList<>();
        for (int i=0;i<status.length;i++){
            result.addAll( orderRepository.findOrdersByStatus(Integer.valueOf(status[i])));
        };
        return result;
  }
}
