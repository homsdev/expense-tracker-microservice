package com.homsdev.transactions_service.service.impl;

import com.homsdev.transactions_service.dto.CategoryResponse;
import com.homsdev.transactions_service.dto.CreateCategoryRequest;
import com.homsdev.transactions_service.exception.ResourceNotFoundException;
import com.homsdev.transactions_service.mapper.CategoryMapper;
import com.homsdev.transactions_service.model.Category;
import com.homsdev.transactions_service.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getAllCategoriesReturnsOnlyActiveCategories() {
        Category active = new Category();
        active.setActive(true);
        Category inactive = new Category();
        inactive.setActive(false);
        CategoryResponse response = new CategoryResponse("1", "Groceries");

        when(categoryRepository.findAll()).thenReturn(List.of(active, inactive));
        when(categoryMapper.toCategoryResponse(active)).thenReturn(response);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).containsExactly(response);
        verify(categoryMapper, never()).toCategoryResponse(inactive);
    }

    @Test
    void getCategoryByIdReturnsCategoryWhenFound() {
        Category category = new Category();
        CategoryResponse response = new CategoryResponse("1", "Groceries");

        when(categoryRepository.findById("1")).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

        assertThat(categoryService.getCategoryById("1")).isEqualTo(response);
    }

    @Test
    void getCategoryByIdThrowsWhenNotFound() {
        when(categoryRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createCategorySetsActiveAndSaves() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        Category mapped = new Category();
        Category saved = new Category();
        CategoryResponse response = new CategoryResponse("1", "Groceries");

        when(categoryMapper.toCategory(request)).thenReturn(mapped);
        when(categoryRepository.save(mapped)).thenReturn(saved);
        when(categoryMapper.toCategoryResponse(saved)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(request);

        assertThat(mapped.isActive()).isTrue();
        assertThat(result).isEqualTo(response);
    }

    @Test
    void deleteCategorySoftDeletesByMarkingInactive() {
        Category category = new Category();
        category.setActive(true);

        when(categoryRepository.findById("1")).thenReturn(Optional.of(category));

        categoryService.deleteCategory("1");

        assertThat(category.isActive()).isFalse();
        verify(categoryRepository).save(category);
    }

    @Test
    void deleteCategoryThrowsWhenNotFound() {
        when(categoryRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(categoryRepository, never()).save(any());
    }
}
