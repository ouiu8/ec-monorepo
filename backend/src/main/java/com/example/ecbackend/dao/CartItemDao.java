package com.example.ecbackend.dao;

import com.example.ecbackend.entity.CartItem;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.List;
import java.util.Optional;

@Dao
@ConfigAutowireable
public interface CartItemDao {
    @Select
    Optional<CartItem> findById(Long id);

    @Select
    List<CartItem> findByCartId(Long cartId);

    @Select
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Insert
    int insert(CartItem cartItem);

    @Update
    int update(CartItem cartItem);

    @Delete
    int delete(CartItem cartItem);
} 