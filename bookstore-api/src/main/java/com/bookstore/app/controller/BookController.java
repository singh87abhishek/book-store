package com.bookstore.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController 
@RequestMapping("/bookstore-api/books")
@RequiredArgsConstructor 
public class BookController {
    private final BookService bookService;

    @GetMapping 
    public ResponseEntity<List<BookDto>> getAllBooks() {
        log.debug("Getting request to fetch all available books");
        List<BookDto> bookList = bookService.getAllAvailableBooks();
        log.debug("Fetched books: {}", bookList);
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        log.debug("Getting request to fetch book with ID: {}", id);
        BookDto book = bookService.getBookById(id);
        log.debug("Fetched book: {}", book);
        return ResponseEntity.ok(book);
    }

}