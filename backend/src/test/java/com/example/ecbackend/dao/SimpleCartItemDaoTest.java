package com.example.ecbackend.dao;

import com.example.ecbackend.entity.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SimpleCartItemDaoTest {

    @Autowired
    private CartItemDao cartItemDao;

    @Test
    public void testFindByCartId() {
        // カートID 1に関連するアイテムを取得（data.sqlで初期データとして入っているはず）
        var cartItems = cartItemDao.findByCartId(1L);
        
        // テストデータが2件あるはず
        assertNotNull(cartItems);
        assertEquals(2, cartItems.size());
    }
} 