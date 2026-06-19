package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {
    @org.springframework.beans.factory.annotation.Autowired
    private ProductRepository productRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private CategoryRepository categoryRepository;

    @Test
    void searchProducts_findsMatchingProductByName() {
        Category category = categoryRepository.save(
                Category.builder().name("Electronics").build()
        );
        productRepository.save(Product.builder()
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(new BigDecimal("999.99"))
                .stock(50)
                .category(category)
                .build());

        productRepository.save(Product.builder()
                .name("Samsung TV")
                .description("Smart television")
                .price(new BigDecimal("799.99"))
                .stock(20)
                .category(category)
                .build());
        Page<Product> result = productRepository.searchProducts(
            "iphone", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("iPhone 15");
    }

    @Test
    void searchProducts_withEmptyKeyword_returnsAllProducts() {
        Category category = categoryRepository.save(
                Category.builder().name("Books").build()
        );
        productRepository.save(Product.builder()
                .name("Java Book")
                .price(new BigDecimal("29.99"))
                .stock(10)
                .category(category)
                .build());
        Page<Product> result = productRepository.searchProducts(
                "", PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).hasSize(1);
    }
}
