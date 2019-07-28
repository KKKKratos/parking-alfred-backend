package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.enums.OrderTypeEnum;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.OrderNotExistedException;
import com.alfred.parkingalfred.repository.OrderRepository;
import com.alfred.parkingalfred.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceImplTest {

    private OrderService orderService;

    private OrderRepository orderRepository;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderServiceImpl(orderRepository);
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

        assertEquals(objectMapper.writeValueAsString(expectOrder), objectMapper.writeValueAsString(actualOrder));
    }

    @Test
    public void should_get_order_when_get_order_by_id() throws JsonProcessingException {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        Order actualOrder = orderService.getOrderById(id);

        assertEquals(objectMapper.writeValueAsString(order), objectMapper.writeValueAsString(actualOrder));
    }

    @Test
    public void should_throw_exception_when_get_order_by_invalid_id() {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);

        when(orderRepository.findById(anyLong())).thenThrow(new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));

        assertThrows(OrderNotExistedException.class, () -> orderService.getOrderById(id));
    }

    @Test
    public void should_throw_exception_when_get_order_by_invalid_id1() throws JsonProcessingException {
        Long id = 1L;

        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());

        Order orderExpected = new Order();
        orderExpected.setId(id);
        orderExpected.setStatus(OrderStatusEnum.WAIT_FOR_CONFIRM.getCode());

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(orderExpected);
        Order actualOrder = orderService.updateOrderStatusById(id, orderExpected);

        assertEquals(objectMapper.writeValueAsString(orderExpected), objectMapper.writeValueAsString(actualOrder));
    }
}