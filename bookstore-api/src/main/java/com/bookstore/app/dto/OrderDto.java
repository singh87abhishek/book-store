package com.bookstore.app.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data 
public class OrderDto {
    private Long id;
    private String status;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    @Data 
    public static class OrderItemDto {
        private Long bookId;
        private String title;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;

        public static class Builder {
            private final OrderItemDto dto = new OrderItemDto();

            public Builder bookId(Long id) { dto.setBookId(id); return this; }
            public Builder title(String t) { dto.setTitle(t); return this; }
            public Builder quantity(Integer q) { dto.setQuantity(q); return this; }
            public Builder unitPrice(Double p) { dto.setUnitPrice(p); return this; }
            public Builder subtotal(Double s) { dto.setSubtotal(s); return this; }
            public OrderItemDto build() { return dto; }
        }
    }

    public static class Builder {
        private final OrderDto dto = new OrderDto();

        public Builder id(Long id) { dto.setId(id); return this; }
        public Builder status(String s) { dto.setStatus(s); return this; }
        public Builder totalAmount(Double a) { dto.setTotalAmount(a); return this; }
        public Builder createdAt(LocalDateTime t) { dto.setCreatedAt(t); return this; }
        public Builder items(List<OrderItemDto> items) { dto.setItems(items); return this; }
        public Builder addItem(OrderItemDto item) { if (dto.getItems() == null) dto.setItems(new ArrayList<>()); dto.getItems().add(item); return this; }
        public OrderDto build() { return dto; }
    }
}
