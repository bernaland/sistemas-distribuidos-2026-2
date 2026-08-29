package uelbosque.userservice.model.dto;

public class UserResponse {
    private final Long id;
    private final String username;
    private final String roles;
    private final boolean enabled;

    public UserResponse(Long id, String username, String roles, boolean enabled) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRoles() {
        return roles;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
