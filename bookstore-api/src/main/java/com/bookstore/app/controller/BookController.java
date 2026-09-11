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

@RestController 
@RequestMapping("/bookstore-api/books")
@RequiredArgsConstructor 
public class BookController {
    private final BookService bookService;

    @GetMapping 
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> bookList = bookService.getAllAvailableBooks();
        System.out.println("Book List: " + bookList);
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        System.out.println("Book detail: " + book);
        return ResponseEntity.ok(book);
    }

}
