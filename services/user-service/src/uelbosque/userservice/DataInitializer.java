package uelbosque.userservice;

import uelbosque.userservice.model.User;
import uelbosque.userservice.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        String defaultUsername = "admininicial";
        repo.findByUsername(defaultUsername).orElseGet(() -> {
            User admin = new User(defaultUsername, encoder.encode("admin123456"), "ROLE_ADMIN");
            return repo.save(admin);
        });
    }
}
