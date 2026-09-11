package com.bookstore.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dto.CartDto;
import com.bookstore.app.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/bookstore-api/cart")
@RequiredArgsConstructor 
public class CartController {

    private final CartService cartService;

    @GetMapping 
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cartService.getCart(user.getUsername()));
    }

    @PostMapping("add/{bookId}")
    public ResponseEntity<CartDto> addBookToCart(@AuthenticationPrincipal UserDetails user, @PathVariable Long bookId,
                                                    @RequestParam(defaultValue = "1") int quantity) {
        // Implementation for adding book to cart
        return ResponseEntity.ok(cartService.addBookToCart(user.getUsername(), bookId, quantity));
    }

    @PutMapping ("update/{bookId}")
    public ResponseEntity<CartDto> updateBookQuantityInCart(@AuthenticationPrincipal UserDetails user, @PathVariable Long bookId,
                                                                @RequestParam int quantity) {
        // Implementation for updating book quantity in cart
        return ResponseEntity.ok(cartService.updateBookQuantity(user.getUsername(), bookId, quantity));
    }

    @DeleteMapping("remove/{bookId}")
    public ResponseEntity<CartDto> removeBookFromCart(@AuthenticationPrincipal UserDetails user, @PathVariable Long bookId) {
        // Implementation for removing book from cart
        return ResponseEntity.ok(cartService.removeBookFromCart(user.getUsername(), bookId));
    }

}
