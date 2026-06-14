package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderItemResponse;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.EmptyCartException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse checkout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));
        Cart cart = cartRepository.findByUserWithItems(user)
                .orElseThrow(() -> new EmptyCartException("Cart is empty"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot checkout an empty cart");
        }
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Not enough stock for product: " + product.getName()
                        + " (available: " + product.getStock()
                        + ", requested: " + item.getQuantity() + ")"
                );
            }
        }
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = Order.builder()
                .user(user)
                .totalPrice(total)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();
        order = orderRepository.save(order);
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();
            order.getOrderItems().add(orderItem);
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        order = orderRepository.save(order);
        cartItemRepository.deleteAllById(
                cart.getItems().stream()
                        .map(CartItem::getId)
                        .collect(Collectors.toList())
        );
        return toOrderResponse(order);
    }
    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(oi -> new OrderItemResponse(
                        oi.getProduct().getId(),
                        oi.getProduct().getName(),
                        oi.getQuantity(),
                        oi.getPrice(),
                        oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()))
                ))
                .collect(Collectors.toList());
        return new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }
    public List<OrderResponse> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));
        return orderRepository.findByUserWithItems(user)
                .stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }
    public OrderResponse getOrderById(String email, Long order_id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));
        Order order = orderRepository.findByIdWithItems(order_id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + order_id
                ));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException(
                    "Order not found with id: " + order_id
            );
        }
        return toOrderResponse(order);
    }
}

