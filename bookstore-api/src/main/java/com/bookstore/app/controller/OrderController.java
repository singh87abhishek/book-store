package com.bookstore.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import com.bookstore.app.dto.OrderDto;
import com.bookstore.app.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController 
@RequestMapping ("/bookstore-api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping 
    public ResponseEntity<List<OrderDto>> getUserOrders(@AuthenticationPrincipal UserDetails user) {
        log.debug("Fetching orders for user: {}", user.getUsername());
        List<OrderDto> orders = orderService.getUserOrders(user.getUsername());
        log.debug("Returning {} orders for user: {}", orders.size(), user.getUsername());

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkOut(@AuthenticationPrincipal UserDetails user) {
        log.debug("Checkout request for user: {}", user.getUsername());
        OrderDto order = orderService.checkOut(user.getUsername());
        log.debug("Order Placed successfully for user: {}, orderId: {}", user.getUsername(), order.getId());
    
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    

}
