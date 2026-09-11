package com.bookstore.app.mapper;

import java.util.stream.Collectors;

import com.bookstore.app.dto.OrderDto;
import com.bookstore.app.entity.Order;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderDto toDto(Order order) {
        if (order == null) return null;

        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setStatus(order.getStatus());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setCreatedAt(order.getCreatedAt());

        orderDto.setItems(order.getItems().stream().map(item -> {
            OrderDto.OrderItemDto i = new OrderDto.OrderItemDto();
            i.setBookId(item.getBook().getId());
            i.setTitle(item.getBook().getTitle());
            i.setQuantity(item.getQuantity());
            i.setUnitPrice(item.getUnitPrice());
            i.setSubtotal(item.getUnitPrice() * item.getQuantity());
            return i;
        }).collect(Collectors.toList()));

        return orderDto;
    }
}
