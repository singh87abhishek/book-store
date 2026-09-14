package com.bookstore.app.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.bookstore.app.constants.OrderStatus;
import com.bookstore.app.entity.Book;
import com.bookstore.app.entity.Order;
import com.bookstore.app.entity.OrderItem;
import com.bookstore.app.entity.User;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.service.BookService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFactory {

    private final BookService bookService;

    public Order createOrder(User user, Map<Long, Integer> cartItems) {
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is Empty");
        }

        Order order = new Order();
        double total = 0;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Book book = bookService.findEntityById(entry.getKey());
            int qty = entry.getValue();

            if (book.getStock() < qty) {
                throw new BadRequestException("Insufficient stock for: " + book.getTitle());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(qty);
            item.setUnitPrice(book.getPrice());

            order.getItems().add(item);

            // Update stock
            book.setStock(book.getStock() - qty);
            bookService.save(book);

            total += book.getPrice() * qty;
        }

        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED.name());

        return order;
    }
}
