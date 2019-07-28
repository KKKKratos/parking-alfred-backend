package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.entity.ParkingLot;

import java.util.List;

public interface OrderService {

    public List<Order> getOrders();
    public List<Order> getOrdersByTypeAndStatus(Integer type,Integer status);
    Order addOrder(CreateOrderDto createOrderDto);

    Order getOrderById(Long id);

    Order updateOrderStatusById(Long id, Order order);
}
