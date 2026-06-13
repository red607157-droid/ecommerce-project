package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResponse {
    private Long cart_id;
    private List<CartItemResponse> items;
    private BigDecimal total;
}
