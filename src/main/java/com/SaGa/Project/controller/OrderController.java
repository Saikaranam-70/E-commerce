package com.SaGa.Project.controller;

import com.SaGa.Project.model.Order;
import com.SaGa.Project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Order> addOrder(@RequestBody Order order){
        Order newOrder = orderService.addOrder(order);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @PutMapping("/updateOrder")
    public ResponseEntity<Order> updateOrder(@RequestBody Order updatedOrder){
        Order order = orderService.updateOrder(updatedOrder);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders(@RequestBody String adminUserId){
        List<Order> orders = orderService.getAllOrders(adminUserId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId, @RequestParam String adminUserId) {
        Order order = orderService.getOrderById(orderId, adminUserId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
