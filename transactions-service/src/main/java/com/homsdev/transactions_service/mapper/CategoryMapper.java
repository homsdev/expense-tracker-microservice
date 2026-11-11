package com.homsdev.transactions_service.mapper;

import com.homsdev.transactions_service.dto.CategoryResponse;
import com.homsdev.transactions_service.dto.CreateCategoryRequest;
import com.homsdev.transactions_service.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    CategoryResponse toCategoryResponse(Category category);
    Category toCategory(CreateCategoryRequest request);
}
