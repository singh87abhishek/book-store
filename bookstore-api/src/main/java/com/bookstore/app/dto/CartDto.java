package com.bookstore.app.dto;

import java.util.List;

import lombok.Data;

/**
 * CartDto
 */
@Data 
public class CartDto {
    private List<CartItemDto> items;
    private Double totalPrice;

    @Data
    public static class CartItemDto {
        private Long bookId;
        private String title;
        private String author;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;
    }

}
