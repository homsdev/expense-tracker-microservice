package com.homsdev.transactions_service.controller;

import com.homsdev.transactions_service.dto.CategoryResponse;
import com.homsdev.transactions_service.exception.ResourceNotFoundException;
import com.homsdev.transactions_service.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    // @EnableJpaAuditing on the application class needs this bean; no database is used here
    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    void getAllCategoriesReturnsOk() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(new CategoryResponse("1", "Groceries")));

        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Groceries"));
    }

    @Test
    void getCategoryByIdReturnsOk() throws Exception {
        when(categoryService.getCategoryById("1")).thenReturn(new CategoryResponse("1", "Groceries"));

        mockMvc.perform(get("/api/v1/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getCategoryByIdReturnsNotFoundProblemWhenMissing() throws Exception {
        when(categoryService.getCategoryById("missing"))
                .thenThrow(new ResourceNotFoundException("Category", "missing"));

        mockMvc.perform(get("/api/v1/category/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createCategoryReturnsCreated() throws Exception {
        when(categoryService.createCategory(any())).thenReturn(new CategoryResponse("1", "Groceries"));

        mockMvc.perform(post("/api/v1/category")
                        .contentType("application/json")
                        .content("{\"name\":\"Groceries\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    void deleteCategoryReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/category/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory("1");
    }
}
