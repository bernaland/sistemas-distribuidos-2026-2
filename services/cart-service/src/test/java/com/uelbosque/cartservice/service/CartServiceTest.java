package com.uelbosque.cartservice.service;

import com.uelbosque.cartservice.dto.AddItemRequest;
import com.uelbosque.cartservice.dto.ApplyDiscountRequest;
import com.uelbosque.cartservice.dto.CartResponse;
import com.uelbosque.cartservice.dto.UpdateQuantityRequest;
import com.uelbosque.cartservice.exception.ResourceNotFoundException;
import com.uelbosque.cartservice.model.Cart;
import com.uelbosque.cartservice.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock com.uelbosque.cartservice.CatalogReader catalog;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart sampleCart;

    @BeforeEach
    void setUp() {
        sampleCart = new Cart("user123");
        sampleCart.setId(1L);
    }

    @Test
    @DisplayName("Debe retornar carrito existente")
    void shouldGetExistingCart() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));

        CartResponse res = cartService.getOrCreateCart("user123");

        assertNotNull(res);
        assertEquals("user123", res.getUserId());
    }

    @Test
    @DisplayName("Debe crear nuevo carrito si no existe")
    void shouldCreateNewCartWhenNotFound() {
        when(cartRepository.findByUserId("newUser")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.getOrCreateCart("newUser");

        assertNotNull(res);
        assertEquals("newUser", res.getUserId());
    }

    @Test
    @DisplayName("Debe agregar item al carrito y calcular subtotal e IVA correctamente")
    void shouldAddItemAndCalculateTotals() {
        AddItemRequest req = new AddItemRequest("P001", "Precio manipulado", new BigDecimal("1.00"), 2);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        when(catalog.get("P001")).thenReturn(new com.uelbosque.cartservice.CatalogReader.Product("P001","Arroz 1kg",new BigDecimal("1000.00"),new BigDecimal("19"),true));
        CartResponse res = cartService.addItem("user123", req);

        assertNotNull(res);
        assertEquals(1, res.getItems().size());
        assertEquals(new BigDecimal("2000.00"), res.getSubtotal());
        assertEquals(new BigDecimal("380.00"), res.getTaxTotal()); // 19% de 2000
        assertEquals(new BigDecimal("2380.00"), res.getGrandTotal());
    }

    @Test
    @DisplayName("Debe acumular cantidad si el producto ya estÃ¡ en el carrito")
    void shouldAccumulateQuantityForExistingProduct() {
        sampleCart.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 1);
        AddItemRequest req = new AddItemRequest("P001", "Arroz 1kg", new BigDecimal("1000.00"), 3);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        when(catalog.get("P001")).thenReturn(new com.uelbosque.cartservice.CatalogReader.Product("P001","Arroz 1kg",new BigDecimal("1000.00"),new BigDecimal("19"),true));
        CartResponse res = cartService.addItem("user123", req);

        assertEquals(1, res.getItems().size());
        assertEquals(4, res.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("4000.00"), res.getSubtotal());
    }

    @Test
    @DisplayName("Debe actualizar la cantidad de un item")
    void shouldUpdateQuantitySuccessfully() {
        sampleCart.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
        UpdateQuantityRequest req = new UpdateQuantityRequest(5);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.updateItemQuantity("user123", "P001", req);

        assertEquals(5, res.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("5000.00"), res.getSubtotal());
    }

    @Test
    @DisplayName("Debe remover el item si la cantidad se actualiza a 0")
    void shouldRemoveItemWhenQuantityIsZero() {
        sampleCart.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
        UpdateQuantityRequest req = new UpdateQuantityRequest(0);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.updateItemQuantity("user123", "P001", req);

        assertTrue(res.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO.setScale(2), res.getSubtotal());
    }

    @Test
    @DisplayName("Debe lanzar excepciÃ³n si se intenta actualizar un item que no existe")
    void shouldThrowWhenUpdatingNonExistentItem() {
        UpdateQuantityRequest req = new UpdateQuantityRequest(3);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));

        assertThrows(ResourceNotFoundException.class, () ->
                cartService.updateItemQuantity("user123", "P999", req));
    }

    @Test
    @DisplayName("Debe remover un item del carrito")
    void shouldRemoveItemSuccessfully() {
        sampleCart.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.removeItem("user123", "P001");

        assertTrue(res.getItems().isEmpty());
    }

    @Test
    @DisplayName("Debe vaciar todo el carrito")
    void shouldClearCartSuccessfully() {
        sampleCart.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
        sampleCart.addItem("P002", "Leche 1L", new BigDecimal("3000.00"), 1);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.clearCart("user123");

        assertTrue(res.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO.setScale(2), res.getGrandTotal());
    }

    @Test
    @DisplayName("Debe aplicar descuento y recalcular gran total")
    void shouldApplyDiscountCorrectly() {
        sampleCart.addItem("P001", "Arroz 1kg", new BigDecimal("10000.00"), 1); // Subtotal 10000, IVA 1900, Total 11900
        ApplyDiscountRequest req = new ApplyDiscountRequest(new BigDecimal("900.00"));
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.applyDiscount("user123", req);

        assertEquals(new BigDecimal("900.00"), res.getDiscountTotal());
        assertEquals(new BigDecimal("11000.00"), res.getGrandTotal()); // 11900 - 900
    }
}
