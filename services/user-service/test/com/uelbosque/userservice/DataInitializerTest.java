package com.uelbosque.userservice;

import com.uelbosque.userservice.config.DataInitializer;
import com.uelbosque.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DataInitializerTest {

    @Test
    void failsWhenPasswordIsNull() {
        var users = Mockito.mock(UserRepository.class);
        var encoder = Mockito.mock(PasswordEncoder.class);
        assertThrows(IllegalStateException.class, () -> new DataInitializer(users, encoder, null));
    }

    @Test
    void failsWhenPasswordIsBlank() {
        var users = Mockito.mock(UserRepository.class);
        var encoder = Mockito.mock(PasswordEncoder.class);
        assertThrows(IllegalStateException.class, () -> new DataInitializer(users, encoder, "   "));
    }

    @Test
    void succeedsWhenPasswordIsProvided() {
        var users = Mockito.mock(UserRepository.class);
        var encoder = Mockito.mock(PasswordEncoder.class);
        assertDoesNotThrow(() -> new DataInitializer(users, encoder, "SecurePassword123!"));
    }
}
