package com.uelbosque.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @jakarta.validation.constraints.Size(min=4,max=50) private String username;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username=username; }

    @Size(max = 100, message = "El nombre no debe superar 100 caracteres")
    private String name;

    @Email(message = "El formato de correo electrónico no es válido")
    private String email;

    @Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @jakarta.validation.constraints.Pattern(regexp="ROLE_(ADMIN|USER)(,ROLE_(ADMIN|USER))*")
    private String roles;

    private Boolean enabled;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String name, String email, String password, String roles, Boolean enabled) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.enabled = enabled;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
