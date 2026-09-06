package com.uelbosque.cartservice.repository;

import com.uelbosque.cartservice.model.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        Cart cart = new Cart("user100");
        cart.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
        cartRepository.save(cart);
    }

    @Test
    @DisplayName("Debe buscar carrito por userId con items")
    void shouldFindByUserId() {
        Optional<Cart> found = cartRepository.findByUserId("user100");

        assertTrue(found.isPresent());
        assertEquals("user100", found.get().getUserId());
        assertEquals(1, found.get().getItems().size());
        assertEquals("P001", found.get().getItems().get(0).getProductCode());
    }

    @Test
    @DisplayName("Debe verificar existencia de carrito por userId")
    void shouldCheckExistsByUserId() {
        assertTrue(cartRepository.existsByUserId("user100"));
        assertFalse(cartRepository.existsByUserId("nonexistent"));
    }

    @Test
    @DisplayName("Debe eliminar carrito por userId")
    void shouldDeleteByUserId() {
        cartRepository.deleteByUserId("user100");

        assertFalse(cartRepository.existsByUserId("user100"));
    }
}
