package com.example.ecbackend.dao;

import com.example.ecbackend.entity.Product;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.springframework.stereotype.Repository;
import org.seasar.doma.boot.ConfigAutowireable;
import java.util.List;

@Dao
@ConfigAutowireable
@Repository
public interface ProductDao {
    @Select
    List<Product> selectAll();

    @Select
    Product selectById(Long id);
    
    @Insert
    int insert(Product product);
    
    @Update
    int update(Product product);
}