package com.homsdev.transactions_service.mapper;

import com.homsdev.transactions_service.dto.CategoryResponse;
import com.homsdev.transactions_service.dto.CreateCategoryRequest;
import com.homsdev.transactions_service.model.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private final CategoryMapper categoryMapper = new CategoryMapperImpl();

    @Test
    void toCategoryResponseMapsIdAndName() {
        Category category = new Category();
        category.setId("cat-1");
        category.setName("Groceries");

        assertThat(categoryMapper.toCategoryResponse(category))
                .isEqualTo(new CategoryResponse("cat-1", "Groceries"));
    }

    @Test
    void toCategoryMapsName() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Groceries");

        Category category = categoryMapper.toCategory(request);

        assertThat(category.getName()).isEqualTo("Groceries");
    }

    @Test
    void toCategoryResponseReturnsNullForNullCategory() {
        assertThat(categoryMapper.toCategoryResponse(null)).isNull();
    }

    @Test
    void toCategoryReturnsNullForNullRequest() {
        assertThat(categoryMapper.toCategory(null)).isNull();
    }
}
