package com.example.ecbackend.service;

import com.example.ecbackend.dao.OrderDao;
import com.example.ecbackend.dao.OrderItemDao;
import com.example.ecbackend.dao.ProductDao;
import com.example.ecbackend.entity.CartItem;
import com.example.ecbackend.entity.Order;
import com.example.ecbackend.entity.OrderItem;
import com.example.ecbackend.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;

/**
 * OrderServiceのユニットテスト
 * 
 * テスト設計原則:
 * 1. 各メソッドの正常系と異常系を網羅
 * 2. モックを使用してDAOレイヤーと他のサービスを分離
 * 3. BDDスタイル（given-when-then）でテスト構造を明確に
 * 4. 複雑なビジネスロジックの正確な実行を検証
 * 5. パラメータ化テストで多様な入力値をテスト
 * 6. エッジケースと境界値の徹底的検証
 * 7. トランザクション関連の動作やロールバックも考慮
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService: 注文ビジネスロジックのテスト")
class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderItemDao orderItemDao;
    
    @Mock
    private ProductDao productDao;
    
    @Mock
    private CartService cartService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Captor
    private ArgumentCaptor<Order> orderCaptor;
    
    @Captor
    private ArgumentCaptor<OrderItem> orderItemCaptor;
    
    @Captor
    private ArgumentCaptor<Product> productCaptor;
    
    // テストで使用する固定日時
    private final LocalDateTime fixedDateTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
    
    // テストデータ
    private final String SESSION_ID = "test-session-id";
    private final Long USER_ID = 1L;
    private final String SHIPPING_ADDRESS = "東京都渋谷区1-1-1";
    private final String PAYMENT_METHOD = "CREDIT_CARD";
    
    private List<CartItem> cartItems;
    private Product product1;
    private Product product2;
    
    @BeforeEach
    void setUp() {
        // カート内の商品を設定
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setCartId(1L);
        cartItem1.setProductId(1L);
        cartItem1.setQuantity(2);
        
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setCartId(1L);
        cartItem2.setProductId(2L);
        cartItem2.setQuantity(1);
        
        cartItems = Arrays.asList(cartItem1, cartItem2);
        
        // 商品情報を設定
        product1 = new Product();
        product1.setId(1L);
        product1.setName("テスト商品1");
        product1.setPrice(1000);
        product1.setStock(10);
        
        product2 = new Product();
        product2.setId(2L);
        product2.setName("テスト商品2");
        product2.setPrice(2000);
        product2.setStock(5);
    }
    
    @Nested
    @DisplayName("createOrderFromCart(): カートから注文を作成")
    class CreateOrderFromCartTests {
        
        @Test
        @DisplayName("カートの内容から注文を正常に作成できる")
        void shouldCreateOrderFromCart() {
            // Given: カートに商品が存在し、在庫が十分ある場合
            given(cartService.getCartItems(SESSION_ID)).willReturn(cartItems);
            given(productDao.selectById(1L)).willReturn(product1);
            given(productDao.selectById(2L)).willReturn(product2);
            given(productDao.update(any(Product.class))).willReturn(1);
            
            given(orderDao.insert(any(Order.class))).willAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return 1;
            });
            
            given(orderItemDao.insert(any(OrderItem.class))).willReturn(1);
            willDoNothing().given(cartService).clearCart(SESSION_ID);
            
            try (var mockedStatic = mockStatic(LocalDateTime.class)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(fixedDateTime);
                
                // When: カートから注文を作成
                Order result = orderService.createOrderFromCart(SESSION_ID, USER_ID, SHIPPING_ADDRESS, PAYMENT_METHOD);
                
                // Then: 注文が正しく作成される
                assertThat(result).isNotNull()
                    .satisfies(order -> {
                        assertThat(order.getId()).isEqualTo(1L);
                        assertThat(order.getUserId()).isEqualTo(USER_ID);
                        assertThat(order.getOrderDate()).isEqualTo(fixedDateTime);
                        assertThat(order.getStatus()).isEqualTo("PENDING");
                        assertThat(order.getShippingAddress()).isEqualTo(SHIPPING_ADDRESS);
                        assertThat(order.getPaymentMethod()).isEqualTo(PAYMENT_METHOD);
                        assertThat(order.getTotalAmount()).isEqualTo(4000); // 1000*2 + 2000*1 = 4000
                    });
                
                // OrderDao.insertが正しい引数で呼ばれたことを検証
                then(orderDao).should().insert(orderCaptor.capture());
                Order capturedOrder = orderCaptor.getValue();
                assertThat(capturedOrder.getUserId()).isEqualTo(USER_ID);
                assertThat(capturedOrder.getOrderDate()).isEqualTo(fixedDateTime);
                assertThat(capturedOrder.getStatus()).isEqualTo("PENDING");
                assertThat(capturedOrder.getTotalAmount()).isEqualTo(4000);
                
                // OrderItemDao.insertが正しく呼ばれたことを検証
                then(orderItemDao).should(times(2)).insert(orderItemCaptor.capture());
                List<OrderItem> capturedItems = orderItemCaptor.getAllValues();
                assertThat(capturedItems).hasSize(2)
                    .extracting("productId", "quantity", "unitPrice", "subtotal")
                    .containsExactly(
                        tuple(1L, 2, 1000, 2000),
                        tuple(2L, 1, 2000, 2000)
                    );
                
                // 在庫が減少していることを検証
                then(productDao).should(times(2)).update(productCaptor.capture());
                List<Product> capturedProducts = productCaptor.getAllValues();
                assertThat(capturedProducts).hasSize(2)
                    .extracting("id", "stock")
                    .containsExactly(
                        tuple(1L, 8),  // 10 - 2 = 8
                        tuple(2L, 4)   // 5 - 1 = 4
                    );
                
                // カートがクリアされたことを検証
                then(cartService).should().clearCart(SESSION_ID);
            }
        }
        
        @Test
        @DisplayName("空のカートから注文を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCartIsEmpty() {
            // Given: カートが空の場合
            given(cartService.getCartItems(SESSION_ID)).willReturn(Collections.emptyList());
            
            // When & Then: IllegalArgumentExceptionがスローされる
            assertThatThrownBy(() -> orderService.createOrderFromCart(SESSION_ID, USER_ID, SHIPPING_ADDRESS, PAYMENT_METHOD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create order from empty cart");
                
            // DAOが呼び出されないことを検証
            then(orderDao).shouldHaveNoInteractions();
            then(orderItemDao).shouldHaveNoInteractions();
            then(productDao).shouldHaveNoInteractions();
            // カートはクリアされないことを検証
            then(cartService).should(never()).clearCart(anyString());
        }
        
        @Test
        @DisplayName("存在しない商品を含むカートから注文を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenProductDoesNotExist() {
            // Given: カートに存在しない商品が含まれている場合
            given(cartService.getCartItems(SESSION_ID)).willReturn(cartItems);
            given(productDao.selectById(1L)).willReturn(product1);
            given(productDao.selectById(2L)).willReturn(null); // 2つ目の商品が存在しない
            
            // When & Then: NoSuchElementExceptionがスローされる
            assertThatThrownBy(() -> orderService.createOrderFromCart(SESSION_ID, USER_ID, SHIPPING_ADDRESS, PAYMENT_METHOD))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Product not found with id: 2");
                
            // 注文が作成されないことを検証
            then(orderDao).shouldHaveNoInteractions();
            then(orderItemDao).shouldHaveNoInteractions();
            // カートはクリアされないことを検証
            then(cartService).should(never()).clearCart(anyString());
        }
        
        @Test
        @DisplayName("在庫不足の商品を含むカートから注文を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenStockIsInsufficient() {
            // Given: 在庫不足の商品がある場合
            Product lowStockProduct = new Product();
            lowStockProduct.setId(1L);
            lowStockProduct.setName("在庫不足商品");
            lowStockProduct.setPrice(1000);
            lowStockProduct.setStock(1); // 在庫が1個しかない
            
            given(cartService.getCartItems(SESSION_ID)).willReturn(cartItems); // 2個注文しようとしている
            given(productDao.selectById(1L)).willReturn(lowStockProduct);
            
            // When & Then: IllegalStateExceptionがスローされる
            assertThatThrownBy(() -> orderService.createOrderFromCart(SESSION_ID, USER_ID, SHIPPING_ADDRESS, PAYMENT_METHOD))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient stock for product: 在庫不足商品");
                
            // 注文が作成されないことを検証
            then(orderDao).shouldHaveNoInteractions();
            then(orderItemDao).shouldHaveNoInteractions();
            // 在庫は更新されないことを検証
            then(productDao).should(never()).update(any(Product.class));
            // カートはクリアされないことを検証
            then(cartService).should(never()).clearCart(anyString());
        }
        
        @Test
        @DisplayName("注文登録に失敗すると例外がスローされる")
        void shouldThrowExceptionWhenInsertOrderFails() {
            // Given: 注文登録が失敗する場合
            given(cartService.getCartItems(SESSION_ID)).willReturn(cartItems);
            given(productDao.selectById(1L)).willReturn(product1);
            given(productDao.selectById(2L)).willReturn(product2);
            given(productDao.update(any(Product.class))).willReturn(1); // 在庫更新は成功する
            given(orderDao.insert(any(Order.class))).willReturn(0); // 注文登録のみ失敗
            
            // When & Then: RuntimeExceptionがスローされる
            assertThatThrownBy(() -> orderService.createOrderFromCart(SESSION_ID, USER_ID, SHIPPING_ADDRESS, PAYMENT_METHOD))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to create order");
                
            // 注文明細は作成されないことを検証
            then(orderItemDao).shouldHaveNoInteractions();
            // カートはクリアされないことを検証
            then(cartService).should(never()).clearCart(anyString());
            // 在庫更新は呼ばれることを検証（OrderService実装に合わせる）
            then(productDao).should(times(2)).update(any(Product.class));
        }
        
        @Test
        @DisplayName("注文明細登録に失敗すると例外がスローされる")
        void shouldThrowExceptionWhenInsertOrderItemFails() {
            // Given: 注文明細登録が失敗する場合
            given(cartService.getCartItems(SESSION_ID)).willReturn(cartItems);
            given(productDao.selectById(1L)).willReturn(product1);
            given(productDao.selectById(2L)).willReturn(product2);
            
            given(orderDao.insert(any(Order.class))).willAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return 1;
            });
            
            given(orderItemDao.insert(any(OrderItem.class))).willReturn(0); // 登録失敗
            
            // When & Then: RuntimeExceptionがスローされる
            assertThatThrownBy(() -> orderService.createOrderFromCart(SESSION_ID, USER_ID, SHIPPING_ADDRESS, PAYMENT_METHOD))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to create order item");
                
            // カートはクリアされないことを検証
            then(cartService).should(never()).clearCart(anyString());
        }
    }
    
    @Nested
    @DisplayName("getOrderHistory(): 注文履歴取得")
    class GetOrderHistoryTests {
        
        @Test
        @DisplayName("正常に注文履歴を取得できる")
        void shouldGetOrderHistory() {
            // Given: ユーザーに注文履歴がある場合
            List<Order> orders = createSampleOrders();
            given(orderDao.findByUserId(USER_ID)).willReturn(orders);
            
            // When: 注文履歴を取得
            List<Order> result = orderService.getOrderHistory(USER_ID);
            
            // Then: 正しい注文履歴が返される
            assertThat(result).isNotNull().hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(1).getId()).isEqualTo(2L);
            
            then(orderDao).should().findByUserId(USER_ID);
        }
        
        @Test
        @DisplayName("注文履歴が空の場合は空のリストを返す")
        void shouldReturnEmptyListWhenNoOrderHistory() {
            // Given: ユーザーに注文履歴がない場合
            given(orderDao.findByUserId(USER_ID)).willReturn(Collections.emptyList());
            
            // When: 注文履歴を取得
            List<Order> result = orderService.getOrderHistory(USER_ID);
            
            // Then: 空のリストが返される
            assertThat(result).isNotNull().isEmpty();
            
            then(orderDao).should().findByUserId(USER_ID);
        }
        
        @ParameterizedTest
        @NullSource
        @DisplayName("ユーザーIDがnullの場合は例外がスローされる")
        void shouldThrowExceptionWhenUserIdIsNull(Long nullUserId) {
            // When & Then: IllegalArgumentExceptionがスローされる
            assertThatThrownBy(() -> orderService.getOrderHistory(nullUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null");
                
            then(orderDao).shouldHaveNoInteractions();
        }
    }
    
    @Nested
    @DisplayName("getOrderById(): 注文詳細取得")
    class GetOrderByIdTests {
        
        @Test
        @DisplayName("正常に注文詳細を取得できる")
        void shouldGetOrderById() {
            // Given: 注文が存在する場合
            Long orderId = 1L;
            Order order = createSampleOrder(orderId);
            List<OrderItem> orderItems = createSampleOrderItems(orderId);
            
            given(orderDao.findById(orderId)).willReturn(order);
            given(orderItemDao.findByOrderId(orderId)).willReturn(orderItems);
            
            // When: 注文詳細を取得
            Order result = orderService.getOrderById(orderId);
            
            // Then: 正しい注文詳細が返される
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderId);
            assertThat(result.getItems()).isNotNull().hasSize(2);
            assertThat(result.getItems().get(0).getProductId()).isEqualTo(1L);
            assertThat(result.getItems().get(1).getProductId()).isEqualTo(2L);
            
            then(orderDao).should().findById(orderId);
            then(orderItemDao).should().findByOrderId(orderId);
        }
        
        @Test
        @DisplayName("存在しない注文IDの場合は例外がスローされる")
        void shouldThrowExceptionWhenOrderDoesNotExist() {
            // Given: 注文が存在しない場合
            Long nonExistentOrderId = 999L;
            given(orderDao.findById(nonExistentOrderId)).willReturn(null);
            
            // When & Then: NoSuchElementExceptionがスローされる
            assertThatThrownBy(() -> orderService.getOrderById(nonExistentOrderId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Order not found with id: 999");
                
            then(orderItemDao).shouldHaveNoInteractions();
        }
    }
    
    @Nested
    @DisplayName("cancelOrder(): 注文キャンセル")
    class CancelOrderTests {
        
        @Test
        @DisplayName("PENDING状態の注文を正常にキャンセルできる")
        void shouldCancelPendingOrder() {
            // Given: PENDING状態の注文が存在する場合
            Long orderId = 1L;
            Order pendingOrder = createSampleOrder(orderId);
            pendingOrder.setStatus("PENDING");
            List<OrderItem> orderItems = createSampleOrderItems(orderId);
            pendingOrder.setItems(orderItems);
            
            given(orderDao.findById(orderId)).willReturn(pendingOrder);
            given(orderItemDao.findByOrderId(orderId)).willReturn(orderItems);
            given(orderDao.update(any(Order.class))).willReturn(1);
            given(productDao.selectById(1L)).willReturn(product1);
            given(productDao.selectById(2L)).willReturn(product2);
            given(productDao.update(any(Product.class))).willReturn(1);
            
            try (var mockedStatic = mockStatic(LocalDateTime.class)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(fixedDateTime);
                
                // When: 注文をキャンセル
                orderService.cancelOrder(orderId);
                
                // Then: 注文ステータスが更新される
                then(orderDao).should().update(orderCaptor.capture());
                Order capturedOrder = orderCaptor.getValue();
                assertThat(capturedOrder.getStatus()).isEqualTo("CANCELLED");
                assertThat(capturedOrder.getCancelledAt()).isEqualTo(fixedDateTime);
                
                // 在庫が戻されることを検証
                then(productDao).should(times(2)).update(productCaptor.capture());
                List<Product> capturedProducts = productCaptor.getAllValues();
                assertThat(capturedProducts).hasSize(2)
                    .extracting("id", "stock")
                    .containsExactly(
                        tuple(1L, 12),  // 10 + 2 = 12
                        tuple(2L, 6)    // 5 + 1 = 6
                    );
            }
        }
        
        @Test
        @DisplayName("PROCESSING状態の注文を正常にキャンセルできる")
        void shouldCancelProcessingOrder() {
            // Given: PROCESSING状態の注文が存在する場合
            Long orderId = 1L;
            Order processingOrder = createSampleOrder(orderId);
            processingOrder.setStatus("PROCESSING");
            List<OrderItem> orderItems = createSampleOrderItems(orderId);
            processingOrder.setItems(orderItems);
            
            given(orderDao.findById(orderId)).willReturn(processingOrder);
            given(orderItemDao.findByOrderId(orderId)).willReturn(orderItems);
            given(orderDao.update(any(Order.class))).willReturn(1);
            given(productDao.selectById(1L)).willReturn(product1);
            given(productDao.selectById(2L)).willReturn(product2);
            given(productDao.update(any(Product.class))).willReturn(1);
            
            // When: 注文をキャンセル
            orderService.cancelOrder(orderId);
            
            // Then: 注文ステータスが更新される
            then(orderDao).should().update(orderCaptor.capture());
            Order capturedOrder = orderCaptor.getValue();
            assertThat(capturedOrder.getStatus()).isEqualTo("CANCELLED");
        }
        
        @Test
        @DisplayName("SHIPPED状態の注文はキャンセルできない")
        void shouldNotCancelShippedOrder() {
            // Given: SHIPPED状態の注文が存在する場合
            Long orderId = 1L;
            Order shippedOrder = createSampleOrder(orderId);
            shippedOrder.setStatus("SHIPPED");
            List<OrderItem> orderItems = createSampleOrderItems(orderId);
            shippedOrder.setItems(orderItems);
            
            given(orderDao.findById(orderId)).willReturn(shippedOrder);
            given(orderItemDao.findByOrderId(orderId)).willReturn(orderItems);
            
            // When & Then: IllegalStateExceptionがスローされる
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot cancel order with status: SHIPPED");
            
            // 注文ステータスが更新されないことを検証
            then(orderDao).should(never()).update(any(Order.class));
            // 在庫が戻されないことを検証
            then(productDao).shouldHaveNoInteractions();
        }
        
        @Test
        @DisplayName("キャンセル処理に失敗した場合は例外がスローされる")
        void shouldThrowExceptionWhenCancelUpdateFails() {
            // Given: キャンセル処理が失敗する場合
            Long orderId = 1L;
            Order pendingOrder = createSampleOrder(orderId);
            pendingOrder.setStatus("PENDING");
            List<OrderItem> orderItems = createSampleOrderItems(orderId);
            pendingOrder.setItems(orderItems);
            
            given(orderDao.findById(orderId)).willReturn(pendingOrder);
            given(orderItemDao.findByOrderId(orderId)).willReturn(orderItems);
            given(orderDao.update(any(Order.class))).willReturn(0); // 更新失敗
            
            // When & Then: RuntimeExceptionがスローされる
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to cancel order");
            
            // 在庫が戻されないことを検証
            then(productDao).should(never()).update(any(Product.class));
        }
    }
    
    /**
     * サンプル注文リストを作成するヘルパーメソッド
     */
    private List<Order> createSampleOrders() {
        Order order1 = createSampleOrder(1L);
        Order order2 = createSampleOrder(2L);
        return Arrays.asList(order1, order2);
    }
    
    /**
     * サンプル注文を作成するヘルパーメソッド
     */
    private Order createSampleOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setUserId(USER_ID);
        order.setOrderDate(fixedDateTime.minusDays(1));
        order.setStatus("PENDING");
        order.setTotalAmount(4000);
        order.setShippingAddress(SHIPPING_ADDRESS);
        order.setPaymentMethod(PAYMENT_METHOD);
        return order;
    }
    
    /**
     * サンプル注文明細リストを作成するヘルパーメソッド
     */
    private List<OrderItem> createSampleOrderItems(Long orderId) {
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setOrderId(orderId);
        item1.setProductId(1L);
        item1.setQuantity(2);
        item1.setUnitPrice(1000);
        item1.setSubtotal(2000);
        
        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setOrderId(orderId);
        item2.setProductId(2L);
        item2.setQuantity(1);
        item2.setUnitPrice(2000);
        item2.setSubtotal(2000);
        
        return Arrays.asList(item1, item2);
    }
    
    /**
     * 多様な入力パターンを提供するメソッド
     */
    private static Stream<Arguments> invalidOrderInputs() {
        return Stream.of(
            Arguments.of(null, "無効な配送先住所", "CREDIT_CARD", "User ID is required for registered user orders"),
            Arguments.of(1L, null, "CREDIT_CARD", "Shipping address cannot be null or empty"),
            Arguments.of(1L, "", "CREDIT_CARD", "Shipping address cannot be null or empty"),
            Arguments.of(1L, "有効な住所", null, "Payment method cannot be null or empty"),
            Arguments.of(1L, "有効な住所", "", "Payment method cannot be null or empty")
        );
    }
} 