package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * ProductDaoのテストクラス
 * テスト設計原則:
 * 1. 各テストは独立して実行可能
 * 2. テスト目的が明確で理解しやすい
 * 3. 境界条件と代表的なケースをカバー
 * 4. データ変更を伴うテストは元の状態に復元する責任を持つ
 * 5. BDDスタイル（Given-When-Then）に従う
 */
@SpringBootTest
@ActiveProfiles("test") // テスト用プロファイルを有効化
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
    
    // テスト対象商品の元の状態を保存するための変数
    private Product originalProduct1;
    private Product originalProduct2;
    
    @BeforeEach
    void setUp() {
        // テスト実行前に既存商品のオリジナル状態を保存
        originalProduct1 = cloneProduct(productDao.selectById(EXISTING_PRODUCT_ID_1));
        originalProduct2 = cloneProduct(productDao.selectById(EXISTING_PRODUCT_ID_2));
    }
    
    /**
     * 商品データのディープコピーを作成するユーティリティメソッド
     */
    private Product cloneProduct(Product original) {
        if (original == null) return null;
        
        Product clone = new Product();
        clone.setId(original.getId());
        clone.setName(original.getName());
        clone.setDescription(original.getDescription());
        clone.setPrice(original.getPrice());
        clone.setStock(original.getStock());
        clone.setImageUrl(original.getImageUrl());
        clone.setCreatedAt(original.getCreatedAt());
        clone.setUpdatedAt(original.getUpdatedAt());
        return clone;
    }
    
    /**
     * 商品の状態を元に戻すユーティリティメソッド
     */
    private void restoreProduct(Product original) {
        if (original != null) {
            productDao.update(original);
        }
    }

    @Nested
    @DisplayName("selectAll: 全商品取得のテスト")
    class SelectAllTests {
        
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
    }
    
    @Nested
    @DisplayName("selectById: 商品ID検索のテスト")
    class SelectByIdTests {
        
        @ParameterizedTest
        @ValueSource(longs = {1, 2, 3})
        @DisplayName("存在するIDで検索すると正しい商品が取得できる")
        void shouldReturnProductWhenIdExists(long id) {
            // When: 指定IDで商品を検索
            Product product = productDao.selectById(id);
            
            // Then: 商品が存在し、IDが一致すること
            assertThat(product)
                .isNotNull()
                .satisfies(p -> {
                    assertThat(p.getId()).isEqualTo(id);
                    assertThat(p.getName()).isEqualTo("テスト商品" + id);
                    assertThat(p.getPrice()).isEqualTo((int)id * 1000);
                });
        }
        
        @ParameterizedTest
        @ValueSource(longs = {0, -1, 999, 10000})
        @DisplayName("存在しないIDで検索するとnullが返る")
        void shouldReturnNullWhenIdDoesNotExist(long id) {
            // When: 存在しないIDで商品を検索
            Product product = productDao.selectById(id);
            
            // Then: 結果がnullであること
            assertThat(product).isNull();
        }
    }
    
    @Nested
    @DisplayName("update: 商品更新のテスト")
    class UpdateTests {
        
        @Test
        @DisplayName("商品の情報が正しく更新される")
        void shouldUpdateProductCorrectly() {
            try {
                // Given: 更新対象の商品を取得
                Product productToUpdate = productDao.selectById(EXISTING_PRODUCT_ID_1);
                assertThat(productToUpdate).isNotNull();
                
                // 更新データを準備
                String updatedName = "更新後の商品名";
                int updatedPrice = 9999;
                String originalDescription = productToUpdate.getDescription();
                
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
                // テスト後に元の状態に復元
                restoreProduct(originalProduct1);
            }
        }
        
        @Test
        @DisplayName("複数のフィールドを同時に更新できる")
        void shouldUpdateMultipleFieldsAtOnce() {
            try {
                // Given: 更新対象の商品を取得
                Product productToUpdate = productDao.selectById(EXISTING_PRODUCT_ID_2);
                assertThat(productToUpdate).isNotNull();
                
                // 更新データを準備（複数フィールド）
                String updatedName = "複合更新商品";
                int updatedPrice = 7777;
                String updatedDescription = "複数フィールド更新のテスト";
                int updatedStock = 50;
                
                // When: 複数のフィールドを更新
                productToUpdate.setName(updatedName);
                productToUpdate.setPrice(updatedPrice);
                productToUpdate.setDescription(updatedDescription);
                productToUpdate.setStock(updatedStock);
                productToUpdate.setUpdatedAt(LocalDateTime.now());
                int updateResult = productDao.update(productToUpdate);
                
                // Then: 更新が成功していること
                assertThat(updateResult).isEqualTo(1);
                
                // 更新した商品を再取得
                Product updatedProduct = productDao.selectById(EXISTING_PRODUCT_ID_2);
                
                // すべてのフィールドが正しく更新されていることを検証
                assertThat(updatedProduct)
                    .isNotNull()
                    .satisfies(p -> {
                        assertThat(p.getName()).isEqualTo(updatedName);
                        assertThat(p.getPrice()).isEqualTo(updatedPrice);
                        assertThat(p.getDescription()).isEqualTo(updatedDescription);
                        assertThat(p.getStock()).isEqualTo(updatedStock);
                    });
            } finally {
                // テスト後に元の状態に復元
                restoreProduct(originalProduct2);
            }
        }
        
        @Test
        @DisplayName("存在しないIDの商品を更新すると0が返る")
        void shouldReturnZeroWhenUpdatingNonExistentProduct() {
            // Given: 存在しないIDを持つ商品エンティティを作成
            Product nonExistentProduct = new Product();
            nonExistentProduct.setId(NON_EXISTING_PRODUCT_ID);
            nonExistentProduct.setName("存在しない商品");
            nonExistentProduct.setPrice(1);
            nonExistentProduct.setUpdatedAt(LocalDateTime.now());
            
            // When: 存在しない商品の更新を試みる
            int updateResult = productDao.update(nonExistentProduct);
            
            // Then: 更新の影響を受けた行数が0であることを確認
            assertThat(updateResult).isZero();
        }
    }

    /**
     * 注意: insert操作のテストはH2とPostgreSQLの互換性問題により実装を省略しています。
     * 実際の本番環境では、insert操作のテストも重要です。
     */
} 