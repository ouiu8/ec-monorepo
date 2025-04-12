package com.example.ecbackend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cart: カートモデルのテスト")
class CartTest {

    @Test
    @DisplayName("カートオブジェクトのプロパティ設定と取得")
    void shouldSetAndGetCartProperties() {
        // Given: テスト用のデータ
        Long id = 1L;
        String sessionId = "test-session-id";
        LocalDateTime now = LocalDateTime.now();
        
        // When: カートオブジェクトにプロパティを設定
        Cart cart = new Cart();
        cart.setId(id);
        cart.setSessionId(sessionId);
        cart.setCreatedAt(now);
        cart.setUpdatedAt(now);
        
        // Then: プロパティが正しく取得できる
        assertThat(cart.getId()).isEqualTo(id);
        assertThat(cart.getSessionId()).isEqualTo(sessionId);
        assertThat(cart.getCreatedAt()).isEqualTo(now);
        assertThat(cart.getUpdatedAt()).isEqualTo(now);
    }
    
    @Test
    @DisplayName("カートオブジェクトのnullプロパティ")
    void shouldHandleNullProperties() {
        // Given: プロパティを設定していない新しいカートオブジェクト
        Cart cart = new Cart();
        
        // When & Then: すべてのプロパティがnull
        assertThat(cart.getId()).isNull();
        assertThat(cart.getSessionId()).isNull();
        assertThat(cart.getCreatedAt()).isNull();
        assertThat(cart.getUpdatedAt()).isNull();
    }
    
    @Test
    @DisplayName("カートオブジェクトのプロパティ更新")
    void shouldUpdateCartProperties() {
        // Given: 初期値を持つカートオブジェクト
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setSessionId("old-session-id");
        LocalDateTime oldTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        cart.setCreatedAt(oldTime);
        cart.setUpdatedAt(oldTime);
        
        // When: プロパティを更新
        Long newId = 2L;
        String newSessionId = "new-session-id";
        LocalDateTime newTime = LocalDateTime.now();
        
        cart.setId(newId);
        cart.setSessionId(newSessionId);
        // 作成日時は変更しない
        cart.setUpdatedAt(newTime);
        
        // Then: 更新したプロパティが新しい値になり、更新していないプロパティは元の値のまま
        assertThat(cart.getId()).isEqualTo(newId);
        assertThat(cart.getSessionId()).isEqualTo(newSessionId);
        assertThat(cart.getCreatedAt()).isEqualTo(oldTime);
        assertThat(cart.getUpdatedAt()).isEqualTo(newTime);
    }
} 