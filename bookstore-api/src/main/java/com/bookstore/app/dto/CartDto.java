package com.bookstore.app.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * CartDto
 */
@Data 
public class CartDto {
    private List<CartItemDto> items;
    private Double totalPrice;

    @Data
    public static class CartItemDto {
        private Long bookId;
        private String title;
        private String author;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;

        public static class Builder {
            private final CartItemDto dto = new CartItemDto();

            public Builder bookId(Long id) { dto.setBookId(id); return this; }
            public Builder title(String t) { dto.setTitle(t); return this; }
            public Builder author(String a) { dto.setAuthor(a); return this; }
            public Builder quantity(Integer q) { dto.setQuantity(q); return this; }
            public Builder unitPrice(Double p) { dto.setUnitPrice(p); return this; }
            public Builder subtotal(Double s) { dto.setSubtotal(s); return this; }
            public CartItemDto build() { return dto; }
        }
    }

    public static class Builder {
        private final CartDto dto = new CartDto();

        public Builder items(List<CartItemDto> items) { dto.setItems(items); return this; }
        public Builder addItem(CartItemDto item) {
            if (dto.getItems() == null) dto.setItems(new ArrayList<>());
            dto.getItems().add(item);
            return this;
        }
        public Builder totalPrice(Double price) { dto.setTotalPrice(price); return this; }
        public CartDto build() { return dto; }
    }

}
