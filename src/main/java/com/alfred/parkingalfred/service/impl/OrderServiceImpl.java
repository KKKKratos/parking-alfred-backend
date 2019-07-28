package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.OrderNotExistedException;
import com.alfred.parkingalfred.repository.OrderRepository;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.utils.UUIDUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

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
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
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
}
