package com.uelbosque.cartservice.service;

import com.uelbosque.cartservice.dto.AddItemRequest;
import com.uelbosque.cartservice.dto.ApplyDiscountRequest;
import com.uelbosque.cartservice.dto.CartItemResponse;
import com.uelbosque.cartservice.dto.CartResponse;
import com.uelbosque.cartservice.dto.UpdateQuantityRequest;
import com.uelbosque.cartservice.exception.ResourceNotFoundException;
import com.uelbosque.cartservice.model.Cart;
import com.uelbosque.cartservice.model.CartItem;
import com.uelbosque.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final com.uelbosque.cartservice.CatalogReader catalog;

    public CartService(CartRepository cartRepository, com.uelbosque.cartservice.CatalogReader catalog) {
        this.catalog=catalog;
        this.cartRepository = cartRepository;
    }

    public CartResponse getOrCreateCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));
        return toResponse(cart);
    }

    public CartResponse addItem(String userId, AddItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> new Cart(userId));
        var product=catalog.get(request.getProductCode());
        cart.addItem(product.code(), product.name(), product.salePrice(), request.getQuantity(), product.ivaRate());
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse updateItemQuantity(String userId, String productCode, UpdateQuantityRequest request) {
        Cart cart = findCartOrThrow(userId);
        if (cart.findItem(productCode).isEmpty()) {
            throw new ResourceNotFoundException("El producto " + productCode + " no estÃƒÆ’Ã‚Â¡ en el carrito");
        }
        cart.updateQuantity(productCode, request.getQuantity());
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse removeItem(String userId, String productCode) {
        Cart cart = findCartOrThrow(userId);
        cart.removeItem(productCode);
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse clearCart(String userId) {
        Cart cart = findCartOrThrow(userId);
        cart.clear();
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse applyDiscount(String userId, ApplyDiscountRequest request) {
        Cart cart = findCartOrThrow(userId);
        cart.applyDiscount(request.getDiscountAmount());
        return toResponse(cartRepository.save(cart));
    }

    private Cart findCartOrThrow(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado para usuario: " + userId));
    }

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();
        return new CartResponse(cart.getId(), cart.getUserId(), items,
                cart.getSubtotal(), cart.getTaxRate(), cart.getTaxTotal(),
                cart.getDiscountTotal(), cart.getGrandTotal(), cart.getUpdatedAt());
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(item.getId(), item.getProductCode(),
                item.getProductName(), item.getUnitPrice(),
                item.getQuantity(), item.getItemSubtotal());
    }
}
