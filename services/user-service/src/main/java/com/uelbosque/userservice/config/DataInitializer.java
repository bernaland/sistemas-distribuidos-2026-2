package com.uelbosque.userservice.config;

import com.uelbosque.userservice.model.User;
import com.uelbosque.userservice.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        seedUser("admininicial", "admin123456", "Administrador Principal", "admin@uelbosque.edu.co", "ROLE_ADMIN,ROLE_USER");
        seedUser("userinicial", "user123456", "Usuario Inicial", "user@uelbosque.edu.co", "ROLE_USER");
    }

    private void seedUser(String username, String rawPassword, String name, String email, String roles) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, passwordEncoder.encode(rawPassword), name, email, roles);
            userRepository.save(user);
        }
    }
}
