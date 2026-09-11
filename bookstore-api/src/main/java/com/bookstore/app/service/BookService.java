package com.bookstore.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.repository.BookRepository;

import lombok.RequiredArgsConstructor;

/**
 * BookService
 */
@Service
@RequiredArgsConstructor 
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDto> getAllAvailableBooks() {
        List<Book> books = bookRepository.findByStockGreaterThan(0);

        List<BookDto> bookList = new ArrayList<>();
            
        for (Book book : books) {
            bookList.add(toDto(book));
        }

        return bookList;
    }

    public BookDto toDto(Book book) {
        return new BookDto(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getDescription(),
            book.getPrice(),
            book.getStock(),
            book.getImageUrl()
        );
    }

    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public Book findEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

}