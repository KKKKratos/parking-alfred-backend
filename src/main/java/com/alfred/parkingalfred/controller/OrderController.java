package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.vo.ResultVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResultVO getOrders(@RequestParam(name = "sortProperty", required = false) String sortProperty,
                              @RequestParam(name = "sortOrder", required = false) String sortOrder,
                              @RequestParam(name = "status", required = false) Integer filterStatus) {
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.getOrders(sortProperty, sortOrder, filterStatus));
    }

    @PostMapping("/orders")
    public ResultVO createOrder(@RequestBody CreateOrderDto createOrderDto) {
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.addOrder(createOrderDto));
    }

    @GetMapping("/orders/{id}")
    public ResultVO getOrderById(@PathVariable Long id) {
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.getOrderById(id));
    }

    @PutMapping(value = "/orders/{id}")
    public ResultVO updateOrderById(@PathVariable Long id, @RequestBody Order order) {
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.updateOrderById(id, order));
    }
}
