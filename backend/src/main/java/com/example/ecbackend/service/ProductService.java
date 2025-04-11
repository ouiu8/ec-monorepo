package com.example.ecbackend.service;

import com.example.ecbackend.dao.ProductDao;
import com.example.ecbackend.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 商品に関するビジネスロジックを提供するサービス
 */
@Service
@Transactional
public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * すべての商品を取得する
     *
     * @return 商品のリスト
     */
    public List<Product> getAllProducts() {
        return productDao.selectAll();
    }

    /**
     * 指定されたIDの商品を取得する
     *
     * @param id 商品ID
     * @return 商品情報
     * @throws NoSuchElementException 指定されたIDの商品が存在しない場合
     */
    public Product getProductById(Long id) {
        Product product = productDao.selectById(id);
        if (product == null) {
            throw new NoSuchElementException("Product not found with id: " + id);
        }
        return product;
    }

    /**
     * 商品情報を更新する
     *
     * @param product 更新する商品情報
     * @return 更新された商品
     * @throws NoSuchElementException 指定されたIDの商品が存在しない場合
     * @throws IllegalArgumentException 商品情報が不正な場合
     */
    public Product updateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        // 現在の商品情報を取得
        Product existingProduct = getProductById(product.getId());
        
        // 商品情報を更新
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setUpdatedAt(LocalDateTime.now());
        
        // 更新を実行
        int result = productDao.update(existingProduct);
        if (result == 0) {
            throw new RuntimeException("Failed to update product with id: " + product.getId());
        }
        
        return existingProduct;
    }

    /**
     * 新しい商品を登録する
     *
     * @param product 登録する商品情報
     * @return 登録された商品
     * @throws IllegalArgumentException 商品情報が不正な場合
     */
    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException("Product price must be a non-negative value");
        }
        
        // 登録日時と更新日時を設定
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        
        // 商品を登録
        int result = productDao.insert(product);
        if (result == 0) {
            throw new RuntimeException("Failed to create product");
        }
        
        return product;
    }
} 