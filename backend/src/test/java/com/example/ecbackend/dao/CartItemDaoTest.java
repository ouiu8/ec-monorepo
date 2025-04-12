package com.example.ecbackend.dao;

import com.example.ecbackend.entity.CartItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

/**
 * CartItemDao のインテグレーションテスト
 * 
 * テスト設計原則:
 * 1. 各メソッドの正常系と異常系を網羅
 * 2. エッジケースと境界値の徹底的検証
 * 3. BDDスタイル（Given-When-Then）でテスト構造を明確に
 * 4. データベースの状態を適切に検証
 * 5. テストの独立性を保証
 * 
 * TODO: 将来的には Testcontainers を導入し、実際の PostgreSQL データベースで
 * より完全なテストを実装することを検討する。現在は H2 互換の制約のため
 * シーケンス関連の問題を回避するアプローチを採用している。
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CartItemDao: カートアイテム永続化層のテスト")
public class CartItemDaoTest {

    @Autowired
    private CartItemDao cartItemDao;
    
    // 固定の現在時刻を使用（テスト時の再現性を高めるため）
    private final LocalDateTime fixedTime = LocalDateTime.now();
    
    @Nested
    @DisplayName("findById(): IDによるカートアイテム検索")
    class FindByIdTests {
        
        @Test
        @DisplayName("既存のIDで正常にカートアイテムを取得できる")
        void shouldFindCartItemById() {
            // data.sqlで挿入されたID=1のアイテムを検索
            Optional<CartItem> found = cartItemDao.findById(1L);
            
            // 結果の検証
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(1L);
            assertThat(found.get().getCartId()).isEqualTo(1L);
            assertThat(found.get().getProductId()).isEqualTo(1L);
            assertThat(found.get().getQuantity()).isEqualTo(2);
        }
        
        @Test
        @DisplayName("存在しないIDでは空のOptionalが返される")
        void shouldReturnEmptyOptionalForNonExistentId() {
            // 存在しないIDで検索
            Optional<CartItem> found = cartItemDao.findById(999L);
            
            // 結果の検証
            assertThat(found).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("findByCartId(): カートIDによるカートアイテム検索")
    class FindByCartIdTests {
        
        @Test
        @DisplayName("カートIDに紐づくカートアイテムを全て取得できる")
        void shouldFindAllCartItemsByCartId() {
            // カートID 1に関連するアイテムを取得（data.sqlで初期データとして入っているはず）
            List<CartItem> cartItems = cartItemDao.findByCartId(1L);
            
            // 初期データが2件あるはず
            assertThat(cartItems).isNotNull();
            assertThat(cartItems).hasSize(2);
            
            // データの中身を検証
            assertThat(cartItems)
                .extracting("productId")
                .containsExactlyInAnyOrder(1L, 3L);
        }
        
        @Test
        @DisplayName("存在しないカートIDでは空リストが返される")
        void shouldReturnEmptyListWhenCartIdDoesNotExist() {
            // 存在しないカートIDで検索
            List<CartItem> results = cartItemDao.findByCartId(999L);
            
            // 空リストが返されることを検証
            assertThat(results).isNotNull().isEmpty();
        }
    }
    
    @Nested
    @DisplayName("findByCartIdAndProductId(): カートIDと商品ID検索")
    class FindByCartIdAndProductIdTests {
        
        @Test
        @DisplayName("カートIDと商品IDの組み合わせで特定のカートアイテムを取得できる")
        void shouldFindCartItemByCartIdAndProductId() {
            // 存在する組み合わせで検索
            Optional<CartItem> result = cartItemDao.findByCartIdAndProductId(1L, 1L);
            
            // 結果を検証
            assertThat(result).isPresent();
            assertThat(result.get().getCartId()).isEqualTo(1L);
            assertThat(result.get().getProductId()).isEqualTo(1L);
            assertThat(result.get().getQuantity()).isEqualTo(2);
        }
        
        @Test
        @DisplayName("存在しない組み合わせでは空のOptionalが返される")
        void shouldReturnEmptyOptionalWhenCombinationDoesNotExist() {
            // 存在しない商品IDで検索
            Optional<CartItem> result = cartItemDao.findByCartIdAndProductId(1L, 999L);
            
            // 空のOptionalが返されることを検証
            assertThat(result).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("insert(): カートアイテム登録処理")
    class InsertTests {
        
        @Test
        @DisplayName("カートアイテムの一意性制約が機能していることを確認する")
        void shouldEnforceUniqueConstraint() {
            // Given: カートID=1, 商品ID=1の組み合わせは既に存在する
            CartItem duplicateItem = new CartItem();
            duplicateItem.setCartId(1L);
            duplicateItem.setProductId(1L);
            duplicateItem.setQuantity(10);
            duplicateItem.setCreatedAt(fixedTime);
            duplicateItem.setUpdatedAt(fixedTime);
            
            // When & Then: 登録で例外がスローされることを検証
            assertThatThrownBy(() -> cartItemDao.insert(duplicateItem))
                .as("カートIDと商品IDの組み合わせは一意でなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
        
        @ParameterizedTest
        @ValueSource(ints = {-1, 0})
        @DisplayName("商品数量が0以下の場合は不正な値となる")
        void shouldRejectNegativeOrZeroQuantity(int invalidQuantity) {
            // Given: 不正な数量を持つカートアイテム
            CartItem invalidItem = new CartItem();
            invalidItem.setCartId(1L);
            invalidItem.setProductId(2L);
            invalidItem.setQuantity(invalidQuantity);
            invalidItem.setCreatedAt(fixedTime);
            invalidItem.setUpdatedAt(fixedTime);
            
            // When & Then: 登録で例外がスローされることを検証
            assertThatThrownBy(() -> cartItemDao.insert(invalidItem))
                .as("商品数量は正の整数でなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
        
        @Test
        @DisplayName("外部キー制約のテスト - 存在しないカートIDは失敗する")
        void shouldRespectForeignKeyConstraint() {
            // Given: 存在しないカートIDを持つカートアイテム
            CartItem invalidItem = new CartItem();
            invalidItem.setCartId(999L); // 存在しないカートID
            invalidItem.setProductId(1L);
            invalidItem.setQuantity(1);
            invalidItem.setCreatedAt(fixedTime);
            invalidItem.setUpdatedAt(fixedTime);
            
            // When & Then: 登録で例外がスローされることを検証
            assertThatThrownBy(() -> cartItemDao.insert(invalidItem))
                .as("外部キー制約によりカートIDは存在するものでなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
    }
    
    @Nested
    @DisplayName("update(): カートアイテム更新処理")
    class UpdateTests {
        
        @Test
        @DisplayName("既存のカートアイテムの数量を更新できる")
        void shouldUpdateExistingCartItemQuantity() {
            // Given: 既存のカートアイテムを取得 (ID=1)
            Optional<CartItem> existingOptional = cartItemDao.findById(1L);
            assertThat(existingOptional).isPresent();
            
            CartItem existing = existingOptional.get();
            int oldQuantity = existing.getQuantity();
            
            // 数量を更新
            existing.setQuantity(oldQuantity + 3);
            existing.setUpdatedAt(fixedTime);
            
            // When: 更新を実行
            int result = cartItemDao.update(existing);
            
            // Then: 更新結果の検証
            assertThat(result).isEqualTo(1);
            
            // 再取得して確認
            Optional<CartItem> updated = cartItemDao.findById(1L);
            assertThat(updated).isPresent();
            assertThat(updated.get().getQuantity()).isEqualTo(oldQuantity + 3);
        }
        
        @Test
        @DisplayName("数量を0以下に更新すると制約違反となる")
        void shouldRejectUpdatingToZeroOrNegativeQuantity() {
            // Given: 既存のカートアイテムを取得
            Optional<CartItem> existingOptional = cartItemDao.findById(1L);
            assertThat(existingOptional).isPresent();
            
            CartItem existing = existingOptional.get();
            
            // 無効な数量に更新
            existing.setQuantity(0);
            
            // When & Then: 更新で例外がスローされることを検証
            assertThatThrownBy(() -> cartItemDao.update(existing))
                .as("商品数量は正の整数でなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
        
        @Test
        @DisplayName("存在しないIDの更新は影響行数0で失敗する")
        void shouldReturnZeroForNonExistentId() {
            // Given: 存在しないIDのカートアイテム
            CartItem nonExistent = new CartItem();
            nonExistent.setId(999L);
            nonExistent.setCartId(1L);
            nonExistent.setProductId(1L);
            nonExistent.setQuantity(5);
            nonExistent.setUpdatedAt(fixedTime);
            
            // When: 更新を実行
            int result = cartItemDao.update(nonExistent);
            
            // Then: 影響行数が0であることを検証
            assertThat(result).isEqualTo(0);
        }
    }
    
    @Nested
    @DisplayName("delete(): カートアイテム削除処理")
    class DeleteTests {
        
        @Test
        @DisplayName("既存のカートアイテムを削除できる")
        void shouldDeleteExistingCartItem() {
            // Given: 既存のカートアイテムを取得
            Optional<CartItem> existingOptional = cartItemDao.findById(1L);
            assertThat(existingOptional).isPresent();
            
            CartItem existing = existingOptional.get();
            
            // When: 削除を実行
            int result = cartItemDao.delete(existing);
            
            // Then: 削除結果の検証
            assertThat(result).isEqualTo(1);
            
            // 再取得して確認
            Optional<CartItem> deleted = cartItemDao.findById(1L);
            assertThat(deleted).isEmpty();
        }
        
        @Test
        @DisplayName("存在しないIDの削除は影響行数0で失敗する")
        void shouldReturnZeroForNonExistentId() {
            // Given: 存在しないIDのカートアイテム
            CartItem nonExistent = new CartItem();
            nonExistent.setId(999L);
            
            // When: 削除を実行
            int result = cartItemDao.delete(nonExistent);
            
            // Then: 影響行数が0であることを検証
            assertThat(result).isEqualTo(0);
        }
    }
} 