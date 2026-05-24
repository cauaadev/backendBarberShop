package com.corteBrabo.barbershopApi.config;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Value("${app.admin.bootstrap-telefone:}")
    private String telefone;

    @Value("${app.admin.bootstrap-password:}")
    private String password;

    @Value("${app.admin.bootstrap-name:Admin}")
    private String name;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (telefone == null || telefone.isBlank() || password == null || password.isBlank()) {
            return;
        }

        if (userRepository.existsByTelefone(telefone)) {
            log.info("Bootstrap admin: telefone {} já existe, pulando.", telefone);
            return;
        }

        User admin = new User();
        admin.setName(name);
        admin.setTelefone(telefone);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(UserRole.ADM);
        userRepository.save(admin);

    }
}
