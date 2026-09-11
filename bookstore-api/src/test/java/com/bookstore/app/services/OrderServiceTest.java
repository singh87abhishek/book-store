package com.bookstore.app.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.app.dto.OrderDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.entity.Order;
import com.bookstore.app.entity.OrderItem;
import com.bookstore.app.entity.User;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.exception.ResourceNotFoundException;
import com.bookstore.app.repository.OrderRepository;
import com.bookstore.app.repository.UserRepository;
import com.bookstore.app.service.BookService;
import com.bookstore.app.service.CartService;
import com.bookstore.app.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@bookstore.com");

        book = new Book(1L, "Test Book", "Test Author", 10.0, 5, "desc", "img.png");
    }

    @Test
    void testCheckOut_success_singleItem() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartService.getCartItems("testuser")).thenReturn(Map.of(1L, 2));
        when(bookService.findEntityById(1L)).thenReturn(book);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderDto dto = orderService.checkOut("testuser");

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getTotalAmount()).isEqualTo(20.0);
        assertThat(dto.getItems().get(0).getBookId()).isEqualTo(1L);

        verify(bookService).save(book);
    }

    @Test
    void testCheckOut_throwsBadRequest_whenCartEmpty() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartService.getCartItems("testuser")).thenReturn(Map.of());

        assertThatThrownBy(() -> orderService.checkOut("testuser"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cart is Empty");
    }

    @Test
    void testCheckOut_throwsBadRequest_whenInsufficientStock() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartService.getCartItems("testuser")).thenReturn(Map.of(1L, 10));
        Book lowStock = new Book(1L, "Test Book", "Test Author", 10.0, 2, "desc", "img.png");
        when(bookService.findEntityById(1L)).thenReturn(lowStock);

        assertThatThrownBy(() -> orderService.checkOut("testuser"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void testGetUserOrders_returnsOrders() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Order order = new Order();
        order.setId(5L);
        order.setUser(user);
        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setQuantity(1);
        item.setUnitPrice(book.getPrice());
        item.setOrder(order);
        order.getItems().add(item);
        order.setTotalAmount(10.0);

        when(orderRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(order));

        var orders = orderService.getUserOrders("testuser");
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getId()).isEqualTo(5L);
        assertThat(orders.get(0).getItems()).hasSize(1);
    }

    @Test
    void testGetUserOrders_throwsNotFound_whenUserMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getUserOrders("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

}
