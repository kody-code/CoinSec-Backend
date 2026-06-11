package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.exception.GlobalExceptionHandler;
import com.kody.coinsec.backend.dto.BudgetRequest;
import com.kody.coinsec.backend.dto.BudgetResponse;
import com.kody.coinsec.backend.service.BudgetService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new BudgetController(budgetService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/budgets - 返回预算列表")
    void list_Success() throws Exception {
        BudgetResponse response = BudgetResponse.builder()
                .budgetId(1L)
                .categoryId(1L)
                .categoryName("餐饮")
                .budgetAmount(new BigDecimal("3000.00"))
                .periodType("MONTHLY")
                .periodYear(2026)
                .periodMonth(6)
                .build();

        when(budgetService.getBudgets()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/budgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].budgetId").value(1))
                .andExpect(jsonPath("$.data[0].categoryName").value("餐饮"));
    }

    @Test
    @DisplayName("POST /api/budgets - 创建成功返回ID")
    void create_Success() throws Exception {
        BudgetResponse saved = BudgetResponse.builder().budgetId(1L).build();
        when(budgetService.createBudget(any())).thenReturn(saved);

        BudgetRequest request = new BudgetRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("3000.00"));
        request.setPeriodType("MONTHLY");
        request.setPeriodYear(2026);
        request.setPeriodMonth(6);

        mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.budgetId").value(1));
    }

    @Test
    @DisplayName("DELETE /api/budgets/{id} - 删除成功")
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/budgets/overview - 返回预算进度")
    void overview_Success() throws Exception {
        mockMvc.perform(get("/api/budgets/overview")
                        .param("periodType", "MONTHLY")
                        .param("periodYear", "2026")
                        .param("periodMonth", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
