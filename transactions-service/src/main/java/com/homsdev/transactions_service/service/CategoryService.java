package com.homsdev.transactions_service.service;


import com.homsdev.transactions_service.dto.CategoryResponse;
import com.homsdev.transactions_service.dto.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(String id);

    CategoryResponse createCategory(CreateCategoryRequest request);

    void deleteCategory(String id);
}
