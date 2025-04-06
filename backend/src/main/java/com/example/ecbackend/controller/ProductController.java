package com.example.ecbackend.controller;

import com.example.ecbackend.entity.Product;
import com.example.ecbackend.dao.ProductDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ProductController {

    private final ProductDao productDao;

    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @GetMapping("/api/products")
    public List<Product> getProducts() {
        return productDao.selectAll();
    }

    @GetMapping("/api/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productDao.selectById(id);
    }
}