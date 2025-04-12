package com.example.ecbackend.controller;

import com.example.ecbackend.entity.CartItem;
import com.example.ecbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("X-Session-ID") String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<CartItem> cartItems = cartService.getCartItems(sessionId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody CartItem cartItem) {
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        CartItem addedItem = cartService.addToCart(sessionId, cartItem.getProductId(), cartItem.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCartItem(
            @RequestHeader("X-Session-ID") String sessionId,
            @PathVariable Long id,
            @RequestBody CartItem cartItem) {
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            cartService.updateCartItem(sessionId, id, cartItem.getQuantity());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-Session-ID") String sessionId,
            @PathVariable Long id) {
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            cartService.removeFromCart(sessionId, id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
} 