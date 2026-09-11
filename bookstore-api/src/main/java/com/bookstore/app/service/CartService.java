package com.bookstore.app.service;

import java.util.Map;

import com.bookstore.app.dto.CartDto;

public interface CartService {
    CartDto getCart(String username);
    CartDto addBookToCart(String username, Long bookId, int quantity);
    CartDto updateBookQuantity(String username, Long bookId, int quantity);
    CartDto removeBookFromCart(String username, Long bookId);
    Map<Long, Integer> getCartItems(String userName);
    void clearCart(String userName);
}