package com.bookstore.app.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.exception.ResourceNotFoundException;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.repository.BookRepository;
import com.bookstore.app.service.impl.BookServiceImpl;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith (MockitoExtension.class)
public class BookServiceTest {

    @Mock 
    private BookRepository bookRepository;

    @InjectMocks 
    private BookServiceImpl bookService;

    private Book book;

    @BeforeEach 
    void setUp() {
        book = new Book(1L, "Test Book", "Test Author", 10.0, 5, "Test Description", "test_image_url.png");
    }

    @Test
    void testCreateBook_success() {
        BookDto dto = new BookDto(null, "New Book", "New Author", "Desc", 25.0, 3, "img.png");
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(2L);
            return b;
        });

        BookDto created = bookService.createBook(dto);
        assertThat(created.getId()).isEqualTo(2L);
        assertThat(created.getTitle()).isEqualTo("New Book");
    }

    @Test
    void testCreateBook_exception_throwsBadRequest() {
        BookDto dto = new BookDto(null, "New Book", "New Author", "Desc", 25.0, 3, "img.png");
        when(bookRepository.save(any(Book.class))).thenThrow(new DataAccessResourceFailureException("DB down"));

        assertThatThrownBy(() -> bookService.createBook(dto))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Unable to create book");
    }

    @Test
    void testUpdateBook_success() {
        BookDto dto = new BookDto(null, "Updated", "Author", "Desc", 30.0, 4, "img2.png");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookDto updated = bookService.updateBook(1L, dto);
        assertThat(updated.getTitle()).isEqualTo("Updated");
        assertThat(updated.getPrice()).isEqualTo(30.0);
    }

    @Test
    void testUpdateBook_notFound_throwsResourceNotFound() {
        BookDto dto = new BookDto(null, "Updated", "Author", "Desc", 30.0, 4, "img2.png");
        when(bookRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(5L, dto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testUpdateBook_exception_throwsBadRequest() {
        BookDto dto = new BookDto(null, "Updated", "Author", "Desc", 30.0, 4, "img2.png");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenThrow(new DataAccessResourceFailureException("DB error"));

        assertThatThrownBy(() -> bookService.updateBook(1L, dto))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Unable to update book");
    }

    @Test
    void testDeleteBook_success() {
        // no exception means success
        bookService.deleteBook(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void testDeleteBook_notFound_throwsResourceNotFound() {
        doThrow(new EmptyResultDataAccessException(1)).when(bookRepository).deleteById(99L);
        assertThatThrownBy(() -> bookService.deleteBook(99L))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Unable to delete book id : 99");
    }

    @Test
    void testDeleteBook_exception_throwsBadRequest() {
        doThrow(new DataAccessResourceFailureException("DB down")).when(bookRepository).deleteById(2L);
        assertThatThrownBy(() -> bookService.deleteBook(2L))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Unable to delete book");
    }

    @Test 
    void testGetAllAvailableBooks_returnsOnlyBookWithStock() {
       when(bookRepository.findByStockGreaterThan(0)).thenReturn(List.of(book));
       List<BookDto> bookList = bookService.getAllAvailableBooks();

        assertThat(bookList).hasSize(1);
        assertThat(bookList.get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    void testGetBookById_returnsBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(book));
        BookDto book = bookService.getBookById(1L);
        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getAuthor()).isEqualTo("Test Author");
    }

    @Test
    void testGetBookById_throwsBadRequestException_whenNotFound() {
        when(bookRepository.findById(1201L)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> bookService.getBookById(1201L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book not found with id: 1201");
    }

    @Test 
    void testFindEntityById_throwResourceNotFoundException_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findEntityById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 99");
    }
}
