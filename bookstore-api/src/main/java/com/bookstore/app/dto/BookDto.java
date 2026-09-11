package com.bookstore.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;

}
