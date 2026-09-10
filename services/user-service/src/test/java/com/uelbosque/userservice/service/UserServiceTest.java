package com.uelbosque.userservice.service;

import com.uelbosque.userservice.dto.AuthResponse;
import com.uelbosque.userservice.dto.CreateUserRequest;
import com.uelbosque.userservice.dto.LoginRequest;
import com.uelbosque.userservice.dto.UpdateUserRequest;
import com.uelbosque.userservice.dto.UserResponse;
import com.uelbosque.userservice.exception.DuplicateResourceException;
import com.uelbosque.userservice.exception.ResourceNotFoundException;
import com.uelbosque.userservice.model.User;
import com.uelbosque.userservice.repository.UserRepository;
import com.uelbosque.userservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("jdoe", "encodedPass", "John Doe", "jdoe@example.com", "ROLE_USER");
        sampleUser.setId(1L);
    }

    @Test
    @DisplayName("Debe registrar un usuario satisfactoriamente")
    void shouldCreateUserSuccessfully() {
        CreateUserRequest req = new CreateUserRequest("jdoe", "secret123", "John Doe", "jdoe@example.com", "ROLE_USER");
        req.setCedula("123456");
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("jdoe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse res = userService.createUser(req);

        assertNotNull(res);
        assertEquals("jdoe", res.getUsername());
        assertEquals("jdoe@example.com", res.getEmail());
    }

    @Test
    @DisplayName("Debe lanzar DuplicateResourceException si el username ya existe")
    void shouldThrowWhenUsernameExists() {
        CreateUserRequest req = new CreateUserRequest("jdoe", "secret123", "John Doe", "jdoe@example.com", "ROLE_USER");
        req.setCedula("123456");
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar DuplicateResourceException si el email ya existe")
    void shouldThrowWhenEmailExists() {
        CreateUserRequest req = new CreateUserRequest("jdoe", "secret123", "John Doe", "jdoe@example.com", "ROLE_USER");
        req.setCedula("123456");
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("jdoe@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe autenticar credenciales válidas y retornar token JWT")
    void shouldAuthenticateSuccessfully() {
        LoginRequest req = new LoginRequest("jdoe", "secret123");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("secret123", "encodedPass")).thenReturn(true);
        when(tokenProvider.generateToken("jdoe", "ROLE_USER", "John Doe", "jdoe@example.com")).thenReturn("dummy.jwt.token");

        AuthResponse auth = userService.authenticate(req);

        assertNotNull(auth);
        assertEquals("dummy.jwt.token", auth.getToken());
        assertEquals("jdoe", auth.getUser().getUsername());
    }

    @Test
    @DisplayName("Debe lanzar BadCredentialsException si la contraseña no coincide")
    void shouldThrowWhenPasswordMismatch() {
        LoginRequest req = new LoginRequest("jdoe", "wrongPass");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.authenticate(req));
    }

    @Test
    @DisplayName("Debe lanzar BadCredentialsException si el usuario está deshabilitado")
    void shouldThrowWhenUserDisabled() {
        sampleUser.setEnabled(false);
        LoginRequest req = new LoginRequest("jdoe", "secret123");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("secret123", "encodedPass")).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> userService.authenticate(req));
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID")
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponse res = userService.getUserById(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el ID no existe")
    void shouldThrowWhenUserNotFoundById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("Debe obtener un usuario por username")
    void shouldGetUserByUsernameSuccessfully() {
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(sampleUser));

        UserResponse res = userService.getUserByUsername("jdoe");

        assertNotNull(res);
        assertEquals("jdoe", res.getUsername());
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void shouldListUsersSuccessfully() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponse> list = userService.listUsers();

        assertEquals(1, list.size());
        assertEquals("jdoe", list.get(0).getUsername());
    }

    @Test
    @DisplayName("Debe actualizar los datos de un usuario")
    void shouldUpdateUserSuccessfully() {
        UpdateUserRequest req = new UpdateUserRequest("John Updated", "new@example.com", "newSecret", "ROLE_ADMIN", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newSecret")).thenReturn("newEncodedPass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse res = userService.updateUser(1L, req);

        assertNotNull(res);
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Debe eliminar un usuario por ID")
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(sampleUser);
    }
}
