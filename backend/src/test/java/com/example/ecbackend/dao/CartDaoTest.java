package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CartDao のインテグレーションテスト
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
@DisplayName("CartDao: カート永続化層のテスト")
public class CartDaoTest {

    @Autowired
    private CartDao cartDao;

    private String validSessionId;
    private LocalDateTime fixedDateTime;

    @BeforeEach
    void setUp() {
        // テスト用データの準備
        validSessionId = UUID.randomUUID().toString();
        fixedDateTime = LocalDateTime.now();
    }

    @Nested
    @DisplayName("insert(): カート登録処理")
    class InsertTests {
        
        @Test
        @DisplayName("有効なカート情報でデータが正常に登録できる")
        void shouldInsertValidCart() {
            // Given: 有効なカートエンティティを準備
            Cart cart = new Cart();
            cart.setSessionId(validSessionId);
            cart.setCreatedAt(fixedDateTime);
            cart.setUpdatedAt(fixedDateTime);
            
            // When: カートを登録
            int result = cartDao.insert(cart);
            
            // Then: 検証
            assertThat(result).as("挿入成功時は1が返される").isEqualTo(1);
            assertThat(cart.getId()).as("IDが自動生成される").isNotNull().isPositive();
            
            // データベースから取得して検証
            Optional<Cart> savedCart = cartDao.findBySessionId(validSessionId);
            assertThat(savedCart).isPresent();
            assertThat(savedCart.get().getSessionId()).isEqualTo(validSessionId);
            assertThat(savedCart.get().getCreatedAt()).isEqualToIgnoringNanos(fixedDateTime);
            assertThat(savedCart.get().getUpdatedAt()).isEqualToIgnoringNanos(fixedDateTime);
        }
        
        @Test
        @DisplayName("セッションIDが重複している場合は例外がスローされる")
        void shouldThrowExceptionWhenSessionIdIsDuplicated() {
            // Given: 同じセッションIDを持つカートエンティティを2つ準備
            Cart cart1 = new Cart();
            cart1.setSessionId(validSessionId);
            cart1.setCreatedAt(fixedDateTime);
            cart1.setUpdatedAt(fixedDateTime);
            cartDao.insert(cart1);
            
            Cart cart2 = new Cart();
            cart2.setSessionId(validSessionId);
            cart2.setCreatedAt(fixedDateTime);
            cart2.setUpdatedAt(fixedDateTime);
            
            // When & Then: 2つ目のカート登録で例外がスローされることを検証
            assertThatThrownBy(() -> cartDao.insert(cart2))
                .as("セッションIDがユニークであることを確認")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
        
        @ParameterizedTest
        @NullSource
        @DisplayName("必須フィールドがnullの場合は例外がスローされる")
        void shouldThrowExceptionWhenRequiredFieldIsNull(String nullSessionId) {
            // Given: セッションIDがnullのカートエンティティを準備
            Cart cart = new Cart();
            cart.setSessionId(nullSessionId);
            cart.setCreatedAt(fixedDateTime);
            cart.setUpdatedAt(fixedDateTime);
            
            // When & Then: カート登録で例外がスローされることを検証
            assertThatThrownBy(() -> cartDao.insert(cart))
                .as("セッションIDがnullの場合は例外が発生")
                .isInstanceOf(DataIntegrityViolationException.class);
        }
    }
    
    @Nested
    @DisplayName("findBySessionId(): セッションIDによるカート検索")
    class FindBySessionIdTests {
        
        @Test
        @DisplayName("存在するセッションIDでカートが取得できる")
        void shouldFindCartWhenSessionIdExists() {
            // Given: テストデータをDBに準備
            Cart cart = new Cart();
            cart.setSessionId(validSessionId);
            cart.setCreatedAt(fixedDateTime);
            cart.setUpdatedAt(fixedDateTime);
            cartDao.insert(cart);
            
            // When: セッションIDでカートを検索
            Optional<Cart> result = cartDao.findBySessionId(validSessionId);
            
            // Then: 検証
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(cart.getId());
            assertThat(result.get().getSessionId()).isEqualTo(validSessionId);
            assertThat(result.get().getCreatedAt()).isEqualToIgnoringNanos(fixedDateTime);
            assertThat(result.get().getUpdatedAt()).isEqualToIgnoringNanos(fixedDateTime);
        }
        
        @Test
        @DisplayName("存在しないセッションIDではカートが取得できない")
        void shouldNotFindCartWhenSessionIdDoesNotExist() {
            // Given: 存在しないセッションID
            String nonExistentSessionId = "non-existent-" + UUID.randomUUID();
            
            // When: 存在しないセッションIDでカートを検索
            Optional<Cart> result = cartDao.findBySessionId(nonExistentSessionId);
            
            // Then: カートが存在しないことを検証
            assertThat(result).isEmpty();
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("空文字や空白のセッションIDでは空のOptionalが返される")
        void shouldReturnEmptyOptionalWhenSessionIdIsEmptyOrBlank(String emptyOrBlankSessionId) {
            // When: 空または空白のセッションIDでカートを検索
            Optional<Cart> result = cartDao.findBySessionId(emptyOrBlankSessionId);
            
            // Then: カートが存在しないことを検証
            assertThat(result).isEmpty();
        }
        
        @ParameterizedTest
        @NullSource
        @DisplayName("nullのセッションIDでは例外がスローされる")
        void shouldThrowExceptionWhenSessionIdIsNull(String nullSessionId) {
            // When & Then: nullのセッションIDでカートを検索すると例外が発生
            assertThatThrownBy(() -> cartDao.findBySessionId(nullSessionId))
                .isInstanceOf(Exception.class);
        }
    }
} 