package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Order;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.List;

/**
 * 注文データアクセスインターフェース
 */
@Dao
@ConfigAutowireable
public interface OrderDao {
    
    /**
     * 注文を登録する
     *
     * @param order 注文エンティティ
     * @return 登録件数
     */
    @Insert
    int insert(Order order);
    
    /**
     * 注文を更新する
     *
     * @param order 注文エンティティ
     * @return 更新件数
     */
    @Update
    int update(Order order);
    
    /**
     * 注文IDで検索する
     *
     * @param id 注文ID
     * @return 注文エンティティ
     */
    @Select
    Order findById(Long id);
    
    /**
     * ユーザーIDで注文履歴を検索する
     *
     * @param userId ユーザーID
     * @return 注文エンティティのリスト
     */
    @Select
    List<Order> findByUserId(Long userId);
} 