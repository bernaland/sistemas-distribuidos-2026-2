package uelbosque.userservice.service;

import uelbosque.userservice.model.User;
import uelbosque.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String password, String roles) {
        if (repo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User(username, passwordEncoder.encode(password), roles);
        return repo.save(user);
    }

    public Optional<User> findByUsername(String username) { return repo.findByUsername(username); }

    public List<User> listUsers() { return repo.findAll(); }
}
