package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Order;

import java.util.List;

/**
 * 注文データアクセスインターフェース
 */
public interface OrderDao {
    
    /**
     * 注文を登録する
     *
     * @param order 注文エンティティ
     * @return 登録件数
     */
    int insert(Order order);
    
    /**
     * 注文を更新する
     *
     * @param order 注文エンティティ
     * @return 更新件数
     */
    int update(Order order);
    
    /**
     * 注文IDで検索する
     *
     * @param id 注文ID
     * @return 注文エンティティ
     */
    Order findById(Long id);
    
    /**
     * ユーザーIDで注文履歴を検索する
     *
     * @param userId ユーザーID
     * @return 注文エンティティのリスト
     */
    List<Order> findByUserId(Long userId);
} 