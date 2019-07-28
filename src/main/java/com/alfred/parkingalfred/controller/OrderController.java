package com.alfred.parkingalfred.controller;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.vo.ResultVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResultVO getOrders() {
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.getOrders());
    }

    @PostMapping("/orders")
    public ResultVO createOrder(@RequestBody CreateOrderDto createOrderDto) {
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.addOrder(createOrderDto));
    }

    @GetMapping("/orders/{id}")
    public ResultVO getOrderById(@PathVariable Long id) {
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.getOrderById(id));
    }
    @GetMapping(value = "/orders",params = {"type","status"})
    public ResultVO getParcelsByStatus(@RequestParam String type,@RequestParam String status){
        return new ResultVO<List>(200,"Return filteredOrders", orderService.getOrdersByTypeAndStatus(Integer.valueOf(type),Integer.valueOf(status)));
    }


    @PutMapping(value="/orders/{id}")
    public ResultVO updateOrderStatusById(@PathVariable Long id,@RequestBody Order order){
        return new ResultVO<>(ResultEnum.SUCCESS.getStatus(), null, orderService.updateOrderStatusById(id,order));
    }
}
