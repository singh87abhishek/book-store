package com.bookstore.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.exception.ResourceNotFoundException;
import com.bookstore.app.mapper.BookMapper;
import com.bookstore.app.repository.BookRepository;
import com.bookstore.app.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<BookDto> getAllAvailableBooks() {
        log.debug("Fetching all available books with stock > 0");
        List<Book> books = bookRepository.findByStockGreaterThan(0);

        List<BookDto> bookList = new ArrayList<>();
        for (Book book : books) {
            bookList.add(BookMapper.toDto(book));
        }

        log.debug("Found {} available books", bookList.size());
        return bookList;
    }

    @Override
    public BookDto toDto(Book book) {
        return BookMapper.toDto(book);
    }

    @Override
    public BookDto getBookById(Long id) {
        log.debug("Fetching book by Id: {}", id);
        return bookRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> {
                    log.error("Book not found with Id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
    }

    @Override
    public Book findEntityById(Long id) {
        log.debug("Fetching book entity by id: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book Entity no found with Id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
    }

    @Override
    public void save(Book book) {
        log.debug("Saving book: {}, updated stock: {}", book.getTitle(), book.getStock());
        bookRepository.save(book);
    }
}
