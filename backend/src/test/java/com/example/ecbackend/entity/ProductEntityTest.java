package com.example.ecbackend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product: 商品エンティティのテスト")
class ProductEntityTest {

    @Test
    @DisplayName("商品エンティティのプロパティ設定と取得")
    void shouldSetAndGetProductProperties() {
        // Given: テスト用のデータ
        Long id = 1L;
        String name = "テスト商品";
        String description = "これはテスト商品の説明です";
        Integer price = 1000;
        Integer stock = 50;
        String imageUrl = "https://example.com/test.jpg";
        LocalDateTime now = LocalDateTime.now();
        
        // When: 商品エンティティにプロパティを設定
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        
        // Then: プロパティが正しく取得できる
        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getImageUrl()).isEqualTo(imageUrl);
        assertThat(product.getCreatedAt()).isEqualTo(now);
        assertThat(product.getUpdatedAt()).isEqualTo(now);
    }
    
    @Test
    @DisplayName("商品エンティティのnullプロパティ")
    void shouldHandleNullProperties() {
        // Given: プロパティを設定していない新しい商品エンティティ
        Product product = new Product();
        
        // When & Then: すべてのプロパティがnull
        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isNull();
        assertThat(product.getDescription()).isNull();
        assertThat(product.getPrice()).isNull();
        assertThat(product.getStock()).isNull();
        assertThat(product.getImageUrl()).isNull();
        assertThat(product.getCreatedAt()).isNull();
        assertThat(product.getUpdatedAt()).isNull();
    }
    
    @Test
    @DisplayName("商品エンティティのプロパティ更新")
    void shouldUpdateProductProperties() {
        // Given: 初期値を持つ商品エンティティ
        Product product = new Product();
        product.setId(1L);
        product.setName("旧商品名");
        product.setDescription("旧商品説明");
        product.setPrice(1000);
        product.setStock(50);
        product.setImageUrl("https://example.com/old.jpg");
        LocalDateTime oldTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        product.setCreatedAt(oldTime);
        product.setUpdatedAt(oldTime);
        
        // When: プロパティを更新
        String newName = "新商品名";
        String newDescription = "新商品説明";
        Integer newPrice = 1200;
        String newImageUrl = "https://example.com/new.jpg";
        LocalDateTime newTime = LocalDateTime.now();
        
        product.setName(newName);
        product.setDescription(newDescription);
        product.setPrice(newPrice);
        product.setImageUrl(newImageUrl);
        product.setUpdatedAt(newTime);
        
        // Then: 更新したプロパティが新しい値になり、更新していないプロパティは元の値のまま
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo(newName);
        assertThat(product.getDescription()).isEqualTo(newDescription);
        assertThat(product.getPrice()).isEqualTo(newPrice);
        assertThat(product.getStock()).isEqualTo(50);
        assertThat(product.getImageUrl()).isEqualTo(newImageUrl);
        assertThat(product.getCreatedAt()).isEqualTo(oldTime);
        assertThat(product.getUpdatedAt()).isEqualTo(newTime);
    }
    
    @Test
    @DisplayName("在庫の減少処理のテスト")
    void shouldDecreaseStock() {
        // Given: 在庫を持つ商品エンティティ
        Product product = new Product();
        product.setStock(10);
        
        // When: 在庫を減少
        Integer quantity = 3;
        Integer newStock = product.getStock() - quantity;
        product.setStock(newStock);
        
        // Then: 在庫が正しく減少している
        assertThat(product.getStock()).isEqualTo(7);
    }
    
    @Test
    @DisplayName("在庫の増加処理のテスト")
    void shouldIncreaseStock() {
        // Given: 在庫を持つ商品エンティティ
        Product product = new Product();
        product.setStock(5);
        
        // When: 在庫を増加
        Integer quantity = 8;
        Integer newStock = product.getStock() + quantity;
        product.setStock(newStock);
        
        // Then: 在庫が正しく増加している
        assertThat(product.getStock()).isEqualTo(13);
    }
} 