package com.uelbosque.userservice.service;

import com.uelbosque.userservice.model.User;
import com.uelbosque.userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DatabaseUserDetailsService service;

    @Test
    @DisplayName("Debe cargar usuario por username y mapear roles a authorities")
    void shouldLoadUserSuccessfully() {
        User user = new User("admin", "pass123", "Admin User", "admin@uelbosque.edu.co", "ROLE_ADMIN,ROLE_USER");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin");

        assertNotNull(details);
        assertEquals("admin", details.getUsername());
        assertEquals(2, details.getAuthorities().size());
    }

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException si el usuario no existe")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown"));
    }
}
