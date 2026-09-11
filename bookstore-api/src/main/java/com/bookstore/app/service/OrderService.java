package com.bookstore.app.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.app.dto.OrderDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.entity.Order;
import com.bookstore.app.entity.OrderItem;
import com.bookstore.app.entity.User;
import com.bookstore.app.repository.OrderRepository;
import com.bookstore.app.repository.UserRepository;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service 
@RequiredArgsConstructor  
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final BookService bookService;

    public List<OrderDto> getUserOrders(String userName) {
        log.debug("Fetching orders for user : {}", userName);
        User user = getUser(userName);

        List<OrderDto> orders = orderRepository.findByUserOrderByCreatedAtDesc(user)
                        .stream()
                        .map(this::toDto).collect(Collectors.toList());
        
        log.debug("Found {} orders for User: {}", orders.size(), userName);
        return orders;
    }

    @Transactional
    public OrderDto checkOut(String userName) {
        log.debug("Checkout started for User: {}", userName);
        User user = getUser(userName);

        Map<Long, Integer> cartItems = cartService.getCartItems(userName);
        if(cartItems.isEmpty()) {
            log.error("Checkout failed - Cart is empty for User: {}", userName);
            throw new BadRequestException("Cart is Empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("CONFIRMED");

        double total = 0;

        for(Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Book book = bookService.findEntityById(entry.getKey());
            int qty = entry.getValue();

            //Check if the stock is sufficient
            if(book.getStock() < qty) {
                log.error("Checkout failed - insuficient stock for book: {} requested: {} available: {}", book.getTitle(), qty, book.getStock());
                throw new BadRequestException("Insufficient stock for: "+ book.getTitle());
            }

            log.debug("Adding to order - book: {} quantity: {} unitPrice: {}", book.getTitle(), qty, book.getPrice());
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(qty);
            item.setUnitPrice(book.getPrice());
            
            order.getItems().add(item);

            //Update the book quantity (decrease)
            book.setStock(book.getStock() - qty);
            bookService.save(book);

            //Calculate the total amount
            total += book.getPrice() * qty;
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        
        //Clear the cart of the user
        cartService.clearCart(userName);

        log.debug("Order placed successfuly for user: {} orderId: {} total:{}", userName, saved.getId(), total);
        return toDto(saved);
    }


    private OrderDto toDto(Order order) {
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

    private User getUser(String userName) {
        return userRepository.findByUsername(userName)
                    .orElseThrow(() -> {
                        log.error("Fetch orders failed - user not found: {}", userName);
                        return new ResourceNotFoundException("User not found");
                    });
    }
    
}
