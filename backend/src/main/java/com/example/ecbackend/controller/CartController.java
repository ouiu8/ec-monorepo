package com.example.ecbackend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.ecbackend.service.CartService;
import com.example.ecbackend.entity.CartItem;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(HttpSession session) {
        List<CartItem> items = cartService.getCartItems(session.getId());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/items")
    public ResponseEntity<CartItem> addToCart(
            HttpSession session,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        CartItem item = cartService.addToCart(session.getId(), productId, quantity);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            HttpSession session,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        cartService.updateCartItemQuantity(session.getId(), productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            HttpSession session,
            @PathVariable Long productId) {
        cartService.removeFromCart(session.getId(), productId);
        return ResponseEntity.ok().build();
    }
} 