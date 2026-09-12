package com.bookstore.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.exception.ResourceNotFoundException;
import com.bookstore.app.mapper.BookMapper;
import com.bookstore.app.exception.BadRequestException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    @Override
    public void deleteBook(Long id) {
        log.debug("Deleting book by id: {}", id);
        try {
            bookRepository.deleteById(id);
        } catch (Exception ex) {
            log.error("Exception while deleting book id: {} - {}", id, ex.getMessage());
            throw new BadRequestException("Unable to delete book id : " + id);
        }
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        log.debug("Creating book from book: {}", bookDto.getTitle());
        try {
            Book book = BookMapper.toEntity(bookDto, null);
            Book saved = bookRepository.save(book);
            return BookMapper.toDto(saved);
        } catch (Exception ex) {
            log.error("Exception while creating book {} : {}", bookDto.getTitle(), ex.getMessage());
            throw new BadRequestException("Unable to create book: " + bookDto.getTitle());
        }
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        log.debug("Updating book id: {} with book: {}", id, bookDto.getTitle());
        // ensure the book exists
        findEntityById(id);

        try {
            Book book = BookMapper.toEntity(bookDto, id);
            Book saved = bookRepository.save(book);
            return BookMapper.toDto(saved);
        } catch (Exception ex) {
            log.error("Exception while updating book id: {} - {}", id, ex.getMessage());
            throw new BadRequestException("Unable to update book id : " + id);
        }
    }

    private BookDto toDto(Book book) {
        return BookMapper.toDto(book);
    }
}