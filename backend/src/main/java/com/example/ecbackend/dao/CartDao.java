package com.example.ecbackend.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.Delete;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Result;
import com.example.ecbackend.entity.Cart;
import java.util.Optional;

@Dao
@ConfigAutowireable
public interface CartDao {
    @Select
    Optional<Cart> findById(Long id);

    @Select
    Optional<Cart> findBySessionId(String sessionId);

    @Insert
    Result<Cart> insert(Cart cart);

    @Update
    Result<Cart> update(Cart cart);

    @Delete
    Result<Cart> delete(Cart cart);
} 