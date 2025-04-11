package com.example.ecbackend.service;

import com.example.ecbackend.dao.CartDao;
import com.example.ecbackend.dao.CartItemDao;
import com.example.ecbackend.entity.Cart;
import com.example.ecbackend.entity.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;

    public CartService(CartDao cartDao, CartItemDao cartItemDao) {
        this.cartDao = cartDao;
        this.cartItemDao = cartItemDao;
    }

    public List<CartItem> getCartItems(String sessionId) {
        Optional<Cart> cart = cartDao.findBySessionId(sessionId);
        if (cart.isPresent()) {
            return cartItemDao.findByCartId(cart.get().getId());
        }
        return List.of();
    }

    public CartItem addToCart(String sessionId, Long productId, int quantity) {
        Cart cart = cartDao.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    cartDao.insert(newCart);
                    return newCart;
                });

        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItemDao.insert(cartItem);
        return cartItem;
    }

    public void updateCartItem(String sessionId, Long itemId, int quantity) {
        Cart cart = cartDao.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem cartItem = cartItemDao.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to this cart");
        }

        cartItem.setQuantity(quantity);
        cartItemDao.update(cartItem);
    }

    public void removeFromCart(String sessionId, Long itemId) {
        Cart cart = cartDao.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem cartItem = cartItemDao.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to this cart");
        }

        cartItemDao.delete(cartItem);
    }

    /**
     * カートを空にする
     *
     * @param sessionId セッションID
     */
    public void clearCart(String sessionId) {
        Optional<Cart> cartOpt = cartDao.findBySessionId(sessionId);
        
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            List<CartItem> cartItems = cartItemDao.findByCartId(cart.getId());
            
            for (CartItem item : cartItems) {
                cartItemDao.delete(item);
            }
        }
    }
} 