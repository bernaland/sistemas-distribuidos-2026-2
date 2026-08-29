package com.uelbosque.userservice.service;

import com.uelbosque.userservice.model.User;
import com.uelbosque.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) { this.repo = repo; }

    public User createUser(User user) { return repo.save(user); }

    public Optional<User> findByUsername(String username) { return repo.findByUsername(username); }

    public List<User> listUsers() { return repo.findAll(); }
}
