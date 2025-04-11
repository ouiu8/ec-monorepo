package com.example.ecbackend.service;

import com.example.ecbackend.dao.CartDao;
import com.example.ecbackend.dao.CartItemDao;
import com.example.ecbackend.entity.Cart;
import com.example.ecbackend.entity.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * CartServiceのユニットテスト
 * 
 * テスト設計原則:
 * 1. 各メソッドの正常系と異常系を網羅
 * 2. モックを使用してDAOレイヤーを分離
 * 3. BDDスタイル（given-when-then）でテスト構造を明確に
 * 4. 境界条件のテストで堅牢性を確保
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartService: カートビジネスロジックのテスト")
class CartServiceTest {
    
    @Mock
    private CartDao cartDao;

    @Mock
    private CartItemDao cartItemDao;

    @InjectMocks
    private CartService cartService;

    private final String sessionId = "test-session-id";
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        // テスト用データの初期化
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setSessionId(sessionId);
        
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCartId(1L);
        testCartItem.setProductId(1L);
        testCartItem.setQuantity(2);
    }

    @Nested
    @DisplayName("getCartItems(): カート内商品取得")
    class GetCartItemsTests {
        
        @Test
        @DisplayName("カートが存在する場合、商品のリストを返す")
        void shouldReturnItemsWhenCartExists() {
            // Given: カートが存在し、商品が含まれている
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.of(testCart));
            given(cartItemDao.findByCartId(testCart.getId())).willReturn(Arrays.asList(testCartItem));

            // When: カート内商品を取得
            List<CartItem> result = cartService.getCartItems(sessionId);

            // Then: 正しい商品リストが返される
            assertThat(result).isNotNull().hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getCartId()).isEqualTo(1L);
            assertThat(result.get(0).getProductId()).isEqualTo(1L);
            assertThat(result.get(0).getQuantity()).isEqualTo(2);
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao).findByCartId(testCart.getId());
        }

        @Test
        @DisplayName("カートが存在しない場合、空のリストを返す")
        void shouldReturnEmptyListWhenCartDoesNotExist() {
            // Given: カートが存在しない
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.empty());

            // When: カート内商品を取得
            List<CartItem> result = cartService.getCartItems(sessionId);

            // Then: 空のリストが返される
            assertThat(result).isNotNull().isEmpty();
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao, never()).findByCartId(anyLong());
        }
    }

    @Nested
    @DisplayName("addToCart(): カートへの商品追加")
    class AddToCartTests {
        
        @Test
        @DisplayName("カートが既に存在する場合、商品を追加できる")
        void shouldAddItemToExistingCart() {
            // Given: 既存のカートがある
            Long productId = 1L;
            int quantity = 2;
            
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.of(testCart));
            given(cartItemDao.insert(any(CartItem.class))).willAnswer(invocation -> {
                CartItem item = invocation.getArgument(0);
                item.setId(1L);
                return 1;
            });

            // When: カートに商品を追加
            CartItem result = cartService.addToCart(sessionId, productId, quantity);

            // Then: 商品が正しく追加される
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCartId()).isEqualTo(testCart.getId());
            assertThat(result.getProductId()).isEqualTo(productId);
            assertThat(result.getQuantity()).isEqualTo(quantity);
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao).insert(any(CartItem.class));
        }

        @Test
        @DisplayName("カートが存在しない場合、新しいカートを作成して商品を追加できる")
        void shouldCreateNewCartAndAddItem() {
            // Given: カートが存在しない
            Long productId = 1L;
            int quantity = 2;
            
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.empty());
            given(cartDao.insert(any(Cart.class))).willAnswer(invocation -> {
                Cart cart = invocation.getArgument(0);
                cart.setId(1L);
                return 1;
            });
            given(cartItemDao.insert(any(CartItem.class))).willAnswer(invocation -> {
                CartItem item = invocation.getArgument(0);
                item.setId(1L);
                return 1;
            });

            // When: カートに商品を追加
            CartItem result = cartService.addToCart(sessionId, productId, quantity);

            // Then: 新しいカートが作成され、商品が追加される
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCartId()).isEqualTo(1L);
            assertThat(result.getProductId()).isEqualTo(productId);
            assertThat(result.getQuantity()).isEqualTo(quantity);
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartDao).insert(any(Cart.class));
            verify(cartItemDao).insert(any(CartItem.class));
        }
    }

    @Nested
    @DisplayName("updateCartItem(): カート内商品更新")
    class UpdateCartItemTests {
        
        @Test
        @DisplayName("商品が存在する場合、数量を更新できる")
        void shouldUpdateQuantityOfExistingItem() {
            // Given: カートと商品が存在する
            Long cartItemId = 1L;
            int newQuantity = 5;
            
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.of(testCart));
            given(cartItemDao.findById(cartItemId)).willReturn(Optional.of(testCartItem));
            given(cartItemDao.update(any(CartItem.class))).willReturn(1);

            // When: 商品の数量を更新
            assertDoesNotThrow(() -> cartService.updateCartItem(sessionId, cartItemId, newQuantity));

            // Then: 数量が更新される
            ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemDao).update(cartItemCaptor.capture());
            
            CartItem capturedItem = cartItemCaptor.getValue();
            assertThat(capturedItem.getQuantity()).isEqualTo(newQuantity);
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao).findById(cartItemId);
            verify(cartItemDao).update(any(CartItem.class));
        }

        @Test
        @DisplayName("カートが存在しない場合、例外がスローされる")
        void shouldThrowExceptionWhenCartDoesNotExist() {
            // Given: カートが存在しない
            Long cartItemId = 1L;
            int newQuantity = 5;
            
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.empty());

            // When & Then: 例外がスローされる
            assertThatThrownBy(() -> cartService.updateCartItem(sessionId, cartItemId, newQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cart not found");
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao, never()).findById(anyLong());
            verify(cartItemDao, never()).update(any(CartItem.class));
        }

        @Test
        @DisplayName("商品が存在しない場合、例外がスローされる")
        void shouldThrowExceptionWhenItemDoesNotExist() {
            // Given: カートは存在するが商品が存在しない
            Long cartItemId = 999L;
            int newQuantity = 5;
            
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.of(testCart));
            given(cartItemDao.findById(cartItemId)).willReturn(Optional.empty());

            // When & Then: 例外がスローされる
            assertThatThrownBy(() -> cartService.updateCartItem(sessionId, cartItemId, newQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cart item not found");
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao).findById(cartItemId);
            verify(cartItemDao, never()).update(any(CartItem.class));
        }
    }

    @Nested
    @DisplayName("removeFromCart(): カートからの商品削除")
    class RemoveFromCartTests {
        
        @Test
        @DisplayName("商品が存在する場合、削除できる")
        void shouldRemoveExistingItem() {
            // Given: カートと商品が存在する
            Long cartItemId = 1L;
            
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.of(testCart));
            given(cartItemDao.findById(cartItemId)).willReturn(Optional.of(testCartItem));
            given(cartItemDao.delete(any(CartItem.class))).willReturn(1);

            // When: 商品を削除
            assertDoesNotThrow(() -> cartService.removeFromCart(sessionId, cartItemId));

            // Then: 商品が削除される
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao).findById(cartItemId);
            verify(cartItemDao).delete(any(CartItem.class));
        }

        @Test
        @DisplayName("カートが存在しない場合、例外がスローされる")
        void shouldThrowExceptionWhenCartDoesNotExist() {
            // Given: カートが存在しない
            Long cartItemId = 1L;
            
            given(cartDao.findBySessionId(sessionId)).willReturn(Optional.empty());

            // When & Then: 例外がスローされる
            assertThatThrownBy(() -> cartService.removeFromCart(sessionId, cartItemId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cart not found");
            
            verify(cartDao).findBySessionId(sessionId);
            verify(cartItemDao, never()).findById(anyLong());
            verify(cartItemDao, never()).delete(any(CartItem.class));
        }
    }
} 