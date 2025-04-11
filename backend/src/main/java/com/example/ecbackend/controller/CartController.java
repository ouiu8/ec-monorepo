package com.example.ecbackend.controller;

import com.example.ecbackend.entity.CartItem;
import com.example.ecbackend.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestHeader("X-Session-ID") String sessionId) {
        return ResponseEntity.ok(cartService.getCartItems(sessionId));
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody CartItem cartItem) {
        CartItem savedItem = cartService.addToCart(sessionId, cartItem.getProductId(), cartItem.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<Void> updateCartItem(
            @RequestHeader("X-Session-ID") String sessionId,
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem) {
        cartService.updateCartItem(sessionId, cartItemId, cartItem.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-Session-ID") String sessionId,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(sessionId, cartItemId);
        return ResponseEntity.noContent().build();
    }
} 