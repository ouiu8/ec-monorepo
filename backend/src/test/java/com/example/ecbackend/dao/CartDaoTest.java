package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CartDao のシンプルなテスト
 * 既存のデータを用いた検索機能のみをテスト
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CartDaoTest {

    @Autowired
    private CartDao cartDao;

    @Test
    @DisplayName("findBySessionId: 既存のカートを取得できる")
    void shouldFindExistingCart() {
        // data.sqlの初期データにあるセッションIDで検索
        Optional<Cart> cart = cartDao.findBySessionId("test-session-1");
        
        // 検証
        assertThat(cart).isPresent();
        assertThat(cart.get().getSessionId()).isEqualTo("test-session-1");
    }
    
    @Test
    @DisplayName("findBySessionId: 存在しないセッションIDでは空のOptionalが返される")
    void shouldReturnEmptyForNonExistentSessionId() {
        // 存在しないセッションIDで検索
        Optional<Cart> cart = cartDao.findBySessionId("non-existent-session");
        
        // 検証
        assertThat(cart).isEmpty();
    }
    
    @Test
    @DisplayName("findById: 既存のIDでカートを取得できる")
    void shouldFindCartById() {
        // data.sqlの初期データにあるIDで検索
        Optional<Cart> cart = cartDao.findById(1L);
        
        // 検証
        assertThat(cart).isPresent();
        assertThat(cart.get().getId()).isEqualTo(1L);
        assertThat(cart.get().getSessionId()).isEqualTo("test-session-1");
    }
    
    @Test
    @DisplayName("findById: 存在しないIDでは空のOptionalが返される")
    void shouldReturnEmptyForNonExistentId() {
        // 存在しないIDで検索
        Optional<Cart> cart = cartDao.findById(999L);
        
        // 検証
        assertThat(cart).isEmpty();
    }
} 