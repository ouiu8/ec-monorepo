package com.example.ecbackend.controller;

import com.example.ecbackend.dao.ProductDao;
import com.example.ecbackend.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductControllerのテスト
 * 
 * 設計原則:
 * 1. 各エンドポイントの正常系と異常系をカバー
 * 2. モックを使用して外部依存関係を分離
 * 3. BDDスタイル（given-when-then）でテスト構造を明確に
 * 4. 詳細な検証でレスポンスの構造と内容を確認
 */
@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@DisplayName("ProductController: 商品APIのテスト")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductDao productDao;

    // テスト用データの準備
    private Product createTestProduct(Long id, String name, String description, int price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return product;
    }

    @Nested
    @DisplayName("/api/products: 全商品取得API")
    class GetProductsTests {

        @Test
        @DisplayName("商品一覧を取得できる")
        void shouldReturnAllProducts() throws Exception {
            // Given: 複数の商品データが存在する場合
            List<Product> products = Arrays.asList(
                createTestProduct(1L, "テスト商品1", "商品1の説明", 1000),
                createTestProduct(2L, "テスト商品2", "商品2の説明", 2000),
                createTestProduct(3L, "テスト商品3", "商品3の説明", 3000)
            );
            given(productDao.selectAll()).willReturn(products);

            // When: 商品一覧APIを呼び出す
            ResultActions response = mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

            // Then: 正常なレスポンスと商品一覧が返される
            response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("テスト商品1")))
                .andExpect(jsonPath("$[0].description", is("商品1の説明")))
                .andExpect(jsonPath("$[0].price", is(1000)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[2].id", is(3)));

            // 正確に1回だけDAOが呼び出されることを検証
            verify(productDao, times(1)).selectAll();
        }

        @Test
        @DisplayName("商品がない場合は空配列を返す")
        void shouldReturnEmptyArrayWhenNoProducts() throws Exception {
            // Given: 商品が存在しない場合
            given(productDao.selectAll()).willReturn(Collections.emptyList());

            // When: 商品一覧APIを呼び出す
            ResultActions response = mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

            // Then: 正常なレスポンスと空配列が返される
            response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", is(empty())));

            verify(productDao, times(1)).selectAll();
        }
    }

    @Nested
    @DisplayName("/api/products/{id}: 商品詳細取得API")
    class GetProductByIdTests {

        @Test
        @DisplayName("存在するIDで商品詳細を取得できる")
        void shouldReturnProductWhenExists() throws Exception {
            // Given: 指定IDの商品が存在する場合
            Long productId = 1L;
            Product product = createTestProduct(productId, "テスト商品1", "詳細な商品説明", 1500);
            given(productDao.selectById(productId)).willReturn(product);

            // When: 商品詳細APIを呼び出す
            ResultActions response = mockMvc.perform(get("/api/products/{id}", productId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

            // Then: 正常なレスポンスと商品詳細が返される
            response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("テスト商品1")))
                .andExpect(jsonPath("$.description", is("詳細な商品説明")))
                .andExpect(jsonPath("$.price", is(1500)));

            verify(productDao, times(1)).selectById(productId);
        }

        @Test
        @DisplayName("存在しないIDの場合はnullが返される")
        void shouldReturnNullWhenProductDoesNotExist() throws Exception {
            // Given: 指定IDの商品が存在しない場合
            Long nonExistentId = 999L;
            given(productDao.selectById(nonExistentId)).willReturn(null);

            // When: 商品詳細APIを呼び出す
            ResultActions response = mockMvc.perform(get("/api/products/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

            // Then: 正常なステータスとnullが返される
            response
                .andExpect(status().isOk())
                .andExpect(content().string("null"));

            verify(productDao, times(1)).selectById(nonExistentId);
        }
    }
} 