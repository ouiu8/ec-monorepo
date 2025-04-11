package com.example.ecbackend.controller;

import com.example.ecbackend.entity.CartItem;
import com.example.ecbackend.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CartControllerのテスト
 * 
 * 設計原則:
 * 1. 各エンドポイントの正常系と異常系をカバー
 * 2. モックを使用してサービスレイヤーを分離
 * 3. BDDスタイル（given-when-then）でテスト構造を明確に
 * 4. セキュリティ要件を考慮したテスト設計
 * 5. パラメータ化テストで境界値テスト
 * 6. エラーハンドリングの網羅的検証
 */
@WebMvcTest(CartController.class)
@ActiveProfiles("test")
@WithMockUser
@DisplayName("CartController: カートAPIのテスト")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private final String SESSION_ID_HEADER = "X-Session-ID";
    private final String sessionId = "test-session-id";

    // テスト用データ生成ヘルパーメソッド
    private CartItem createCartItem(Long id, Long cartId, Long productId, int quantity) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setCartId(cartId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        return item;
    }

    /**
     * APIリクエストを実行するヘルパーメソッド
     * 
     * @param method HTTPメソッド
     * @param uri URI
     * @param sessionId セッションID
     * @param requestBody リクエストボディ（nullの場合は送信しない）
     * @param withCsrf CSRFトークンを追加するかどうか
     * @return ResultActions
     */
    private ResultActions performRequest(String method, String uri, String sessionId, 
                                         Object requestBody, boolean withCsrf) throws Exception {
        switch (method.toUpperCase()) {
            case "GET":
                return mockMvc.perform(get(uri)
                        .header(SESSION_ID_HEADER, sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print());
            case "POST":
                return mockMvc.perform(post(uri)
                        .with(withCsrf ? SecurityMockMvcRequestPostProcessors.csrf() : req -> req)
                        .header(SESSION_ID_HEADER, sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody != null ? objectMapper.writeValueAsString(requestBody) : ""))
                        .andDo(print());
            case "PUT":
                return mockMvc.perform(put(uri)
                        .with(withCsrf ? SecurityMockMvcRequestPostProcessors.csrf() : req -> req)
                        .header(SESSION_ID_HEADER, sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody != null ? objectMapper.writeValueAsString(requestBody) : ""))
                        .andDo(print());
            case "DELETE":
                return mockMvc.perform(delete(uri)
                        .with(withCsrf ? SecurityMockMvcRequestPostProcessors.csrf() : req -> req)
                        .header(SESSION_ID_HEADER, sessionId))
                        .andDo(print());
            default:
                throw new IllegalArgumentException("未対応のHTTPメソッド: " + method);
        }
    }

    @Nested
    @DisplayName("GET /api/cart: カート内商品一覧取得")
    class GetCartTests {
        
        @Test
        @DisplayName("カート内の商品一覧を取得できる")
        void shouldReturnCartItems() throws Exception {
            // Given: カート内に複数の商品がある場合
            List<CartItem> cartItems = Arrays.asList(
                createCartItem(1L, 1L, 1L, 2),
                createCartItem(2L, 1L, 2L, 1)
            );
            given(cartService.getCartItems(sessionId)).willReturn(cartItems);

            // When: カート一覧APIを呼び出す
            ResultActions response = performRequest("GET", "/api/cart", sessionId, null, false);

            // Then: 正常なレスポンスとカート内商品一覧が返される
            response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].cartId", is(1)))
                .andExpect(jsonPath("$[0].productId", is(1)))
                .andExpect(jsonPath("$[0].quantity", is(2)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].quantity", is(1)));

            verify(cartService, times(1)).getCartItems(sessionId);
        }
        
        @Test
        @DisplayName("空のカートの場合は空配列を返す")
        void shouldReturnEmptyArrayWhenCartIsEmpty() throws Exception {
            // Given: カートが空の場合
            given(cartService.getCartItems(sessionId)).willReturn(Collections.emptyList());

            // When: カート一覧APIを呼び出す
            ResultActions response = performRequest("GET", "/api/cart", sessionId, null, false);

            // Then: 正常なレスポンスと空配列が返される
            response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", is(empty())));

            verify(cartService, times(1)).getCartItems(sessionId);
        }
        
        @Test
        @DisplayName("セッションIDヘッダーがない場合は400エラーを返す")
        void shouldReturnBadRequestWhenSessionIdIsMissing() throws Exception {
            // When: セッションIDなしでAPIを呼び出す
            ResultActions response = mockMvc.perform(get("/api/cart")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

            // Then: 400 Bad Requestが返される
            response.andExpect(status().isBadRequest());
            
            verify(cartService, never()).getCartItems(any());
        }
        
        @Test
        @DisplayName("サービス層で予期しない例外が発生した場合は500エラーを返す")
        void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() throws Exception {
            // Given: サービス層で予期しない例外が発生する
            String errorMessage = "Database connection error";
            given(cartService.getCartItems(sessionId)).willThrow(new RuntimeException(errorMessage));
            
            // When: カート一覧APIを呼び出す
            ResultActions response = performRequest("GET", "/api/cart", sessionId, null, false);
            
            // Then: 500エラーが返される
            response.andExpect(status().isInternalServerError());
            
            verify(cartService, times(1)).getCartItems(sessionId);
        }
    }

    @Nested
    @DisplayName("POST /api/cart: カートへの商品追加")
    class AddToCartTests {
        
        @Test
        @DisplayName("カートに商品を追加できる")
        void shouldAddItemToCart() throws Exception {
            // Given: 追加する商品情報
            CartItem itemToAdd = createCartItem(null, null, 1L, 2);
            CartItem addedItem = createCartItem(1L, 1L, 1L, 2);
            
            given(cartService.addToCart(eq(sessionId), eq(1L), eq(2))).willReturn(addedItem);

            // When: カート追加APIを呼び出す
            ResultActions response = performRequest("POST", "/api/cart", sessionId, itemToAdd, true);

            // Then: 201 Createdと追加された商品情報が返される
            response
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cartId", is(1)))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.quantity", is(2)));

            verify(cartService, times(1)).addToCart(sessionId, 1L, 2);
        }
        
        @Test
        @DisplayName("カートに商品を追加する際にCSRFトークンがない場合は403エラーを返す")
        void shouldReturnForbiddenWhenCsrfIsMissing() throws Exception {
            // Given: 追加する商品情報
            CartItem itemToAdd = createCartItem(null, null, 1L, 2);
            
            // When: CSRFトークンなしでAPIを呼び出す（withCsrf=false）
            ResultActions response = performRequest("POST", "/api/cart", sessionId, itemToAdd, false);

            // Then: 403 Forbiddenが返される
            response.andExpect(status().isForbidden());
            
            verify(cartService, never()).addToCart(any(), anyLong(), anyInt());
        }
        
        @Test
        @DisplayName("不正な商品情報でカートに追加しようとすると400エラーを返す")
        void shouldReturnBadRequestWhenProductDataIsInvalid() throws Exception {
            // Given: 商品情報に不備がある場合（productIdがnull）
            CartItem invalidItem = new CartItem();
            invalidItem.setQuantity(2);
            // productIdが設定されていない
            
            // Mockサービス層でエラーをスロー
            given(cartService.addToCart(anyString(), isNull(), anyInt()))
                .willThrow(new IllegalArgumentException("Product ID cannot be null"));
            
            // When: 不正なデータでAPIを呼び出す
            ResultActions response = performRequest("POST", "/api/cart", sessionId, invalidItem, true);

            // Then: 400 Bad Requestが返される
            response.andExpect(status().isBadRequest());
            
            verify(cartService, never()).addToCart(any(), anyLong(), anyInt());
        }
        
        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 1000})
        @DisplayName("境界値で商品数量を検証する")
        void shouldValidateQuantityBoundaries(int quantity) throws Exception {
            // Given: 境界値の数量
            CartItem itemToAdd = createCartItem(null, null, 1L, quantity);
            
            // 予想される結果（数量に応じて）
            ResultMatcher expectedStatus;
            if (quantity < 0) {
                // 負の数量はバリデーションエラー
                given(cartService.addToCart(eq(sessionId), eq(1L), eq(quantity)))
                    .willThrow(new IllegalArgumentException("Quantity must be positive"));
                expectedStatus = status().isBadRequest();
            } else if (quantity > 99) {
                // 過剰な数量はバリデーションエラー
                given(cartService.addToCart(eq(sessionId), eq(1L), eq(quantity)))
                    .willThrow(new IllegalArgumentException("Quantity exceeds maximum allowed"));
                expectedStatus = status().isBadRequest();
            } else {
                // 正常範囲内
                CartItem addedItem = createCartItem(1L, 1L, 1L, quantity);
                given(cartService.addToCart(eq(sessionId), eq(1L), eq(quantity))).willReturn(addedItem);
                expectedStatus = status().isCreated();
            }
            
            // When: カート追加APIを呼び出す
            ResultActions response = performRequest("POST", "/api/cart", sessionId, itemToAdd, true);
            
            // Then: 期待されるステータスが返される
            response.andExpect(expectedStatus);
        }
        
        @Test
        @DisplayName("サービス層で例外が発生した場合は500エラーを返す")
        void shouldReturn500WhenServiceThrowsException() throws Exception {
            // Given: サービス層で例外が発生する場合
            CartItem itemToAdd = createCartItem(null, null, 1L, 2);
            
            given(cartService.addToCart(anyString(), anyLong(), anyInt()))
                .willThrow(new RuntimeException("Database error"));

            // When: APIを呼び出す
            ResultActions response = performRequest("POST", "/api/cart", sessionId, itemToAdd, true);

            // Then: 500 Internal Server Errorが返される
            response.andExpect(status().isInternalServerError());
            
            verify(cartService, times(1)).addToCart(eq(sessionId), eq(1L), eq(2));
        }
    }

    @Nested
    @DisplayName("PUT /api/cart/{id}: カート内商品更新")
    class UpdateCartItemTests {
        
        @Test
        @DisplayName("カート内の商品数量を更新できる")
        void shouldUpdateCartItem() throws Exception {
            // Given: 更新する商品情報
            Long cartItemId = 1L;
            CartItem itemToUpdate = new CartItem();
            itemToUpdate.setQuantity(3);
            
            willDoNothing().given(cartService).updateCartItem(sessionId, cartItemId, 3);

            // When: カート更新APIを呼び出す
            ResultActions response = performRequest("PUT", "/api/cart/" + cartItemId, sessionId, itemToUpdate, true);

            // Then: 204 No Contentが返される
            response.andExpect(status().isNoContent());
            
            verify(cartService, times(1)).updateCartItem(sessionId, cartItemId, 3);
        }
        
        @Test
        @DisplayName("存在しない商品IDで更新しようとすると404エラーを返す")
        void shouldReturn404WhenItemDoesNotExist() throws Exception {
            // Given: 存在しない商品ID
            Long nonExistentItemId = 999L;
            CartItem itemToUpdate = new CartItem();
            itemToUpdate.setQuantity(3);
            
            willThrow(new NoSuchElementException("Cart item not found"))
                .given(cartService).updateCartItem(sessionId, nonExistentItemId, 3);

            // When: 存在しないIDでAPIを呼び出す
            ResultActions response = performRequest("PUT", "/api/cart/" + nonExistentItemId, sessionId, itemToUpdate, true);

            // Then: 404 Not Foundが返される
            response.andExpect(status().isNotFound());
            
            verify(cartService, times(1)).updateCartItem(sessionId, nonExistentItemId, 3);
        }
        
        @Test
        @DisplayName("不正な商品IDフォーマットで更新しようとすると400エラーを返す")
        void shouldReturn400WhenItemIdFormatIsInvalid() throws Exception {
            // Given: 不正なID形式
            String invalidItemId = "invalid-id";
            CartItem itemToUpdate = new CartItem();
            itemToUpdate.setQuantity(3);
            
            // When: 不正なID形式でAPIを呼び出す（Long型に変換できない）
            ResultActions response = performRequest("PUT", "/api/cart/" + invalidItemId, sessionId, itemToUpdate, true);
            
            // Then: 400 Bad Requestが返される（型変換エラー）
            response.andExpect(status().isBadRequest());
            
            verify(cartService, never()).updateCartItem(anyString(), anyLong(), anyInt());
        }
        
        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 1000})
        @DisplayName("更新で境界値の数量をチェックする")
        void shouldValidateQuantityBoundariesForUpdate(int quantity) throws Exception {
            // Given: 境界値の数量
            Long cartItemId = 1L;
            CartItem itemToUpdate = new CartItem();
            itemToUpdate.setQuantity(quantity);
            
            // 予想される結果（数量に応じて）
            ResultMatcher expectedStatus;
            if (quantity < 0) {
                // 負の数量はバリデーションエラー
                willThrow(new IllegalArgumentException("Quantity must be positive"))
                    .given(cartService).updateCartItem(sessionId, cartItemId, quantity);
                expectedStatus = status().isBadRequest();
            } else if (quantity > 99) {
                // 過剰な数量はバリデーションエラー
                willThrow(new IllegalArgumentException("Quantity exceeds maximum allowed"))
                    .given(cartService).updateCartItem(sessionId, cartItemId, quantity);
                expectedStatus = status().isBadRequest();
            } else {
                // 正常範囲内
                willDoNothing().given(cartService).updateCartItem(sessionId, cartItemId, quantity);
                expectedStatus = status().isNoContent();
            }
            
            // When: カート更新APIを呼び出す
            ResultActions response = performRequest("PUT", "/api/cart/" + cartItemId, sessionId, itemToUpdate, true);
            
            // Then: 期待されるステータスが返される
            response.andExpect(expectedStatus);
        }
        
        @Test
        @DisplayName("不正な権限で他ユーザーのカート商品を更新しようとすると403エラーを返す")
        void shouldReturn403WhenUpdatingOtherUsersCartItem() throws Exception {
            // Given: 他ユーザーのカート商品へのアクセス試行
            Long cartItemId = 1L;
            CartItem itemToUpdate = new CartItem();
            itemToUpdate.setQuantity(3);
            
            willThrow(new SecurityException("Access denied to cart item"))
                .given(cartService).updateCartItem(sessionId, cartItemId, 3);
            
            // When: カート更新APIを呼び出す
            ResultActions response = performRequest("PUT", "/api/cart/" + cartItemId, sessionId, itemToUpdate, true);
            
            // Then: 403 Forbiddenが返される
            response.andExpect(status().isForbidden());
            
            verify(cartService, times(1)).updateCartItem(sessionId, cartItemId, 3);
        }
    }

    @Nested
    @DisplayName("DELETE /api/cart/{id}: カートから商品削除")
    class RemoveFromCartTests {
        
        @Test
        @DisplayName("カートから商品を削除できる")
        void shouldRemoveItemFromCart() throws Exception {
            // Given: 削除する商品ID
            Long cartItemId = 1L;
            
            willDoNothing().given(cartService).removeFromCart(sessionId, cartItemId);

            // When: カート削除APIを呼び出す
            ResultActions response = performRequest("DELETE", "/api/cart/" + cartItemId, sessionId, null, true);

            // Then: 204 No Contentが返される
            response.andExpect(status().isNoContent());
            
            verify(cartService, times(1)).removeFromCart(sessionId, cartItemId);
        }
        
        @Test
        @DisplayName("存在しない商品IDで削除しようとすると404エラーを返す")
        void shouldReturn404WhenItemDoesNotExist() throws Exception {
            // Given: 存在しない商品ID
            Long nonExistentItemId = 999L;
            
            willThrow(new NoSuchElementException("Cart item not found"))
                .given(cartService).removeFromCart(sessionId, nonExistentItemId);

            // When: 存在しないIDでAPIを呼び出す
            ResultActions response = performRequest("DELETE", "/api/cart/" + nonExistentItemId, sessionId, null, true);

            // Then: 404 Not Foundが返される
            response.andExpect(status().isNotFound());
            
            verify(cartService, times(1)).removeFromCart(sessionId, nonExistentItemId);
        }
        
        @Test
        @DisplayName("不正な商品IDフォーマットで削除しようとすると400エラーを返す")
        void shouldReturn400WhenItemIdFormatIsInvalid() throws Exception {
            // Given: 不正なID形式
            String invalidItemId = "invalid-id";
            
            // When: 不正なID形式でAPIを呼び出す（Long型に変換できない）
            ResultActions response = performRequest("DELETE", "/api/cart/" + invalidItemId, sessionId, null, true);
            
            // Then: 400 Bad Requestが返される（型変換エラー）
            response.andExpect(status().isBadRequest());
            
            verify(cartService, never()).removeFromCart(anyString(), anyLong());
        }
        
        @Test
        @DisplayName("不正な権限で他ユーザーのカート商品を削除しようとすると403エラーを返す")
        void shouldReturn403WhenDeletingOtherUsersCartItem() throws Exception {
            // Given: 他ユーザーのカート商品へのアクセス試行
            Long cartItemId = 1L;
            
            willThrow(new SecurityException("Access denied to cart item"))
                .given(cartService).removeFromCart(sessionId, cartItemId);
            
            // When: カート削除APIを呼び出す
            ResultActions response = performRequest("DELETE", "/api/cart/" + cartItemId, sessionId, null, true);
            
            // Then: 403 Forbiddenが返される
            response.andExpect(status().isForbidden());
            
            verify(cartService, times(1)).removeFromCart(sessionId, cartItemId);
        }
    }
    
    @Nested
    @DisplayName("例外ハンドリングの全般テスト")
    class ExceptionHandlingTests {
        
        @Test
        @DisplayName("セッションIDが空の場合は400エラーを返す")
        void shouldReturn400WhenSessionIdIsEmpty() throws Exception {
            // When: 空のセッションIDでAPIを呼び出す
            ResultActions response = mockMvc.perform(get("/api/cart")
                .header(SESSION_ID_HEADER, "")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());
            
            // Then: 400 Bad Requestが返される
            response.andExpect(status().isBadRequest());
            
            verify(cartService, never()).getCartItems(anyString());
        }
        
        @Test
        @DisplayName("メモリ不足などのシステムエラーでは500エラーを返す")
        void shouldReturn500OnSystemError() throws Exception {
            // Given: システムエラー（OutOfMemoryError）
            given(cartService.getCartItems(sessionId)).willAnswer(invocation -> {
                throw new OutOfMemoryError("Simulated system error");
            });
            
            // When: APIを呼び出す
            ResultActions response = performRequest("GET", "/api/cart", sessionId, null, false);
            
            // Then: 500エラーが返される
            response.andExpect(status().isInternalServerError());
        }
        
        @Test
        @DisplayName("不正なHTTPメソッドでアクセスすると405エラーを返す")
        void shouldReturn405ForInvalidHttpMethod() throws Exception {
            // When: サポートされていないHTTPメソッド（PATCH）を使用
            ResultActions response = mockMvc.perform(patch("/api/cart/1")
                .header(SESSION_ID_HEADER, sessionId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print());
            
            // Then: 405 Method Not Allowedが返される
            response.andExpect(status().isMethodNotAllowed());
        }
    }
}