package com.bookstore.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.app.dto.OrderDto;
import com.bookstore.app.entity.Order;
import com.bookstore.app.entity.User;
import com.bookstore.app.repository.OrderRepository;
import com.bookstore.app.repository.UserRepository;
import com.bookstore.app.exception.ResourceNotFoundException;
import com.bookstore.app.factory.OrderFactory;
import com.bookstore.app.mapper.OrderMapper;
import com.bookstore.app.service.CartService;
import com.bookstore.app.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final OrderFactory orderFactory;

    @Override
    public List<OrderDto> getUserOrders(String userName) {
        log.debug("Fetching orders for user : {}", userName);
        User user = getUser(userName);

        List<OrderDto> orders = orderRepository.findByUserOrderByCreatedAtDesc(user)
                        .stream()
                        .map(this::toDto).collect(Collectors.toList());
        
        log.debug("Found {} orders for User: {}", orders.size(), userName);
        return orders;
    }

    @Override
    @Transactional
    public OrderDto checkOut(String userName) {
        log.debug("Checkout started for User: {}", userName);
        User user = getUser(userName);

        Map<Long, Integer> cartItems = cartService.getCartItems(userName);
        
        Order order = orderFactory.createOrder(user, cartItems);

        Order saved = orderRepository.save(order);
        
        //Clear the cart of the user
        cartService.clearCart(userName);

        log.debug("Order placed successfuly for user: {} orderId: {} total:{}", userName, saved.getId(), saved.getTotalAmount());
        return toDto(saved);
    }

    private OrderDto toDto(Order order) {
        return OrderMapper.toDto(order);
    }

    private User getUser(String userName) {
        return userRepository.findByUsername(userName)
                    .orElseThrow(() -> {
                        log.error("Fetch orders failed - user not found: {}", userName);
                        return new ResourceNotFoundException("User not found");
                    });
    }
}
