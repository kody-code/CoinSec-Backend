package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.exception.GlobalExceptionHandler;
import com.kody.coinsec.backend.dto.TagRequest;
import com.kody.coinsec.backend.dto.TagResponse;
import com.kody.coinsec.backend.service.TagService;
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
class TagControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private TagService tagService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new TagController(tagService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/tags - 返回标签列表")
    void list_Success() throws Exception {
        TagResponse response = TagResponse.builder()
                .tagId(1L)
                .name("工作餐")
                .color("#FF6B6B")
                .build();

        when(tagService.getTags()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("工作餐"));
    }

    @Test
    @DisplayName("POST /api/tags - 创建成功返回ID")
    void create_Success() throws Exception {
        TagResponse saved = TagResponse.builder().tagId(1L).build();
        when(tagService.createTag(any())).thenReturn(saved);

        TagRequest request = new TagRequest();
        request.setName("工作餐");
        request.setColor("#FF6B6B");

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.tagId").value(1));
    }

    @Test
    @DisplayName("DELETE /api/tags/{id} - 删除成功")
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/api/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
