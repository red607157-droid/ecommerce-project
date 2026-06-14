package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemResponse {
    private Long product_id;
    private String product_name;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
