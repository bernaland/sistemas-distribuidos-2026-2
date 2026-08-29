package uelbosque.userservice.controller;

import jakarta.validation.Valid;
import uelbosque.userservice.model.User;
import uelbosque.userservice.model.dto.CreateUserRequest;
import uelbosque.userservice.model.dto.UserResponse;
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
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User created = service.createUser(request.getUsername(), request.getPassword(), request.getRoles());
        return ResponseEntity.created(URI.create("/api/users/" + created.getUsername())).body(toResponse(created));
    }

    @GetMapping
    public List<UserResponse> list() {
        return service.listUsers().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getByUsername(@PathVariable String username) {
        return service.findByUsername(username)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRoles(), user.isEnabled());
    }
}
