package com.example.ecbackend.service;

import com.example.ecbackend.dao.OrderDao;
import com.example.ecbackend.dao.OrderItemDao;
import com.example.ecbackend.dao.ProductDao;
import com.example.ecbackend.entity.CartItem;
import com.example.ecbackend.entity.Order;
import com.example.ecbackend.entity.OrderItem;
import com.example.ecbackend.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 注文に関するビジネスロジックを提供するサービス
 */
@Service
@Transactional
public class OrderService {
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final ProductDao productDao;
    private final CartService cartService;

    public OrderService(OrderDao orderDao, OrderItemDao orderItemDao, ProductDao productDao, CartService cartService) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
        this.cartService = cartService;
    }

    /**
     * カートの内容から注文を作成する
     *
     * @param sessionId セッションID
     * @param userId ユーザーID (ゲスト注文の場合はnull)
     * @param shippingAddress 配送先住所
     * @param paymentMethod 支払い方法
     * @return 作成された注文情報
     * @throws IllegalArgumentException カートが空の場合
     * @throws IllegalStateException 在庫不足などで注文できない場合
     */
    public Order createOrderFromCart(String sessionId, Long userId, String shippingAddress, String paymentMethod) {
        List<CartItem> cartItems = cartService.getCartItems(sessionId);
        
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }
        
        // 注文オブジェクトの作成
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        
        // 注文合計金額の計算
        AtomicInteger totalAmount = new AtomicInteger(0);
        List<OrderItem> orderItems = new ArrayList<>();
        
        // 在庫確認と注文アイテムの作成
        for (CartItem cartItem : cartItems) {
            Product product = productDao.selectById(cartItem.getProductId());
            if (product == null) {
                throw new NoSuchElementException("Product not found with id: " + cartItem.getProductId());
            }
            
            // 在庫確認
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            
            // 注文アイテムの作成
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice() * cartItem.getQuantity());
            
            orderItems.add(orderItem);
            totalAmount.addAndGet(orderItem.getSubtotal());
            
            // 在庫の更新
            product.setStock(product.getStock() - cartItem.getQuantity());
            productDao.update(product);
        }
        
        order.setTotalAmount(totalAmount.get());
        
        // 注文の保存
        int result = orderDao.insert(order);
        if (result == 0) {
            throw new RuntimeException("Failed to create order");
        }
        
        // 注文アイテムの保存
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            result = orderItemDao.insert(item);
            if (result == 0) {
                throw new RuntimeException("Failed to create order item");
            }
        }
        
        // カートの中身を空にする
        cartService.clearCart(sessionId);
        
        return order;
    }
    
    /**
     * 注文履歴を取得する
     *
     * @param userId ユーザーID
     * @return 注文リスト
     */
    public List<Order> getOrderHistory(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        return orderDao.findByUserId(userId);
    }
    
    /**
     * 注文詳細を取得する
     *
     * @param orderId 注文ID
     * @return 注文詳細
     * @throws NoSuchElementException 指定された注文が存在しない場合
     */
    public Order getOrderById(Long orderId) {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            throw new NoSuchElementException("Order not found with id: " + orderId);
        }
        
        // 注文アイテムの取得
        List<OrderItem> items = orderItemDao.findByOrderId(orderId);
        order.setItems(items);
        
        return order;
    }
    
    /**
     * 注文をキャンセルする
     *
     * @param orderId 注文ID
     * @throws NoSuchElementException 指定された注文が存在しない場合
     * @throws IllegalStateException 注文がキャンセル不可能な状態の場合
     */
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        // キャンセル可能な状態かチェック
        if (!"PENDING".equals(order.getStatus()) && !"PROCESSING".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }
        
        // 注文ステータスを更新
        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        
        int result = orderDao.update(order);
        if (result == 0) {
            throw new RuntimeException("Failed to cancel order");
        }
        
        // 在庫を戻す
        for (OrderItem item : order.getItems()) {
            Product product = productDao.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productDao.update(product);
            }
        }
    }
} 