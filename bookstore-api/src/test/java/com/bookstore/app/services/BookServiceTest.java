package com.bookstore.app.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;
import com.bookstore.app.repository.BookRepository;
import com.bookstore.app.service.BookService;

@ExtendWith (MockitoExtension.class)
public class BookServiceTest {

    @Mock 
    private BookRepository bookRepository;

    @InjectMocks 
    private BookService bookService;

    private Book book;

    @BeforeEach 
    void setUp() {
        book = new Book(1L, "Test Book", "Test Author", 10.0, 5, "Test Description", "test_image_url.png");
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
    void testGetBookById_throwsException_whenNotFound() {
        when(bookRepository.findById(1201L)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> bookService.getBookById(1201L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found with id: 1201");
    }
}
