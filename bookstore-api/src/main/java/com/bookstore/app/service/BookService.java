package com.bookstore.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.exception.ResourceNotFoundException;
import com.bookstore.app.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
@RequiredArgsConstructor 
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDto> getAllAvailableBooks() {
        log.debug("Fetching all available books with stock > 0");
        List<Book> books = bookRepository.findByStockGreaterThan(0);

        List<BookDto> bookList = new ArrayList<>();
            
        for (Book book : books) {
            bookList.add(toDto(book));
        }

        log.debug("Found {} available books", bookList.size());
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
        log.debug("Fetching book by Id: {}", id);
        return bookRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> {
                    log.error("Book not found with Id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
    }

    public Book findEntityById(Long id) {
        log.debug("Fetching book entity by id: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book Entity no found with Id: {}", id); 
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
    }

}