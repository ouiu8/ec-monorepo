package com.example.ecbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ecbackend.dao.CartDao;
import com.example.ecbackend.dao.CartItemDao;
import com.example.ecbackend.dao.ProductDao;
import com.example.ecbackend.entity.Cart;
import com.example.ecbackend.entity.CartItem;
import com.example.ecbackend.entity.Product;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final ProductDao productDao;

    public CartService(CartDao cartDao, CartItemDao cartItemDao, ProductDao productDao) {
        this.cartDao = cartDao;
        this.cartItemDao = cartItemDao;
        this.productDao = productDao;
    }

    @Transactional
    public Cart createCart(String sessionId) {
        LocalDateTime now = LocalDateTime.now();
        Cart cart = new Cart(null, sessionId, now, now);
        cartDao.insert(cart);
        return cart;
    }

    @Transactional
    public CartItem addToCart(String sessionId, Long productId, Integer quantity) {
        Cart cart = cartDao.findBySessionId(sessionId)
            .orElseGet(() -> createCart(sessionId));

        Optional<CartItem> existingItem = cartItemDao.findByCartIdAndProductId(cart.getId(), productId);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            CartItem updatedItem = new CartItem(
                item.getId(),
                item.getCartId(),
                item.getProductId(),
                item.getQuantity() + quantity,
                item.getCreatedAt(),
                LocalDateTime.now()
            );
            cartItemDao.update(updatedItem);
            return updatedItem;
        } else {
            LocalDateTime now = LocalDateTime.now();
            CartItem newItem = new CartItem(null, cart.getId(), productId, quantity, now, now);
            cartItemDao.insert(newItem);
            return newItem;
        }
    }

    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(String sessionId) {
        return cartDao.findBySessionId(sessionId)
            .map(cart -> cartItemDao.findByCartId(cart.getId()))
            .orElse(List.of());
    }

    @Transactional
    public void updateCartItemQuantity(String sessionId, Long productId, Integer quantity) {
        cartDao.findBySessionId(sessionId).ifPresent(cart -> {
            cartItemDao.findByCartIdAndProductId(cart.getId(), productId).ifPresent(item -> {
                CartItem updatedItem = new CartItem(
                    item.getId(),
                    item.getCartId(),
                    item.getProductId(),
                    quantity,
                    item.getCreatedAt(),
                    LocalDateTime.now()
                );
                cartItemDao.update(updatedItem);
            });
        });
    }

    @Transactional
    public void removeFromCart(String sessionId, Long productId) {
        cartDao.findBySessionId(sessionId).ifPresent(cart -> {
            cartItemDao.findByCartIdAndProductId(cart.getId(), productId).ifPresent(cartItemDao::delete);
        });
    }
} 