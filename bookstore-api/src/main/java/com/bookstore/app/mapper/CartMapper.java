package com.bookstore.app.mapper;

import java.util.ArrayList;
import java.util.Map;

import com.bookstore.app.dto.CartDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.service.BookService;

public final class CartMapper {

    private CartMapper() {}

    public static CartDto buildCartDto(Map<Long, Integer> cartItems, BookService bookService) {
        CartDto.Builder builder = new CartDto.Builder();
        builder.items(new ArrayList<>());
        double totalPrice = 0;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Book book = bookService.findEntityById(entry.getKey());

            CartDto.CartItemDto item = new CartDto.CartItemDto.Builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .quantity(entry.getValue())
                .unitPrice(book.getPrice())
                .subtotal(book.getPrice() * entry.getValue())
                .build();

            totalPrice += item.getSubtotal();
            builder.addItem(item);
        }

        builder.totalPrice(totalPrice);
        return builder.build();
    }
}
