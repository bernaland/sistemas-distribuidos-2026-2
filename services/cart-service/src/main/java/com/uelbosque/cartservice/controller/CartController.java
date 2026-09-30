package com.uelbosque.cartservice.controller;

import com.uelbosque.cartservice.dto.AddItemRequest;
import com.uelbosque.cartservice.dto.ApplyDiscountRequest;
import com.uelbosque.cartservice.dto.CartResponse;
import com.uelbosque.cartservice.dto.UpdateQuantityRequest;
import com.uelbosque.cartservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable String userId,
            @Valid @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/{userId}/items/{productCode}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable String userId,
            @PathVariable String productCode,
            @Valid @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, productCode, request));
    }

    @DeleteMapping("/{userId}/items/{productCode}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable String userId,
            @PathVariable String productCode) {
        return ResponseEntity.ok(cartService.removeItem(userId, productCode));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CartResponse> clearCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @PostMapping("/{userId}/discount")
    public ResponseEntity<CartResponse> applyDiscount(
            @PathVariable String userId,
            @Valid @RequestBody ApplyDiscountRequest request) {
        return ResponseEntity.ok(cartService.applyDiscount(userId, request));
    }
}
