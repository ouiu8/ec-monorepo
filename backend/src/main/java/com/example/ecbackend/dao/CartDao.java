package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Cart;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.Optional;

@Dao
@ConfigAutowireable
public interface CartDao {
    @Select
    Optional<Cart> findBySessionId(String sessionId);

    @Select
    Optional<Cart> findById(Long id);

    @Insert
    int insert(Cart cart);
} 