package com.bookstore.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto book) {
        log.debug("Create book request: {}", book);
        BookDto created = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto book) {
        log.debug("Update book id: {} payload: {}", id, book);
        BookDto updated = bookService.updateBook(id, book);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.debug("Delete book id: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}