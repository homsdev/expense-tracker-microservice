package com.homsdev.transactions_service.service.impl;

import com.homsdev.transactions_service.dto.CategoryResponse;
import com.homsdev.transactions_service.dto.CreateCategoryRequest;
import com.homsdev.transactions_service.exception.ResourceNotFoundException;
import com.homsdev.transactions_service.mapper.CategoryMapper;
import com.homsdev.transactions_service.model.Category;
import com.homsdev.transactions_service.repository.CategoryRepository;
import com.homsdev.transactions_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .filter(Category::isActive)
                .map(categoryMapper::toCategoryResponse).toList();
    }

    @Override
    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", id)
        );

        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category: {}", request);
        Category category = categoryMapper.toCategory(request);

        log.info("Mapping category to: {}", category);
        category.setActive(true);

        Category createdCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(createdCategory);
    }

    @Override
    public void deleteCategory(String id) {

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", id)
        );

        category.setActive(false);

        categoryRepository.save(category);
    }
}
