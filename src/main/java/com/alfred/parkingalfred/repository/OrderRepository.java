package com.alfred.parkingalfred.repository;

import com.alfred.parkingalfred.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order>findOrdersByTypeAndStatus(Integer type,Integer status);
}
