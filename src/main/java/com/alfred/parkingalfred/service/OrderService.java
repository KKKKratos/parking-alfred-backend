package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrders();

    Order addOrder(CreateOrderDto createOrderDto);

    Order getOrderById(Long id);
}
