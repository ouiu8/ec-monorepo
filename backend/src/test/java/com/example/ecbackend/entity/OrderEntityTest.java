package com.example.ecbackend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Order: 注文エンティティのテスト")
class OrderEntityTest {

    @Test
    @DisplayName("注文エンティティのプロパティ設定と取得")
    void shouldSetAndGetOrderProperties() {
        // Given: テスト用のデータ
        Long id = 1L;
        Long userId = 2L;
        LocalDateTime orderDate = LocalDateTime.now();
        String status = "PENDING";
        Integer totalAmount = 5000;
        String shippingAddress = "東京都渋谷区1-1-1";
        String paymentMethod = "CREDIT_CARD";
        LocalDateTime cancelledAt = null;
        LocalDateTime now = LocalDateTime.now();
        List<OrderItem> items = new ArrayList<>();
        
        // When: 注文エンティティにプロパティを設定
        Order order = new Order();
        order.setId(id);
        order.setUserId(userId);
        order.setOrderDate(orderDate);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setCancelledAt(cancelledAt);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setItems(items);
        
        // Then: プロパティが正しく取得できる
        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getOrderDate()).isEqualTo(orderDate);
        assertThat(order.getStatus()).isEqualTo(status);
        assertThat(order.getTotalAmount()).isEqualTo(totalAmount);
        assertThat(order.getShippingAddress()).isEqualTo(shippingAddress);
        assertThat(order.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.getCancelledAt()).isEqualTo(cancelledAt);
        assertThat(order.getCreatedAt()).isEqualTo(now);
        assertThat(order.getUpdatedAt()).isEqualTo(now);
        assertThat(order.getItems()).isEqualTo(items);
    }
    
    @Test
    @DisplayName("注文エンティティのnullプロパティ")
    void shouldHandleNullProperties() {
        // Given: プロパティを設定していない新しい注文エンティティ
        Order order = new Order();
        
        // When & Then: すべてのプロパティがnull
        assertThat(order.getId()).isNull();
        assertThat(order.getUserId()).isNull();
        assertThat(order.getOrderDate()).isNull();
        assertThat(order.getStatus()).isNull();
        assertThat(order.getTotalAmount()).isNull();
        assertThat(order.getShippingAddress()).isNull();
        assertThat(order.getPaymentMethod()).isNull();
        assertThat(order.getCancelledAt()).isNull();
        assertThat(order.getCreatedAt()).isNull();
        assertThat(order.getUpdatedAt()).isNull();
        assertThat(order.getItems()).isNull();
    }
    
    @Test
    @DisplayName("注文エンティティのプロパティ更新")
    void shouldUpdateOrderProperties() {
        // Given: 初期値を持つ注文エンティティ
        Order order = new Order();
        order.setId(1L);
        order.setUserId(2L);
        order.setStatus("PENDING");
        order.setTotalAmount(5000);
        LocalDateTime oldTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        order.setOrderDate(oldTime);
        order.setCreatedAt(oldTime);
        order.setUpdatedAt(oldTime);
        
        // When: プロパティを更新
        String newStatus = "SHIPPED";
        LocalDateTime newTime = LocalDateTime.now();
        LocalDateTime cancelledAt = LocalDateTime.now();
        
        order.setStatus(newStatus);
        order.setCancelledAt(cancelledAt);
        order.setUpdatedAt(newTime);
        
        // Then: 更新したプロパティが新しい値になり、更新していないプロパティは元の値のまま
        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getUserId()).isEqualTo(2L);
        assertThat(order.getStatus()).isEqualTo(newStatus);
        assertThat(order.getTotalAmount()).isEqualTo(5000);
        assertThat(order.getOrderDate()).isEqualTo(oldTime);
        assertThat(order.getCancelledAt()).isEqualTo(cancelledAt);
        assertThat(order.getCreatedAt()).isEqualTo(oldTime);
        assertThat(order.getUpdatedAt()).isEqualTo(newTime);
    }
    
    @Test
    @DisplayName("注文に注文明細を追加")
    void shouldAddOrderItems() {
        // Given: 注文エンティティと注文明細アイテム
        Order order = new Order();
        order.setId(1L);
        
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setOrderId(1L);
        item1.setProductId(10L);
        item1.setQuantity(2);
        item1.setUnitPrice(1000);
        item1.setSubtotal(2000);
        
        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setOrderId(1L);
        item2.setProductId(20L);
        item2.setQuantity(1);
        item2.setUnitPrice(3000);
        item2.setSubtotal(3000);
        
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        
        // When: 注文エンティティに注文明細一覧を設定
        order.setItems(items);
        
        // Then: 注文明細が正しく取得できる
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getItems().get(0).getProductId()).isEqualTo(10L);
        assertThat(order.getItems().get(1).getProductId()).isEqualTo(20L);
    }
} 