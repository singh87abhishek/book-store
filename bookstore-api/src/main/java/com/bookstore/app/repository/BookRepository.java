package com.bookstore.app.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.app.entity.Book;

/**
 * BookRepository
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    public List<Book> findByStockGreaterThan(int stock);
    

}