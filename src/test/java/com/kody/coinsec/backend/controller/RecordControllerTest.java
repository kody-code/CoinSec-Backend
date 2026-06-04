package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.exception.GlobalExceptionHandler;
import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.service.RecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RecordControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private RecordService recordService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new RecordController(recordService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/records - 创建记录成功")
    void create_Success() throws Exception {
        RecordResponse response = RecordResponse.builder().recordId(1L).build();
        when(recordService.createRecord(any())).thenReturn(response);

        RecordRequest request = new RecordRequest();
        request.setCategoryId(1L);
        request.setAccountId(1L);
        request.setType("expense");
        request.setAmount(new BigDecimal("25.00"));
        request.setRemark("午餐");
        request.setRecordTime("2026-06-04 12:30:00");

        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recordId").value(1));
    }

    @Test
    @DisplayName("GET /api/records - 获取记录列表")
    void list_Success() throws Exception {
        mockMvc.perform(get("/api/records"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/records/statistics - 获取统计")
    void statistics_Success() throws Exception {
        mockMvc.perform(get("/api/records/statistics")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
