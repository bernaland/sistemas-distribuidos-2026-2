package uelbosque.userservice.controller;

import uelbosque.userservice.model.User;
import uelbosque.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = service.createUser(user);
        return ResponseEntity.created(URI.create("/api/users/" + created.getUsername())).body(created);
    }

    @GetMapping
    public List<User> list() { return service.listUsers(); }

    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        return service.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
