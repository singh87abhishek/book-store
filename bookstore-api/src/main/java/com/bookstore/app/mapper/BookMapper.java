package com.bookstore.app.mapper;

import com.bookstore.app.dto.BookDto;
import com.bookstore.app.entity.Book;

public final class BookMapper {

    private BookMapper() {}

    public static BookDto toDto(Book book) {
        if (book == null) return null;
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

    public static Book toEntity(BookDto dto, Long id ) {
        if (dto == null) return null;
        return new Book(
            id,
            dto.getTitle(),
            dto.getAuthor(),
            dto.getPrice(),
            dto.getStock(),
            dto.getDescription(),
            dto.getImageUrl()
        );
    }
}
