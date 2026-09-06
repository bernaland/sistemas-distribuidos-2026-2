package com.uelbosque.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uelbosque.userservice.dto.AuthResponse;
import com.uelbosque.userservice.dto.CreateUserRequest;
import com.uelbosque.userservice.dto.LoginRequest;
import com.uelbosque.userservice.dto.UpdateUserRequest;
import com.uelbosque.userservice.dto.UserResponse;
import com.uelbosque.userservice.exception.GlobalExceptionHandler;
import com.uelbosque.userservice.exception.ResourceNotFoundException;
import com.uelbosque.userservice.security.JwtAuthenticationFilter;
import com.uelbosque.userservice.security.JwtTokenProvider;
import com.uelbosque.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/users debe registrar un usuario y retornar 201")
    void shouldRegisterUserSuccessfully() throws Exception {
        CreateUserRequest req = new CreateUserRequest("jdoe", "secret123", "John Doe", "jdoe@example.com", "ROLE_USER");
        UserResponse res = new UserResponse(1L, "jdoe", "John Doe", "jdoe@example.com", "ROLE_USER", true);
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    @DisplayName("POST /api/users con campos vacíos debe retornar 400 Bad Request")
    void shouldReturnBadRequestWhenInvalid() throws Exception {
        CreateUserRequest req = new CreateUserRequest("", "", "", "invalid-email", "");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("POST /api/users/login debe autenticar y retornar 200 con token")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest req = new LoginRequest("jdoe", "secret123");
        UserResponse user = new UserResponse(1L, "jdoe", "John Doe", "jdoe@example.com", "ROLE_USER", true);
        AuthResponse auth = new AuthResponse("mock.jwt.token", user);
        when(userService.authenticate(any(LoginRequest.class))).thenReturn(auth);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.user.username").value("jdoe"));
    }

    @Test
    @DisplayName("GET /api/users debe retornar 200 y lista de usuarios")
    void shouldListUsers() throws Exception {
        UserResponse user = new UserResponse(1L, "jdoe", "John Doe", "jdoe@example.com", "ROLE_USER", true);
        when(userService.listUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jdoe"));
    }

    @Test
    @DisplayName("GET /api/users/{id} debe retornar 200 cuando existe")
    void shouldGetUserById() throws Exception {
        UserResponse user = new UserResponse(1L, "jdoe", "John Doe", "jdoe@example.com", "ROLE_USER", true);
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    @DisplayName("GET /api/users/{id} debe retornar 404 cuando no existe")
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("Usuario no encontrado con ID: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado con ID: 99"));
    }

    @Test
    @DisplayName("GET /api/users/username/{username} debe retornar 200")
    void shouldGetUserByUsername() throws Exception {
        UserResponse user = new UserResponse(1L, "jdoe", "John Doe", "jdoe@example.com", "ROLE_USER", true);
        when(userService.getUserByUsername("jdoe")).thenReturn(user);

        mockMvc.perform(get("/api/users/username/jdoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} debe actualizar y retornar 200")
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest req = new UpdateUserRequest("John New", "new@example.com", null, null, null);
        UserResponse res = new UserResponse(1L, "jdoe", "John New", "new@example.com", "ROLE_USER", true);
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(res);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John New"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} debe retornar 204 No Content")
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
