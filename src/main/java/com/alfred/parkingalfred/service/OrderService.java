package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;

import com.alfred.parkingalfred.entity.ParkingLot;
import java.util.List;

public interface OrderService {

    List<Order> getOrders(String sortProperty, String sortOrder, Integer filterStatus,String carNumber);

    Order addOrder(CreateOrderDto createOrderDto);

    Order getOrderById(Long id);

    Order updateOrderById(Long id, Order order);

    List<Order> getOrdersByEmployeeId(Long id);
}
