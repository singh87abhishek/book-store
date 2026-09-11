package com.bookstore.app.service;

import java.util.List;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;

public interface BookService {
    List<BookDto> getAllAvailableBooks();
    BookDto toDto(Book book);
    BookDto getBookById(Long id);
    Book findEntityById(Long id);
    void save(Book book);
}