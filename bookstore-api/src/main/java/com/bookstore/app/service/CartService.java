package com.bookstore.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.bookstore.app.dto.CartDto;
import com.bookstore.app.entity.Book;
import lombok.RequiredArgsConstructor;

/**
 * CartService
 */
@Service 
@RequiredArgsConstructor 
public class CartService {
    private final BookService bookService;

    //In memory storage for cart per user (username -> map of bookId -> quantity)
    private final Map<String, Map<Long, Integer>> userCarts = new ConcurrentHashMap<>();

    //Get the cart for a specific user
    public CartDto getCart(String username) {
        Map<Long, Integer> cartItems = userCarts.getOrDefault(username, new ConcurrentHashMap<>());
        return buildCartDto(cartItems);
    }


    //Add a book to the user's cart - chceck if the book exists and if there's enough stock
	public CartDto addBookToCart(String username, Long bookId, int quantity) {
		Book book = bookService.findEntityById(bookId); // Ensure the book exists
        Map<Long, Integer> cart = userCarts.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
        int currentQuantity = cart.getOrDefault(bookId, 0);
        int newQuantity = currentQuantity + quantity;

        // Check if the new quantity exceeds the available stock
        if(newQuantity > book.getStock()) {
            throw new RuntimeException("Not enough stock for book: " + book.getTitle());
        }

        cart.put(bookId, newQuantity);
        return buildCartDto(cart);
    
	}

    public CartDto updateBookQuantity(String username, Long bookId, int quantity) {
        // If quantity is zero or negative, remove the book from the cart
        if (quantity <= 0) {
            return removeBookFromCart(username, bookId);
        }

        Book book = bookService.findEntityById(bookId); // Ensure the book exists
        if (quantity > book.getStock()) {
            throw new RuntimeException("Not enough stock for book: " + book.getTitle());
        }

        Map<Long, Integer> cart = userCarts.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
        cart.put(bookId, quantity);
        return buildCartDto(cart);
    }

    public CartDto removeBookFromCart(String username, Long bookId) {
        Map<Long, Integer> cart = userCarts.getOrDefault(username, new ConcurrentHashMap<>());
        if (cart != null) {
            cart.remove(bookId);
        }
        return buildCartDto(cart != null ? cart : new ConcurrentHashMap<>());
    }

    //Build a CartDto from the cart items
    private CartDto buildCartDto(Map<Long, Integer> cartItems) {
        CartDto cartDto = new CartDto();
        List<CartDto.CartItemDto> items = new ArrayList<>();
        double totalPrice = 0;
        

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Book book = bookService.findEntityById(entry.getKey());

            CartDto.CartItemDto item = new CartDto.CartItemDto();
            item.setBookId(book.getId());
            item.setTitle(book.getTitle());
            item.setAuthor(book.getAuthor());
            item.setQuantity(entry.getValue());
            item.setUnitPrice(book.getPrice());
            item.setSubtotal(item.getUnitPrice() * item.getQuantity());
            totalPrice += item.getSubtotal();
            items.add(item);
        }
        cartDto.setItems(items);
        cartDto.setTotalPrice(totalPrice);
        return cartDto;
    }

}
