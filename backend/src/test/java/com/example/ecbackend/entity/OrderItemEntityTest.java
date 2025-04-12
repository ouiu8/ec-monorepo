package com.example.ecbackend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderItem: 注文明細エンティティのテスト")
class OrderItemEntityTest {

    @Test
    @DisplayName("注文明細エンティティのプロパティ設定と取得")
    void shouldSetAndGetOrderItemProperties() {
        // Given: テスト用のデータ
        Long id = 1L;
        Long orderId = 10L;
        Long productId = 100L;
        Integer quantity = 2;
        Integer unitPrice = 1000;
        Integer subtotal = 2000;
        LocalDateTime now = LocalDateTime.now();
        
        // When: 注文明細エンティティにプロパティを設定
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setSubtotal(subtotal);
        orderItem.setCreatedAt(now);
        orderItem.setUpdatedAt(now);
        
        // Then: プロパティが正しく取得できる
        assertThat(orderItem.getId()).isEqualTo(id);
        assertThat(orderItem.getOrderId()).isEqualTo(orderId);
        assertThat(orderItem.getProductId()).isEqualTo(productId);
        assertThat(orderItem.getQuantity()).isEqualTo(quantity);
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderItem.getSubtotal()).isEqualTo(subtotal);
        assertThat(orderItem.getCreatedAt()).isEqualTo(now);
        assertThat(orderItem.getUpdatedAt()).isEqualTo(now);
    }
    
    @Test
    @DisplayName("注文明細エンティティのnullプロパティ")
    void shouldHandleNullProperties() {
        // Given: プロパティを設定していない新しい注文明細エンティティ
        OrderItem orderItem = new OrderItem();
        
        // When & Then: すべてのプロパティがnull
        assertThat(orderItem.getId()).isNull();
        assertThat(orderItem.getOrderId()).isNull();
        assertThat(orderItem.getProductId()).isNull();
        assertThat(orderItem.getQuantity()).isNull();
        assertThat(orderItem.getUnitPrice()).isNull();
        assertThat(orderItem.getSubtotal()).isNull();
        assertThat(orderItem.getCreatedAt()).isNull();
        assertThat(orderItem.getUpdatedAt()).isNull();
    }
    
    @Test
    @DisplayName("注文明細エンティティのプロパティ更新")
    void shouldUpdateOrderItemProperties() {
        // Given: 初期値を持つ注文明細エンティティ
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(10L);
        orderItem.setProductId(100L);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(1000);
        orderItem.setSubtotal(2000);
        LocalDateTime oldTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        orderItem.setCreatedAt(oldTime);
        orderItem.setUpdatedAt(oldTime);
        
        // When: プロパティを更新
        Integer newQuantity = 3;
        Integer newSubtotal = 3000;
        LocalDateTime newTime = LocalDateTime.now();
        
        orderItem.setQuantity(newQuantity);
        orderItem.setSubtotal(newSubtotal);
        orderItem.setUpdatedAt(newTime);
        
        // Then: 更新したプロパティが新しい値になり、更新していないプロパティは元の値のまま
        assertThat(orderItem.getId()).isEqualTo(1L);
        assertThat(orderItem.getOrderId()).isEqualTo(10L);
        assertThat(orderItem.getProductId()).isEqualTo(100L);
        assertThat(orderItem.getQuantity()).isEqualTo(newQuantity);
        assertThat(orderItem.getUnitPrice()).isEqualTo(1000);
        assertThat(orderItem.getSubtotal()).isEqualTo(newSubtotal);
        assertThat(orderItem.getCreatedAt()).isEqualTo(oldTime);
        assertThat(orderItem.getUpdatedAt()).isEqualTo(newTime);
    }
    
    @Test
    @DisplayName("小計計算のテスト")
    void shouldCalculateSubtotal() {
        // Given: 数量と単価を持つ注文明細エンティティ
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(3);
        orderItem.setUnitPrice(1500);
        
        // When: 小計を計算 (ビジネスロジックの一部として)
        Integer calculatedSubtotal = orderItem.getQuantity() * orderItem.getUnitPrice();
        orderItem.setSubtotal(calculatedSubtotal);
        
        // Then: 小計が正しく計算され設定されている
        assertThat(orderItem.getSubtotal()).isEqualTo(4500);
    }
} 