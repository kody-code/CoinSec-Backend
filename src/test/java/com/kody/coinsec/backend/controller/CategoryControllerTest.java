package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.exception.GlobalExceptionHandler;
import com.kody.coinsec.backend.dto.CategoryRequest;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(categoryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/categories - 返回分类列表")
    void list_Success() throws Exception {
        CategoryEntity category = CategoryEntity.builder()
                .categoryId(1L)
                .name("餐饮")
                .type("expense")
                .icon("restaurant")
                .sort(1)
                .build();
        when(categoryService.getCategories(null)).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("餐饮"));
    }

    @Test
    @DisplayName("POST /api/categories - 创建成功返回ID")
    void create_Success() throws Exception {
        CategoryEntity saved = CategoryEntity.builder().categoryId(1L).build();
        when(categoryService.createCategory(any())).thenReturn(saved);

        CategoryRequest request = new CategoryRequest();
        request.setName("交通");
        request.setType("expense");
        request.setIcon("directions_car");
        request.setSort(2);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.categoryId").value(1));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} - 删除成功")
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
