package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Cart;
import com.example.ecbackend.entity.CartItem;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

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
 */
@SpringBootTest
@ActiveProfiles("test") // テスト用プロファイルを有効化
@Transactional // 各テスト後にロールバックして独立性を保証
@DisplayName("CartItemDao: カートアイテム永続化層のテスト")
public class CartItemDaoTest {

    @Autowired
    private CartItemDao cartItemDao;
    
    @Autowired
    private CartDao cartDao;
    
    // テスト用データ
    private Cart testCart;
    private CartItem testCartItem;
    private LocalDateTime fixedDateTime;
    
    @BeforeEach
    void setUp() {
        // 固定の日時を設定
        fixedDateTime = LocalDateTime.now();
        
        // テスト用カートを作成
        testCart = new Cart();
        testCart.setSessionId(UUID.randomUUID().toString());
        testCart.setCreatedAt(fixedDateTime);
        testCart.setUpdatedAt(fixedDateTime);
        cartDao.insert(testCart);
        
        // テスト用カートアイテムを作成
        testCartItem = new CartItem();
        testCartItem.setCartId(testCart.getId());
        testCartItem.setProductId(1L);
        testCartItem.setQuantity(2);
        testCartItem.setCreatedAt(fixedDateTime);
        testCartItem.setUpdatedAt(fixedDateTime);
    }
    
    @Nested
    @DisplayName("insert(): カートアイテム登録処理")
    class InsertTests {
        
        @Test
        @DisplayName("有効なカートアイテム情報でデータが正常に登録できる")
        void shouldInsertValidCartItem() {
            // Given: 有効なカートアイテムエンティティを準備済み (setUp)
            
            // When: カートアイテムを登録
            int result = cartItemDao.insert(testCartItem);
            
            // Then: 検証
            assertThat(result).as("挿入成功時は1が返される").isEqualTo(1);
            assertThat(testCartItem.getId()).as("IDが自動生成される").isNotNull().isPositive();
            
            // データベースから取得して検証
            Optional<CartItem> savedItem = cartItemDao.findById(testCartItem.getId());
            assertThat(savedItem).isPresent();
            assertThat(savedItem.get())
                .extracting("cartId", "productId", "quantity")
                .containsExactly(testCart.getId(), 1L, 2);
        }
        
