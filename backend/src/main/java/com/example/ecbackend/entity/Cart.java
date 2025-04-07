package com.example.ecbackend.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Column;
import org.seasar.doma.Table;
import java.time.LocalDateTime;

@Entity(immutable = true)
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(name = "session_id")
    private final String sessionId;

    @Column(name = "created_at")
    private final LocalDateTime createdAt;

    @Column(name = "updated_at")
    private final LocalDateTime updatedAt;

    public Cart(Long id, String sessionId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 