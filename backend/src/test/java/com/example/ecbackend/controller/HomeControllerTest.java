package com.example.ecbackend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
@DisplayName("HomeController: ホームコントローラーのテスト")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("ルートパスにアクセスすると正常なレスポンスが返される")
    void shouldReturnWelcomeMessage() throws Exception {
        // When: ルートパスにGETリクエストを送信
        mockMvc.perform(get("/")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            // Then: 正常なレスポンスとウェルカムメッセージが返される
            .andExpect(status().isOk())
            .andExpect(content().string("Welcome to EC Application!"));
    }
} 