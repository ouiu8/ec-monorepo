package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * ProductDaoのテストクラス
 * テスト設計原則:
 * 1. 各テストは独立して実行可能
 * 2. テスト目的が明確で理解しやすい
 * 3. 境界条件と代表的なケースをカバー
 * 4. データ変更はトランザクション内で行い、自動的にロールバック
 * 5. BDDスタイル（Given-When-Then）に従う
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductDao: データアクセス機能のテスト")
public class ProductDaoTest {

    @Autowired
    private ProductDao productDao;
    
    // テスト用データの定数
    private static final long EXISTING_PRODUCT_ID_1 = 1L;
    private static final long EXISTING_PRODUCT_ID_2 = 2L;
    private static final long EXISTING_PRODUCT_ID_3 = 3L;
    private static final long NON_EXISTING_PRODUCT_ID = 999L;

    @Test
    @DisplayName("全商品が正しく取得できる")
    void shouldReturnAllProducts() {
        // When: 全商品を取得
        List<Product> products = productDao.selectAll();
        
        // Then: 期待する商品リストが取得できていること
        assertThat(products)
            .isNotNull()
            .hasSize(3)
            .extracting(Product::getId, Product::getName, Product::getPrice)
            .containsExactly(
                tuple(1L, "テスト商品1", 1000),
                tuple(2L, "テスト商品2", 2000),
                tuple(3L, "テスト商品3", 3000)
            );
    }
    
    @Test
    @DisplayName("存在するIDで検索すると正しい商品が取得できる")
    void shouldReturnProductWhenIdExists() {
        // When: 指定IDで商品を検索
        Product product = productDao.selectById(EXISTING_PRODUCT_ID_1);
        
        // Then: 商品が存在し、IDが一致すること
        assertThat(product)
            .isNotNull()
            .satisfies(p -> {
                assertThat(p.getId()).isEqualTo(EXISTING_PRODUCT_ID_1);
                assertThat(p.getName()).isEqualTo("テスト商品1");
                assertThat(p.getPrice()).isEqualTo(1000);
            });
    }
    
    @Test
    @DisplayName("存在しないIDで検索するとnullが返る")
    void shouldReturnNullWhenIdDoesNotExist() {
        // When: 存在しないIDで商品を検索
        Product product = productDao.selectById(NON_EXISTING_PRODUCT_ID);
        
        // Then: 結果がnullであること
        assertThat(product).isNull();
    }
    
    @Test
    @DisplayName("商品の情報が正しく更新される")
    void shouldUpdateProductCorrectly() {
        // Given: 更新対象の商品を取得
        Product productToUpdate = productDao.selectById(EXISTING_PRODUCT_ID_1);
        assertThat(productToUpdate).isNotNull();
        
        // 更新前の情報を保存
        String originalName = productToUpdate.getName();
        Integer originalPrice = productToUpdate.getPrice();
        String originalDescription = productToUpdate.getDescription();
        
        try {
            // 更新データを準備
            String updatedName = "更新後の商品名";
            int updatedPrice = 9999;
            
            // When: 商品情報を更新
            productToUpdate.setName(updatedName);
            productToUpdate.setPrice(updatedPrice);
            productToUpdate.setUpdatedAt(LocalDateTime.now());
            int updateResult = productDao.update(productToUpdate);
            
            // Then: 更新が成功していること
            assertThat(updateResult).isEqualTo(1);
            
            // 更新した商品を再取得
            Product updatedProduct = productDao.selectById(EXISTING_PRODUCT_ID_1);
            
            // 更新内容が反映されていることを検証
            assertThat(updatedProduct)
                .isNotNull()
                .satisfies(p -> {
                    assertThat(p.getName()).isEqualTo(updatedName);
                    assertThat(p.getPrice()).isEqualTo(updatedPrice);
                    // 更新していないフィールドは変更されていないこと
                    assertThat(p.getDescription()).isEqualTo(originalDescription);
                });
        } finally {
            // テスト後に元の状態に復元（必要ない場合はトランザクション自動ロールバックされる）
            productToUpdate.setName(originalName);
            productToUpdate.setPrice(originalPrice);
            productDao.update(productToUpdate);
        }
    }
} 