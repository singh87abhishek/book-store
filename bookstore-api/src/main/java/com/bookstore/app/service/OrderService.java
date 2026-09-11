package com.bookstore.app.service;

import java.util.List;
import com.bookstore.app.dto.OrderDto;

public interface OrderService {
    List<OrderDto> getUserOrders(String userName);
    OrderDto checkOut(String userName);
}
