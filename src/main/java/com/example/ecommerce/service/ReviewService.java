package com.example.ecommerce.service;

import com.example.ecommerce.dto.ReviewRequest;
import com.example.ecommerce.dto.ReviewResponse;
import com.example.ecommerce.entity.ProductReview;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewResponse addReview(String email, ReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));
        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id :" + request.getProductId()
                ));
        if (reviewRepository.existsByProductIdAndUserId(
                request.getProductId(), user.getId()
        )) {
            throw new RuntimeException("You have already reviewed this product");
        }
        ProductReview review = ProductReview.builder()
                .productId(request.getProductId())
                .userId(user.getId())
                .userName(user.getName())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        ProductReview saved = reviewRepository.save(review);
        return toResponse(saved);
    }
    public List<ReviewResponse> getProductReviews(Long product_id) {
        return reviewRepository
                .findByProductIdOrderByCreatedAtDesc(product_id)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    private ReviewResponse toResponse(ProductReview review) {
        return new ReviewResponse(
                review.getId(),
                review.getUserName(),
                review.getProductId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
