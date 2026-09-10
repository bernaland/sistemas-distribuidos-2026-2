package com.uelbosque.userservice.config;
import com.uelbosque.userservice.model.User;
import com.uelbosque.userservice.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class DataInitializer {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public DataInitializer(UserRepository users, PasswordEncoder encoder) { this.users=users; this.encoder=encoder; }
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (users.count()!=0) return;
        User admin=new User("admininicial",encoder.encode("admin123456"),"Administrador inicial","admin@uelbosque.edu.co","ROLE_ADMIN,ROLE_USER");
        admin.setCedula("0");
        users.save(admin);
    }
}
