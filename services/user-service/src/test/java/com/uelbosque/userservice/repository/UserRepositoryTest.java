package com.uelbosque.userservice.repository;

import com.uelbosque.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User("admin", "pass123", "Admin User", "admin@uelbosque.edu.co", "ROLE_ADMIN");
        userRepository.save(user);
    }

    @Test
    @DisplayName("Debe buscar usuario por username")
    void shouldFindByUsername() {
        Optional<User> found = userRepository.findByUsername("admin");
        assertTrue(found.isPresent());
        assertEquals("Admin User", found.get().getName());
    }

    @Test
    @DisplayName("Debe buscar usuario por email")
    void shouldFindByEmail() {
        Optional<User> found = userRepository.findByEmail("admin@uelbosque.edu.co");
        assertTrue(found.isPresent());
        assertEquals("admin", found.get().getUsername());
    }

    @Test
    @DisplayName("Debe verificar existencia por username")
    void shouldCheckExistsByUsername() {
        assertTrue(userRepository.existsByUsername("admin"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Debe verificar existencia por email")
    void shouldCheckExistsByEmail() {
        assertTrue(userRepository.existsByEmail("admin@uelbosque.edu.co"));
        assertFalse(userRepository.existsByEmail("other@uelbosque.edu.co"));
    }
}
