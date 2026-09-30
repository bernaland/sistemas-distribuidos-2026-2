package com.uelbosque.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {
    @NotBlank @jakarta.validation.constraints.Pattern(regexp="[0-9]{1,20}") private String cedula;
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula=cedula; }

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;

    @NotBlank(message = "La contrase単a es requerida")
    @Size(min = 6, max = 100, message = "La contrase単a debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es requerido")
    @Size(max = 100, message = "El nombre no debe superar 100 caracteres")
    private String name;

    @NotBlank(message = "El correo electr坦nico es requerido")
    @Email(message = "El formato de correo electr坦nico no es v叩lido")
    private String email;

    @jakarta.validation.constraints.Pattern(regexp="ROLE_(ADMIN|USER)(,ROLE_(ADMIN|USER))*")
    private String roles;

    public CreateUserRequest() {}

    public CreateUserRequest(String username, String password, String name, String email, String roles) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
}
