package com.example.ecbackend.dao;

import com.example.ecbackend.entity.OrderItem;

import java.util.List;

/**
 * 注文明細データアクセスインターフェース
 */
public interface OrderItemDao {
    
    /**
     * 注文明細を登録する
     *
     * @param orderItem 注文明細エンティティ
     * @return 登録件数
     */
    int insert(OrderItem orderItem);
    
    /**
     * 注文明細を更新する
     *
     * @param orderItem 注文明細エンティティ
     * @return 更新件数
     */
    int update(OrderItem orderItem);
    
    /**
     * 注文IDで注文明細を検索する
     *
     * @param orderId 注文ID
     * @return 注文明細エンティティのリスト
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * 注文明細IDで検索する
     *
     * @param id 注文明細ID
     * @return 注文明細エンティティ
     */
    OrderItem findById(Long id);
} 