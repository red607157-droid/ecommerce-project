package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryRequest;
import com.example.ecommerce.dto.CategoryResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_savesAndReturnCategory() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");
        Category savedCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);
        CategoryResponse response = categoryService.createCategory(request);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    @Test
    void getAllCategories_returnsAllCategories() {
        Category cat1 = Category.builder().id(1L).name("Electronics").build();
        Category cat2 = Category.builder().id(2L).name("Clothing").build();

        when(categoryRepository.findAll())
                .thenReturn(List.of(cat1, cat2));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
        assertThat(result.get(1).getName()).isEqualTo("Clothing");
    }
}
