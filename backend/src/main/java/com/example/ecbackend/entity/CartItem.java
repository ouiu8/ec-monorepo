package com.example.ecbackend.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Column;
import org.seasar.doma.Table;
import java.time.LocalDateTime;

@Entity(immutable = true)
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(name = "cart_id")
    private final Long cartId;

    @Column(name = "product_id")
    private final Long productId;

    @Column(name = "quantity")
    private final Integer quantity;

    @Column(name = "created_at")
    private final LocalDateTime createdAt;

    @Column(name = "updated_at")
    private final LocalDateTime updatedAt;

    public CartItem(Long id, Long cartId, Long productId, Integer quantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCartId() {
        return cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 