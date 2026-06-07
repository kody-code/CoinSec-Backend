package com.kody.coinsec.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IconControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new IconController()).build();
    }

    @Test
    @DisplayName("GET /api/icons/alipay.svg - 存在的图标返回 SVG")
    void getIcon_Exists_ReturnSvg() throws Exception {
        mockMvc.perform(get("/api/icons/alipay.svg"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf("image/svg+xml")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<svg")));
    }

    @Test
    @DisplayName("GET /api/icons/wechat.svg - 存在的图标返回 SVG")
    void getIcon_WechatExists_ReturnSvg() throws Exception {
        mockMvc.perform(get("/api/icons/wechat.svg"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf("image/svg+xml")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<svg")));
    }

    @Test
    @DisplayName("GET /api/icons/notfound.svg - 不存在的图标返回 404")
    void getIcon_NotFound_Return404() throws Exception {
        mockMvc.perform(get("/api/icons/notfound.svg"))
                .andExpect(status().isNotFound());
    }
}