        @Test
        @DisplayName("存在しないカートIDでは外部キー制約違反となる")
        void shouldThrowExceptionWhenCartIdDoesNotExist() {
            // Given: 存在しないカートIDを持つカートアイテム
            CartItem invalidCartItem = new CartItem();
            invalidCartItem.setCartId(999L); // 存在しないカートID
            invalidCartItem.setProductId(1L);
            invalidCartItem.setQuantity(1);
            invalidCartItem.setCreatedAt(fixedDateTime);
            invalidCartItem.setUpdatedAt(fixedDateTime);
            
            // When & Then: 登録で例外がスローされることを検証
            assertThatThrownBy(() -> cartItemDao.insert(invalidCartItem))
                .as("外部キー制約によりカートIDは存在するものでなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
        
        @Test
        @DisplayName("同じカートと商品の組み合わせでは一意性制約違反となる（同一商品は一つのエントリのみ許可）")
        void shouldThrowExceptionWhenCartIdAndProductIdAreDuplicated() {
            // Given: カートアイテムを一つ登録
            cartItemDao.insert(testCartItem);
            
            // 同じカートIDと商品IDを持つ別のカートアイテム
            CartItem duplicateItem = new CartItem();
            duplicateItem.setCartId(testCart.getId());
            duplicateItem.setProductId(1L); // 同じ商品ID
            duplicateItem.setQuantity(3); // 別の数量
            duplicateItem.setCreatedAt(fixedDateTime);
            duplicateItem.setUpdatedAt(fixedDateTime);
            
            // When & Then: 登録で例外がスローされることを検証
            assertThatThrownBy(() -> cartItemDao.insert(duplicateItem))
                .as("カートIDと商品IDの組み合わせは一意でなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
        
        @ParameterizedTest
        @ValueSource(ints = {-1, 0})
        @DisplayName("商品数量が0以下の場合は不正な値となる")
        void shouldThrowExceptionWhenQuantityIsNegativeOrZero(int invalidQuantity) {
            // Given: 不正な数量を持つカートアイテム
            testCartItem.setQuantity(invalidQuantity);
            
            // When & Then: 登録で例外がスローされることを検証（数量は正の整数であるべき）
            assertThatThrownBy(() -> cartItemDao.insert(testCartItem))
                .as("商品数量は正の整数でなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
    }
    
    @Nested
    @DisplayName("findByCartId(): カートIDによるカートアイテム検索")
    class FindByCartIdTests {
        
        @Test
        @DisplayName("カートIDに紐づくカートアイテムを全て取得できる")
        void shouldFindAllCartItemsByCartId() {
            // Given: 同一カートに複数のアイテムを登録
            cartItemDao.insert(testCartItem); // 商品ID: 1, 数量: 2
            
            CartItem anotherItem = new CartItem();
            anotherItem.setCartId(testCart.getId());
            anotherItem.setProductId(2L);
            anotherItem.setQuantity(3);
            anotherItem.setCreatedAt(fixedDateTime);
            anotherItem.setUpdatedAt(fixedDateTime);
            cartItemDao.insert(anotherItem); // 商品ID: 2, 数量: 3
            
            // When: カートIDで検索
            List<CartItem> results = cartItemDao.findByCartId(testCart.getId());
            
            // Then: 結果を検証
            assertThat(results).isNotNull().hasSize(2);
            assertThat(results)
                .extracting("cartId", "productId", "quantity")
                .containsExactlyInAnyOrder(
                    tuple(testCart.getId(), 1L, 2),
                    tuple(testCart.getId(), 2L, 3)
                );
        }
        
        @Test
        @DisplayName("存在しないカートIDでは空リストが返される")
        void shouldReturnEmptyListWhenCartIdDoesNotExist() {
            // Given: 存在しないカートID
            Long nonExistentCartId = 999L;
            
            // When: 存在しないカートIDで検索
            List<CartItem> results = cartItemDao.findByCartId(nonExistentCartId);
            
            // Then: 空リストが返されることを検証
            assertThat(results).isNotNull().isEmpty();
        }
        
        @Test
        @DisplayName("カートが空の場合は空リストが返される")
        void shouldReturnEmptyListWhenCartIsEmpty() {
            // Given: カートにアイテムが追加されていない状態
            
            // When: カートIDで検索
            List<CartItem> results = cartItemDao.findByCartId(testCart.getId());
            
            // Then: 空リストが返されることを検証
            assertThat(results).isNotNull().isEmpty();
        }
    }
    
    @Nested
    @DisplayName("findByCartIdAndProductId(): カートIDと商品ID検索")
    class FindByCartIdAndProductIdTests {
        
        @Test
        @DisplayName("カートIDと商品IDの組み合わせで特定のカートアイテムを取得できる")
        void shouldFindCartItemByCartIdAndProductId() {
            // Given: カートアイテムを登録
            cartItemDao.insert(testCartItem);
            
            // When: カートIDと商品IDで検索
            Optional<CartItem> result = cartItemDao.findByCartIdAndProductId(testCart.getId(), 1L);
            
            // Then: 結果を検証
            assertThat(result).isPresent();
            assertThat(result.get().getCartId()).isEqualTo(testCart.getId());
            assertThat(result.get().getProductId()).isEqualTo(1L);
            assertThat(result.get().getQuantity()).isEqualTo(2);
        }
        
        @Test
        @DisplayName("存在しない組み合わせでは空のOptionalが返される")
        void shouldReturnEmptyOptionalWhenCombinationDoesNotExist() {
            // Given: アイテムを登録
            cartItemDao.insert(testCartItem);
            
            // When: 存在しない商品IDで検索
            Optional<CartItem> result = cartItemDao.findByCartIdAndProductId(testCart.getId(), 999L);
            
            // Then: 空のOptionalが返されることを検証
            assertThat(result).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("update(): カートアイテム更新処理")
    class UpdateTests {
        
        @Test
        @DisplayName("既存のカートアイテムを正常に更新できる")
        void shouldUpdateExistingCartItem() {
            // Given: 既存のカートアイテム
            cartItemDao.insert(testCartItem);
            
            // 更新用データを準備
            testCartItem.setQuantity(5); // 数量を変更
            testCartItem.setUpdatedAt(fixedDateTime.plusHours(1)); // 更新日時を変更
            
            // When: 更新を実行
            int result = cartItemDao.update(testCartItem);
            
            // Then: 結果を検証
            assertThat(result).as("更新成功時は1が返される").isEqualTo(1);
            
            // データベースから再取得して検証
            Optional<CartItem> updatedItem = cartItemDao.findById(testCartItem.getId());
            assertThat(updatedItem).isPresent();
            assertThat(updatedItem.get().getQuantity()).isEqualTo(5);
            assertThat(updatedItem.get().getUpdatedAt()).isEqualToIgnoringNanos(fixedDateTime.plusHours(1));
        }
        
        @Test
        @DisplayName("存在しないIDの更新は失敗する")
        void shouldFailToUpdateNonExistentCartItem() {
            // Given: 存在しないIDのカートアイテム
            testCartItem.setId(999L); // 存在しないID
            
            // When: 更新を実行
            int result = cartItemDao.update(testCartItem);
            
            // Then: 更新が失敗することを検証
            assertThat(result).as("更新対象が存在しない場合は0が返される").isEqualTo(0);
        }
        
        @Test
        @DisplayName("不正な数量での更新は例外となる")
        void shouldThrowExceptionWhenUpdatingWithInvalidQuantity() {
            // Given: 既存のカートアイテム
            cartItemDao.insert(testCartItem);
            
            // 不正な数量に変更
            testCartItem.setQuantity(0); // 0は不正
            
            // When & Then: 更新で例外がスローされることを検証
            assertThatThrownBy(() -> cartItemDao.update(testCartItem))
                .as("商品数量は正の整数でなければならない")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
    }
    
    @Nested
    @DisplayName("delete(): カートアイテム削除処理")
    class DeleteTests {
        
        @Test
        @DisplayName("既存のカートアイテムを正常に削除できる")
        void shouldDeleteExistingCartItem() {
            // Given: 既存のカートアイテム
            cartItemDao.insert(testCartItem);
            
            // When: 削除を実行
            int result = cartItemDao.delete(testCartItem);
            
            // Then: 結果を検証
            assertThat(result).as("削除成功時は1が返される").isEqualTo(1);
            
            // データベースから再取得して削除を確認
            Optional<CartItem> deleted = cartItemDao.findById(testCartItem.getId());
            assertThat(deleted).as("削除後はfindByIdで取得できない").isEmpty();
            
            // カートIDによる検索でも取得できないことを確認
            List<CartItem> items = cartItemDao.findByCartId(testCart.getId());
            assertThat(items).as("カートが空になっていることを確認").isEmpty();
        }
        
        @Test
        @DisplayName("存在しないIDの削除は失敗する")
        void shouldFailToDeleteNonExistentCartItem() {
            // Given: 存在しないIDのカートアイテム
            testCartItem.setId(999L); // 存在しないID
            
            // When: 削除を実行
            int result = cartItemDao.delete(testCartItem);
            
            // Then: 削除が失敗することを検証
            assertThat(result).as("削除対象が存在しない場合は0が返される").isEqualTo(0);
        }
    }
} 