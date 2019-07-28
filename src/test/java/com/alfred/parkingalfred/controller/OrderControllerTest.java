package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private List<Order> orders;

    @Before
    public void setUp() {
        orders = new ArrayList<>();
        orders.add(new Order( "1", 1,"南方软件园", 1));
        orders.add(new Order("2", 1,"凤凰兰亭", 2));
        orders.add(new Order("3", 1,"红树东岸", 3));
    }

    @Test
    public void should_return_orders_when_get_it() throws Exception {
        when(orderService.getOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(jsonPath("$.data.length()")
                        .value(3));
    }
    @Test
    public void should_return_orders_when_get_it_by_type_and_status() throws Exception {
        List<Order>newOrders = new ArrayList<>();
        newOrders.add(new Order("1", 1,"南方软件园", 1));
        when(orderService.getOrdersByTypeAndStatus(any(Integer.class),any(Integer.class))).thenReturn(newOrders);
        mockMvc.perform(get("/orders").param("type","1").param("status","1"))
                .andExpect(jsonPath("$.data.length()")
                        .value(1));
    }

    @Test
    public void should_return_order_when_add_new_order() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto();

        Order order = new Order();
        order.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());

        when(orderService.addOrder(createOrderDto)).thenReturn(order);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_order_when_get_order_by_id() throws Exception {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        when(orderService.getOrderById(anyLong())).thenReturn(order);

        Employee employee = new Employee();
        employee.setId(id);
        String token = JwtUtil.generateToken(employee);

        mockMvc.perform(get("/orders/{id}", id)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_updated_order_when_update_by_id() throws Exception {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);

        Order orderExpected = new Order();
        orderExpected.setId(id);
        orderExpected.setStatus(2);

        when(orderService.updateOrderStatusById((long) 1,order))
                .thenReturn(orderExpected);

        Employee employee = new Employee();
        employee.setId(id);
        String token = JwtUtil.generateToken(employee);

        mockMvc.perform(get("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
