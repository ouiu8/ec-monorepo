package com.example.ecbackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class H2DatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testH2Connection() {
        // 商品テーブルの件数を確認する単純なテスト
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
        assertEquals(3, count); // data.sqlに3件登録されていることを確認
    }
} 