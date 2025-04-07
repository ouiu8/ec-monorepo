package com.example.ecbackend.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.Delete;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Result;
import com.example.ecbackend.entity.CartItem;
import java.util.List;
import java.util.Optional;

@Dao
@ConfigAutowireable
public interface CartItemDao {
    @Select
    List<CartItem> findByCartId(Long cartId);

    @Select
    Optional<CartItem> findById(Long id);

    @Select
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Insert
    Result<CartItem> insert(CartItem cartItem);

    @Update
    Result<CartItem> update(CartItem cartItem);

    @Delete
    Result<CartItem> delete(CartItem cartItem);
} 