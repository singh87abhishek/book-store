package com.bookstore.app.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.bookstore.app.dto.CartDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.mapper.CartMapper;
import com.bookstore.app.service.CartService;
import com.bookstore.app.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final BookService bookService;

    //In memory storage for cart per user (username -> map of bookId -> quantity)
    private final Map<String, Map<Long, Integer>> userCarts = new ConcurrentHashMap<>();

    //Get the cart for a specific user
    @Override
    public CartDto getCart(String username) {
        log.debug("Fetching cart or user : {}", username);
        Map<Long, Integer> cartItems = userCarts.getOrDefault(username, new ConcurrentHashMap<>());
        return CartMapper.buildCartDto(cartItems, bookService);
    }

    //Add a book to the user's cart - check if the book exists and if there's enough stock
    @Override
    public CartDto addBookToCart(String username, Long bookId, int quantity) {
        log.debug("Adding bookId : {} quantity: {} to cart for User: {}", bookId, quantity, username);
        Book book = bookService.findEntityById(bookId); // Ensure the book exists
        Map<Long, Integer> cart = userCarts.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
        int currentQuantity = cart.getOrDefault(bookId, 0);
        int newQuantity = currentQuantity + quantity;

        // Check if the new quantity exceeds the available stock
        if(newQuantity > book.getStock()) {
            log.error("Insufficient stock for bookId: {} requested: {} available: {}", bookId, newQuantity, book.getStock());
            throw new BadRequestException("Not enough stock for book: " + book.getTitle());
        }

        cart.put(bookId, newQuantity);
        log.debug("Cart Updated for user: {}, bookId: {} newQuantity: {}", username, bookId, newQuantity);
        return CartMapper.buildCartDto(cart, bookService);
    }

    @Override
    public CartDto updateBookQuantity(String username, Long bookId, int quantity) {
        log.debug("Updating bookId: {} to quantity: {} for User: {}", bookId, quantity, username);

        // If quantity is zero or negative, remove the book from the cart
        if (quantity <= 0) {
            log.debug("Quantity <=0, removing bookId:{} from cart for user: {}", bookId, username);
            return removeBookFromCart(username, bookId);
        }

        Book book = bookService.findEntityById(bookId); // Ensure the book exists
        if (quantity > book.getStock()) {
            log.error("Insufficient Stock for bookId: {} requested:{} avaiable:{}", bookId, quantity, book.getStock());
            throw new BadRequestException("Not enough stock for book: " + book.getTitle());
        }

        Map<Long, Integer> cart = userCarts.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
        cart.put(bookId, quantity);
        return CartMapper.buildCartDto(cart, bookService);
    }

    @Override
    public CartDto removeBookFromCart(String username, Long bookId) {
        log.debug("Removing bookId: {} from cart for user: {}", bookId, username);
        Map<Long, Integer> cart = userCarts.getOrDefault(username, new ConcurrentHashMap<>());
        if (cart != null) {
            cart.remove(bookId);
        }
        return CartMapper.buildCartDto(cart != null ? cart : new ConcurrentHashMap<>(), bookService);
    }

    @Override
    public Map<Long, Integer> getCartItems(String userName) {
        return userCarts.getOrDefault(userName, new ConcurrentHashMap<>());
    }

    @Override
    public void clearCart(String userName) {
        log.debug("Clearing cart for User: {}", userName);
        userCarts.remove(userName);
    }
}
