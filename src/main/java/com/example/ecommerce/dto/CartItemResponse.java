package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItemResponse {
    private Long cartItem_id;
    private Long product_id;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

}
