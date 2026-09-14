package com.bookstore.app.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.app.dto.CartDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.service.BookService;
import com.bookstore.app.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks 
    private CartServiceImpl cartService;

    @Mock 
    private BookService bookService;

    private Book book;

    @BeforeEach 
    public void setUp() {
        book = new Book(1L, "Test Book", "Test Author", 10.0, 5, "Test Description", "test_image_url.png");
    }

    @Test
    void testAddBookToCart() {
        // Implement test for adding a book to the cart
        when(bookService.findEntityById(1L)).thenReturn(book);
        CartDto cartDto = cartService.addBookToCart("testuser", 1L, 2);

        assertThat(cartDto.getItems()).hasSize(1);
        assertThat(cartDto.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cartDto.getItems().get(0).getTitle()).isEqualTo("Test Book");
        assertThat(cartDto.getTotalPrice()).isEqualTo(20.0);

    }

    @Test
    void testAddBook_throwsException_whenExceedsStock() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        
        assertThatThrownBy(() -> cartService.addBookToCart("testuser", 1L, 6))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Not enough stock");
        
    }

    @Test 
    void testUpdateBookQuantityInCart() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        cartService.addBookToCart("testuser", 1L, 2);
        CartDto cart = cartService.updateBookQuantity("testuser", 1L, 3);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test 
    void testRemoveBookFromCart() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        cartService.addBookToCart("testuser", 1L, 2);
        CartDto cart = cartService.removeBookFromCart("testuser", 1L);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test 
    void testGetCart_returnsEmptyCart() {
        CartDto cart = cartService.getCart("testuser");
        assertThat(cart.getItems()).isEmpty();
    }

    @Test 
    void testUpdateQuantity_throwsBadRequestException_whenExceedsStock() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        assertThatThrownBy(() -> cartService.updateBookQuantity("user1", 1L, 20))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessageContaining("Not enough stock for book: Test Book");
    }

    @Test 
    void carts_areIsolated_betweenDiffUsers() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        cartService.addBookToCart("user1", 1L, 1);

        CartDto user2Cart = cartService.getCart("user2");

        assertThat(user2Cart.getItems()).isEmpty();
    }

    @Test
    void testConcurrentAddBookToCart() throws InterruptedException {
        when(bookService.findEntityById(1L)).thenReturn(book);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    cartService.addBookToCart("concurrentUser", 1L, 1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // wait for all threads to finish
        executor.shutdown();

        CartDto cartDto = cartService.getCart("concurrentUser");

        // Expect 10 items total (quantity = 5)
        assertThat(cartDto.getItems()).hasSize(1);
        assertThat(cartDto.getItems().get(0).getQuantity()).isEqualTo(5);
        assertThat(cartDto.getTotalPrice()).isEqualTo(5 * book.getPrice());
    }
}
