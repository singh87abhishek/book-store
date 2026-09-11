package com.bookstore.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data 
public class OrderDto {
    private Long id;
    private String status;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    @Data 
    public static class OrderItemDto {
        private Long bookId;
        private String title;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;
        
    }
}
