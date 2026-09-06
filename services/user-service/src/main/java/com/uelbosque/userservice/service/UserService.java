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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("El nombre de usuario ya está en uso");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El correo electrónico ya está registrado");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), encodedPassword, request.getName(), request.getEmail(), request.getRoles());
        return toResponse(userRepository.save(user));
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Usuario desactivado");
        }
        String token = tokenProvider.generateToken(user.getUsername(), user.getRoles(), user.getName(), user.getEmail());
        return new AuthResponse(token, toResponse(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        validateEmailUpdate(user, request.getEmail());
        applyUserUpdates(user, request);
        return toResponse(userRepository.save(user));
    }

    private void validateEmailUpdate(User user, String newEmail) {
        if (StringUtils.hasText(newEmail) && !newEmail.equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new DuplicateResourceException("El correo electrónico ya está registrado");
            }
        }
    }

    private void applyUserUpdates(User user, UpdateUserRequest request) {
        if (StringUtils.hasText(request.getName())) user.setName(request.getName());
        if (StringUtils.hasText(request.getEmail())) user.setEmail(request.getEmail());
        if (StringUtils.hasText(request.getPassword())) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (StringUtils.hasText(request.getRoles())) user.setRoles(request.getRoles());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        userRepository.delete(user);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getRoles(), user.isEnabled());
    }
}
