package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryRequest;
import com.example.ecommerce.dto.CategoryResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
     public List<CategoryResponse> getAllCategories() {
         return categoryRepository.findAll()
                 .stream()
                 .map(c -> new CategoryResponse(c.getId(), c.getName()))
                 .collect(Collectors.toList());
     }

     public CategoryResponse createCategory(CategoryRequest request){
         Category category = Category.builder()
                 .name(request.getName())
                 .build();
         Category saved = categoryRepository.save(category);
         return new CategoryResponse(saved.getId(), saved.getName());
     }
}
