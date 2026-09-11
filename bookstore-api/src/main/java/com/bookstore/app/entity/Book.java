package com.bookstore.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book
 */
@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Book {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column 
    private String title;

    @Column 
    private String author;

    @Column
    private Double price;

    @Column
    private Integer stock;

    private String description;
    private String imageUrl;

}
