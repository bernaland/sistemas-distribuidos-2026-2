package com.uelbosque.userservice.config;

import com.uelbosque.userservice.model.User;
import com.uelbosque.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String initialPassword;

    public DataInitializer(UserRepository users, PasswordEncoder encoder,
                           @Value("${ADMIN_INITIAL_PASSWORD:#{null}}") String initialPassword) {
        if (initialPassword == null || initialPassword.isBlank()) {
            throw new IllegalStateException("ADMIN_INITIAL_PASSWORD no definida");
        }
        this.users = users;
        this.encoder = encoder;
        this.initialPassword = initialPassword;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (users.count() != 0) return;
        User admin = new User("admininicial", encoder.encode(initialPassword),
                "Administrador inicial", "admin@uelbosque.edu.co", "ROLE_ADMIN,ROLE_USER");
        admin.setCedula("0");
        users.save(admin);
    }
}
