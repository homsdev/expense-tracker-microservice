package com.homsdev.transactions_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data needed to create a new category")
public class CreateCategoryRequest {
    @Schema(description = "Name of the category. Must not be blank.", example = "Groceries", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
