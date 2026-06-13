package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartItemRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.dto.CartResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserWithItems(user)
                .orElseGet(()-> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                subtotal
        );
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email
                ));
    }

    public CartResponse addItem(String email, CartItemRequest request) {
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProduct_id())
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProduct_id()
                ));
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .map(existing -> {
                    existing.setQuantity(
                            existing.getQuantity() + request.getQuantity()
                    );
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(request.getQuantity())
                            .build();
                    return cartItemRepository.save(newItem);
                });
        return getCart(email);
    }

    public CartResponse getCart(String email) {
        User user = getUserByEmail(email);
        Cart cart = getOrCreateCart(user);
        List<CartItemResponse> items = cart.getItems() == null
                ? List.of()
                : cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), items, total);
    }
    public CartResponse updateItem(String email, Long cartItem_id, CartItemRequest request) {
        getUserByEmail(email);
        CartItem item = cartItemRepository.findById(cartItem_id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItem_id
                ));
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return getCart(email);
    }
    public CartResponse removeItem(String email, Long cartItem_id) {
        getUserByEmail(email);
        CartItem item = cartItemRepository.findById(cartItem_id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItem_id
                ));
        cartItemRepository.delete(item);
        return getCart(email);
    }
 }
